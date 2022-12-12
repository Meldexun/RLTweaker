package com.charles445.rltweaker.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.capability.RLCapabilities;
import com.charles445.rltweaker.capability.TweakerProvider;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.network.MessageSendVersion;
import com.charles445.rltweaker.network.MessageUpdateAttackYaw;
import com.charles445.rltweaker.network.MessageUpdateDismountStatus;
import com.charles445.rltweaker.network.MessageUpdateEntityMovement;
import com.charles445.rltweaker.network.NetworkHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.network.TaskScheduler;

import meldexun.reflectionutil.ReflectionMethod;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MinecraftHandler
{
	private static final ReflectionMethod<Void> WRITE_PLAYER_DATA = new ReflectionMethod<>(PlayerList.class, "func_72391_b", "writePlayerData", EntityPlayerMP.class);
	public static Map<String, IContainerValidator> containerValidators = new ConcurrentHashMap<>();
	
	public Map<UUID, BlockPos> containerEnforcedPlayers = new ConcurrentHashMap<>();
	
	//public Set<UUID> teleportingEntities = ConcurrentHashMap.newKeySet();
	
	public MinecraftHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event)
	{
		if(event.getEntity() instanceof EntityPlayer)
		{
			
			EntityPlayer player = (EntityPlayer)event.getEntity();
			World world = player.world;
			
			if(!world.isRemote && player.openContainer != null && !player.openContainer.equals(player.inventoryContainer))
			{
				//Server side
				
				UUID playerId = player.getUUID(player.getGameProfile());
				
				//Enforce Validator
				IContainerValidator validator = containerValidators.get(player.openContainer.getClass().getName());
				if(validator != null && !validator.isValid(player.openContainer))
				{
					containerEnforcedPlayers.remove(playerId);
					player.closeScreen();
				}
				
				//Enforce container distance
				BlockPos usedPosition = containerEnforcedPlayers.get(playerId);
				if(usedPosition != null && usedPosition.distanceSq(player.getPosition()) > 65.0d)
				{
					containerEnforcedPlayers.remove(playerId);
					player.closeScreen();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote)
			return;
		
		if(ModConfig.server.minecraft.allZombiesBreakDoors && event.getEntity() instanceof EntityZombie)
		{
			((EntityZombie)event.getEntity()).setBreakDoorsAItask(true);
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Save event)
	{
		//Cleans up after the world gets saved, not before
		//So a dimension must be loaded when saved for this to work
		
		//This means cleanup will run erroneously when unloading a world, and on server start
		
		World world = event.getWorld();
		
		if(world.isRemote)
			return;
		
		//TODO other worldgen
		//Uf you enter an area for the first time, leave, world saves twice, come back, the area will be broken
		//This is very uncommon, but it's still a possibility
		//They seem to regenerate when the game is restarted, which is very peculiar, but could also be incredibly helpful
		//It may be possible to run these cleanups on server stopping, or something similar
		//
		//Nobody uses Mineshafts, though, so that cleaner gets to stay for now
		
		
		if(ModConfig.server.minecraft.cleanupMineshaftWorldgenFiles)
		{
			//TODO fix checkIsLoaded to handle invalid structures
			cleanMapGenStructureData(world, "Mineshaft", false, true);
			//cleanMapGenStructureData(world, "Village", false, true);
			//cleanMapGenStructureData(world, "Fortress", false, true);
		}
	}
	
	private void cleanMapGenStructureData(World world, String dataName, boolean checkIsUngenerated, boolean checkIsLoaded)
	{
		long nanoA = System.nanoTime();
		MapGenStructureData structureData = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, dataName);
		if(structureData == null)
			return;
		
		NBTTagCompound structureDataCompound = structureData.getTagCompound();

		List<String> toRemove = new ArrayList<>();
		
		long cleanupCount = 0L;
		
		for (String s : structureDataCompound.getKeySet())
		{
			toRemove.add(s);
			
			//Load check routine removed
			/*
			NBTBase structureNestedBase = structureDataCompound.getTag(s);
			 if (structureNestedBase.getId() == 10)
			 {
				 NBTTagCompound structureNestedCompound = (NBTTagCompound)structureNestedBase;
				 if (structureNestedCompound.hasKey("BB"))
				 {
					 boolean isLoaded = false;
					 boolean shouldRemove = false;
					 StructureBoundingBox sbb = new StructureBoundingBox(structureNestedCompound.getIntArray("BB"));
					 List<ChunkPos> containedChunks = containedChunks(sbb.minX,sbb.minZ, sbb.maxX, sbb.maxZ);
					 
					 if(checkIsLoaded)
					 {
						 for(ChunkPos cPos : containedChunks)
						 {
							 if(((ChunkProviderServer)world.getChunkProvider()).chunkExists(cPos.x, cPos.z))
							 {
								 isLoaded = true;
								 break;
							 }
						 }
						 
						 if(isLoaded)
						 {
							 //Skip this removal as it's currently loaded
							 containedChunks.clear();
							 continue;
						 }
					 }
					 
					 if(checkIsUngenerated)
					 {
						 for(ChunkPos cPos : containedChunks)
						 {
							 if(!world.isChunkGeneratedAt(cPos.x, cPos.z))
							 {
								 shouldRemove = true;
								 break;
							 }
						 }
					 }
					 else
					 {
						 shouldRemove = true;
					 }
					 
					 containedChunks.clear();
					 
					 if(shouldRemove)
						 toRemove.add(s);
				 }
			 }
			 
			 */
		}
		
		for(String remove : toRemove)
		{
			structureDataCompound.removeTag(remove);
		}
		
		if(toRemove.size() > 0)
			structureData.markDirty();
		
		long nanoB = System.nanoTime();
		
		//RLTweaker.logger.debug(""+dataName+" cleanup took nanoseconds: "+(nanoB - nanoA));
		//RLTweaker.logger.debug(""+dataName+" cleanup cleaned entries: "+toRemove.size());
		
		toRemove.clear();
	}
	
	private List<ChunkPos> containedChunks(int xIn1, int zIn1, int xIn2, int zIn2)
	{
		int xStart = xIn1 >> 4;
		int zStart = zIn1 >> 4;
		int xEnd = xIn2 >> 4;
		int zEnd = zIn2 >> 4;
		
		List<ChunkPos> chunks = new ArrayList<ChunkPos>();
		
		for (int i = xStart; i <= xEnd; ++i)
		{
			for (int j = zStart; j <= zEnd; ++j)
			{
				chunks.add(new ChunkPos(i,j));
			}
		}
		
		return chunks;
	}
	
	//Capabilities
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onAttachCapability(AttachCapabilitiesEvent event)
	{
		//Find player
		if(event.getObject() instanceof EntityPlayer)
		{
			//Attach capabilities
			
			//Attach tweaker
			event.addCapability(new ResourceLocation(RLTweaker.MODID, RLCapabilities.TWEAKER_IDENTIFIER), new TweakerProvider(RLCapabilities.TWEAKER));
			
		}
	}
	
	//TaskScheduler processing
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientScheduled(TickEvent.ClientTickEvent event)
	{
		TaskScheduler.processClientTick(event);
	}
	
	//Task Creation
	
	//TODO guarantee that this is the LAST handler registered
	//Nobody currently uses a lower priority for EntityMountEvent in rlcraft
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityDismount(EntityMountEvent event)
	{
		World world = event.getWorldObj();
		if(world.isRemote)
			return;
		
		
		if(ModConfig.server.minecraft.playerDismountSync && event.isDismounting() && event.getEntityMounting() instanceof EntityPlayerMP && event.getEntityBeingMounted()!=null)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntityMounting();
			
			if(NetworkHandler.isVersionAtLeast(0, 3, player))
			{
				//Send a packet to the player of the player to dismount then and there
				MessageUpdateDismountStatus message = new MessageUpdateDismountStatus();
				PacketHandler.instance.sendTo(message, player);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onClientPlayerJoinWorldEvent(EntityJoinWorldEvent event)
	{
		if(!event.getWorld().isRemote)
			return;
		
		//Client logical
		
		if(event.getEntity() instanceof EntityPlayer)
		{
			//EntityPlayer
			
			EntityPlayer localPlayer = RLTweaker.proxy.getClientMinecraftPlayer();
			
			if(localPlayer==null)
				return;
			
			//Client logical
			//Client physical
			
			EntityPlayer entPlayer = (EntityPlayer)event.getEntity();
			
			
			if(entPlayer.getGameProfile().getId().equals(localPlayer.getGameProfile().getId()))
			{
				//Player is you
				if(NetworkHandler.serverHasVersioning)
				{
					//Create a message to the server to let them know your version
					RLTweaker.logger.debug("Sending MessageSendVersion to server");
					PacketHandler.instance.sendToServer(new MessageSendVersion(RLTweaker.VERSION_DELIMITER));
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerLoggedOutEvent(PlayerLoggedOutEvent event)
	{
		if(event.player!=null)
		{
			NetworkHandler.removeClient(event.player.getGameProfile().getId());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorldLowest(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityArrow)
		{
			handleArrowJoinWorld((EntityArrow)event.getEntity());
		}
		
		if(event.getEntity() instanceof EntityPotion)
		{
			handlePotionJoinWorld((EntityPotion)event.getEntity());
		}
	}
	
	private void handlePotionJoinWorld(EntityPotion potion)
	{
		if(ModConfig.server.minecraft.witchPotionReplacements && potion.getThrower() instanceof EntityWitch)
		{
			ItemStack stack = potion.getPotion();
			
			//Switch based on type
			PotionType type = PotionUtils.getPotionFromItem(stack);
			
			if(type == PotionTypes.HARMING)
			{
				swapPotionFromList(potion, stack, ModConfig.server.minecraft.witchHarmingReplacements);
			}
			else if(type == PotionTypes.SLOWNESS)
			{
				swapPotionFromList(potion, stack, ModConfig.server.minecraft.witchSlownessReplacements);
			}
			else if(type == PotionTypes.POISON)
			{
				swapPotionFromList(potion, stack, ModConfig.server.minecraft.witchPoisonReplacements);
			}
			else if(type == PotionTypes.WEAKNESS)
			{
				swapPotionFromList(potion, stack, ModConfig.server.minecraft.witchWeaknessReplacements);
			}
		}
	}
	
	private void swapPotionFromList(EntityPotion potion, ItemStack stack, String[] potionNames)
	{
		if(potionNames.length > 0)
		{
			PotionType toSwap = PotionType.getPotionTypeForName(potionNames[potion.world.rand.nextInt(potionNames.length)]);
			
			if(toSwap!=null)
			{
				PotionUtils.addPotionToItemStack(stack, toSwap);
				potion.setItem(stack);
			}
		}
	}
	
	private void handleArrowJoinWorld(EntityArrow arrow)
	{
		World world = arrow.getEntityWorld();
		if(!world.isRemote && ModConfig.server.minecraft.playerArrowSync && arrow.shootingEntity instanceof EntityPlayer)
		{
			double posX = arrow.posX;
			double posY = arrow.posY;
			double posZ = arrow.posZ;
			MessageUpdateEntityMovement message = new MessageUpdateEntityMovement(arrow);
			
			for (EntityPlayer nearbyPlayer : world.playerEntities)
			{
				if (nearbyPlayer.getDistanceSq(posX, posY, posZ) < 576)
				{
					if(NetworkHandler.isVersionAtLeast(0, 2, nearbyPlayer))
					{
						PacketHandler.instance.sendTo(message, (EntityPlayerMP) nearbyPlayer);
					}
				}
			}
			
			//MessageUpdateEntityMovement message = new MessageUpdateEntityMovement(arrow);
			//PacketHandler.instance.sendToAllAround(message, new TargetPoint(world.provider.getDimension(), arrow.posX, arrow.posY, arrow.posZ, 24));
		}
	}
	
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event)
	{
		if(ModConfig.server.minecraft.blacksmithChestTweak)
		{
			if(event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH))
			{
				LootTable table = event.getTable();
				if(table!=null)
				{
					//The pool needed is called 'main'
					LootPool pool = table.getPool("main");
					
					if(pool != null)
					{
						//RLTweaker.logger.info("Removing loot from village blacksmiths...");
						pool.removeEntry("minecraft:diamond");
						//pool.removeEntry("minecraft:iron_ingot");
						//pool.removeEntry("minecraft:gold_ingot");
						//pool.removeEntry("minecraft:bread");
						//pool.removeEntry("minecraft:apple");
						pool.removeEntry("minecraft:iron_pickaxe");
						pool.removeEntry("minecraft:iron_sword");
						pool.removeEntry("minecraft:iron_chestplate");
						pool.removeEntry("minecraft:iron_helmet");
						pool.removeEntry("minecraft:iron_leggings");
						pool.removeEntry("minecraft:iron_boots");
						pool.removeEntry("minecraft:obsidian");
						//pool.removeEntry("minecraft:sapling");
						//pool.removeEntry("minecraft:saddle");
						pool.removeEntry("minecraft:iron_horse_armor");
						pool.removeEntry("minecraft:golden_horse_armor");
						pool.removeEntry("minecraft:diamond_horse_armor");
					}
				}
				
			}
		}
	}
	
	@SubscribeEvent
	public void onKnockback(LivingKnockBackEvent event)
	{
		if(!ModConfig.server.minecraft.damageTilt)
			return;
		
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if(player.world.isRemote)
				return;
			
			//Server Side
			if(NetworkHandler.isVersionAtLeast(0, 4, player))
				PacketHandler.instance.sendTo(new MessageUpdateAttackYaw(player), (EntityPlayerMP) player);
		}
	}

	@SubscribeEvent
	public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event)
	{
		if(event.getSound() == SoundEvents.ENTITY_LIGHTNING_THUNDER && ModConfig.server.minecraft.lightningSoundChunkDistance != 10000.0d)
		{
			event.setVolume((float)ModConfig.server.minecraft.lightningSoundChunkDistance);
		}
	}

	@SubscribeEvent
	public void onContainerOpen(PlayerContainerEvent.Open event)
	{
		if(event.getEntityPlayer() == null)
			return;
		
		if(event.getContainer() == null)
			return;
		
		EntityPlayer player = event.getEntityPlayer();
		UUID playerId = player.getUUID(player.getGameProfile());
		
		String containerClazz = event.getContainer().getClass().getName();
		
		String[] distanceClazzes = ModConfig.server.minecraft.containerDistanceClasses;
		
		for(int i = 0; i < distanceClazzes.length; i++)
		{
			if(containerClazz.equals(distanceClazzes[i]))
			{
				containerEnforcedPlayers.put(playerId, player.getPosition());
				return;
			}
		}
		
		//If this portion was reached, the container is not enforced
		//Might as well remove any enforcement still pending if there was an error with the close event
		containerEnforcedPlayers.remove(playerId);
	}

	@SubscribeEvent
	public void onContainerClosed(PlayerContainerEvent.Close event)
	{
		if(event.getEntityPlayer() == null)
			return;
		
		if(event.getContainer() == null)
			return;
		
		EntityPlayer player = event.getEntityPlayer();
		
		containerEnforcedPlayers.remove(player.getUUID(player.getGameProfile()));
	}
	
	@SubscribeEvent
	public void onLightningStruckEntity(EntityStruckByLightningEvent event)
	{
		if(!ModConfig.server.minecraft.lightningDestroysItems && event.getEntity() instanceof EntityItem)
			event.setCanceled(true);
	}

	/*
	//Run on LOWEST priority to try and run after other handlers
	//That way if a handler cancels, it won't reach this event and cause trouble later
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTravelToDimension(EntityTravelToDimensionEvent event)
	{
		Entity entity = event.getEntity();
		if(entity == null || entity instanceof EntityPlayer)
			return;
		
		this.teleportingEntities.add(entity.getUniqueID());
	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.ServerTickEvent event)
	{
		if(event.phase == TickEvent.Phase.END)
			return;
		
		if(this.teleportingEntities.size() > 0)
			this.teleportingEntities.clear();
	}
	
	@SubscribeEvent
	public void onTeleportingDeath(LivingDeathEvent event)
	{
		EntityLivingBase living = event.getEntityLiving();
		if(living == null)
			return;
		
		if(this.teleportingEntities.contains(living.getUniqueID()))
		{
			DebugUtil.messageAll("MATCHING Teleporter Death: "+living.getUniqueID());
			RLTweaker.logger.debug("MATCHING Teleporter Death: "+living.getUniqueID());
		}
	}
	*/
	
	public static interface IContainerValidator
	{
		public boolean isValid(Container container);
	}

	@SubscribeEvent
	public void fixDropDisconnectLagDupe(PlayerLoggedOutEvent event) {
		if (!ModConfig.server.minecraft.fixDropDisconnectLagDupe)
			return;
		if (event.player.world.isRemote)
			return;
		MinecraftServer server = event.player.getServer();
		server.futureTaskQueue.add(new FutureTask<>(() -> {
			WRITE_PLAYER_DATA.invoke(server.getPlayerList(), (EntityPlayerMP) event.player);
			return null;
		}));
	}
}
