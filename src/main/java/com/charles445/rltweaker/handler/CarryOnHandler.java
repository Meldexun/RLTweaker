package com.charles445.rltweaker.handler;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.CarryOnReflect;
import com.charles445.rltweaker.reflect.QuarkReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.StackTraceUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CarryOnHandler
{
	boolean quarkLoaded = false;
	
	CarryOnReflect reflector;
	
	@Nullable
	QuarkReflect reflectorQuark;
	
	public CarryOnHandler()
	{
		try
		{
			reflector = new CarryOnReflect();
			
			//Quark
			this.quarkLoaded = Loader.isModLoaded(ModNames.QUARK);
			try
			{
				if(this.quarkLoaded)
					reflectorQuark = new QuarkReflect();
					
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Failed to setup Quark for Carryon");
				ErrorUtil.logSilent("CarryOn Quark Reflector Initialization");
			}
			
			if(ModConfig.server.carryon.tileEntityDropSafetyCheck)
				CompatUtil.wrapSpecificHandler("COItemDropped", COItemDropped::new, "tschipp.carryon.common.event.ItemEvents", "onItemDropped");
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup CarryOnHandler!", e);
			ErrorUtil.logSilent("CarryOn Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	@SuppressWarnings("deprecation")
	public void onPlaceEvent(BlockEvent.PlaceEvent event)
	{
		//Run on both client and server for synchronization purposes, probably unnecessary
		
		if(StackTraceUtil.stackTraceHasClassOfMethod("tschipp.carryon.common.item.ItemTile", "func_180614_a", "onItemUse"))
		{
			//Event is a call from CarryOn
			BlockSnapshot snapshot = event.getBlockSnapshot();
			ResourceLocation registry = snapshot.getRegistryName();
			World world = event.getWorld();
			EntityPlayer player = event.getPlayer();
			
			if(quarkLoaded && reflectorQuark != null && registry.getNamespace().equals(ModNames.QUARK))
			{
				handlePlacedQuarkBlock(event, snapshot, registry, world, player);
			}
		}
	}
	
	public void handlePlacedQuarkBlock(BlockEvent.PlaceEvent event, BlockSnapshot snapshot, ResourceLocation registry, World world, EntityPlayer player)
	{
		Block block = snapshot.getReplacedBlock().getBlock();
		BlockPos pos = snapshot.getPos();
		
		if(ModConfig.server.carryon.quarkChestFix && reflectorQuark.isBlockCustomChest(block))
		{
			try
			{
				//tile entity nbt doesn't get set until later, so obtaining type must be done via carryon
				ItemStack stack = event.getItemInHand();
				
				if(stack.hasTagCompound())
				{
					NBTTagCompound compound = stack.getTagCompound();
					
					if(compound.hasKey("tileData"))
					{
						NBTTagCompound data = compound.getCompoundTag("tileData");
						
						Object type = reflectorQuark.getChestTypeFromString(data.getString("type"));
						
						if(type != null)
						{
							int chestCount = 0;
							
							chestCount += getChestAdjacentCount(block, type, world, pos.north());
							chestCount += getChestAdjacentCount(block, type, world, pos.south());
							chestCount += getChestAdjacentCount(block, type, world, pos.east());
							chestCount += getChestAdjacentCount(block, type, world, pos.west());
							
							//Cancel if there are too many matching chests
							//Because quark chests don't fix themselves, this means any chestq	
							if(chestCount >= 1)
							{
								event.setCanceled(true);
							}
						}
					}
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				e.printStackTrace();
				ErrorUtil.logSilent("CarryOn Quark BlockCustomChest");
			}
		}
	}
	
	private int getChestAdjacentCount(Block block, Object type, World world, BlockPos adjacent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(checkChestMatch(block, type, world, adjacent))
			return reflectorQuark.isDoubleChest(block, world, adjacent, type) ? 2 : 1;
		
		return 0;
	}
	
	private boolean checkChestMatch(Block block, Object type, World world, BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (world.getBlockState(pos).getBlock() == block && reflectorQuark.getCustomChestType(block, world, pos) == type);
	}
	
	public class COItemDropped
	{
		private IEventListener handler;
		public COItemDropped(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent(priority = EventPriority.HIGH)
		public void onItemDropped(final EntityJoinWorldEvent event)
		{
			Vec3d relocate = itemDropRoutine(event);
			handler.invoke(event);
			if(relocate != null)
				event.getEntity().setPosition(relocate.x, relocate.y, relocate.z);
		}
		
		@Nullable
		/** Returns a position to set the entity back to after invocation, or null if it shouldn't **/
		public Vec3d itemDropRoutine(EntityJoinWorldEvent event)
		{
			if(!ModConfig.server.carryon.tileEntityDropSafetyCheck)
				return null;
			
			try
			{
				if(!(event.getEntity() instanceof EntityItem))
					return null;
	
				EntityItem entityItem = (EntityItem) event.getEntity();
				ItemStack stack = entityItem.getItem();
				Item item = stack.getItem();
				
				if(!reflector.isItemTile(item))
					return null;
				
				if(!reflector.hasTileData(stack))
					return null;
				
				//ItemTile and has tile data
				Vec3d vec = entityItem.getPositionVector();
				BlockPos pos = entityItem.getPosition();
				World world = event.getWorld();
				Block itemTileBlock = reflector.getItemTileBlock(stack);
				
				//Check first spot
				if(isChangeable(world, pos, itemTileBlock))
					return null;
				
				//Check all facing spots
				for(EnumFacing facing : EnumFacing.VALUES)
				{
					BlockPos newPos = pos.offset(facing);
					//Actually forcefully try to move it, as the default routine's changeable checks are not as robust
					if(trySetChangeable(world, newPos, itemTileBlock, entityItem))
					{
						RLTweaker.logger.debug("CarryOn tile intercepted for safety purposes... "+pos.toString());
						return vec;
					}
						
				}
				
				//CarryOn check is going to fail
				RLTweaker.logger.debug("CarryOn tile routine is about to fail, intercepting... "+pos.toString());
				for(int i = 0; i < 16; i++)
				{
					for (int j = 0; j < 128; j++)
					{
						for(int k = 0; k < 16; k++)
						{
							if(trySetChangeable(world, pos.add(i,j,k), itemTileBlock, entityItem))
								return vec;
							if(trySetChangeable(world, pos.add(i,-j,k), itemTileBlock, entityItem))
								return vec;
							if(trySetChangeable(world, pos.add(-i,j,k), itemTileBlock, entityItem))
								return vec;
							if(trySetChangeable(world, pos.add(-i,-j,k), itemTileBlock, entityItem))
								return vec;
							if(trySetChangeable(world, pos.add(i,j,-k), itemTileBlock, entityItem))
								return vec;
							if(trySetChangeable(world, pos.add(i,-j,-k), itemTileBlock, entityItem))
								return vec;
							if(trySetChangeable(world, pos.add(-i,j,-k), itemTileBlock, entityItem))
								return vec;
							if(trySetChangeable(world, pos.add(-i,-j,-k), itemTileBlock, entityItem))
								return vec;
						}
					}
				}
				
				RLTweaker.logger.error("CarryOn tile was unable to find safe place to be");
				ErrorUtil.logSilent("CarryOn COItemDropped No Safe Place");
				return null;
			}
			catch(Exception e)
			{
				ErrorUtil.logSilent("CarryOn COItemDropped Exception");
				//Default to original handler
				return null;
			}
		}
		
		private boolean trySetChangeable(World world, BlockPos newPos, Block itemTileBlock, EntityItem entityItem)
		{
			if(isChangeable(world, newPos, itemTileBlock))
			{
				RLTweaker.logger.debug("CarryOn tile is being set to: "+newPos.toString());
				entityItem.setPosition(newPos.getX(), newPos.getY(), newPos.getZ());
				return true;
			}
			
			return false;
		}
		
		private boolean isChangeable(World world, BlockPos pos, Block itemTileBlock)
		{
			return world.getBlockState(pos).getBlock().isReplaceable(world, pos) && itemTileBlock.canPlaceBlockAt(world, pos) && testQuarkChestQuickly(world, pos, itemTileBlock);
		}
		
		private boolean testQuarkChestQuickly(World world, BlockPos pos, Block itemTileBlock)
		{
			//Check whether quark is enabled, the block being placed is a quark chest, and that the position has no quark chests around it
			if(reflectorQuark != null && ModConfig.server.carryon.quarkChestFix && reflectorQuark.isBlockCustomChest(itemTileBlock))
			{
				//It is in fact a custom quark chest
				//Don't bother checking which one, just check the blocks
				if(world.getBlockState(pos.north()).getBlock() == itemTileBlock)
					return false;
				if(world.getBlockState(pos.east()).getBlock() == itemTileBlock)
					return false;
				if(world.getBlockState(pos.south()).getBlock() == itemTileBlock)
					return false;
				if(world.getBlockState(pos.west()).getBlock() == itemTileBlock)
					return false;
			}
			
			return true;
		}
	}
}
