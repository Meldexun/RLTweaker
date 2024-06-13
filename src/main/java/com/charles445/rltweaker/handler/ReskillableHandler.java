package com.charles445.rltweaker.handler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.config.json.JsonDoubleBlockState;
import com.charles445.rltweaker.network.IServerMessageReceiver;
import com.charles445.rltweaker.network.MessageReskillableLockSkill;
import com.charles445.rltweaker.network.ServerMessageHandler;
import com.charles445.rltweaker.reflect.ReskillableReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.reflect.TypeToken;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ReskillableHandler
{
	private static final Path FILE_NAME = Paths.get("reskillableTransmutation.json");
	private static final Type TYPE = new TypeToken<Multimap<String, JsonDoubleBlockState>>() {}.getType();
	private ReskillableReflect reflector;
	private ManualSubscriber manualSubscriber;
	
	public ReskillableHandler()
	{
		try
		{
			reflector = new ReskillableReflect();
			manualSubscriber = new ManualSubscriber(reflector);
			
			if(ModConfig.server.reskillable.toggleableTraits)
			{
				ServerMessageHandler.registerMessage(MessageReskillableLockSkill.class, new LockSkillReceiver());
				manualSubscriber.subscribeToggleableTraits();
			}
			
			MinecraftForge.EVENT_BUS.register(this);
		}
	
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup ReskillableHandler!", e);
			ErrorUtil.logSilent("Reskillable Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}

	public static void loadConfig() {
		if (!Loader.isModLoaded(ModNames.RESKILLABLE)) {
			return;
		}
		if (!ModConfig.server.reskillable.enabled) {
			return;
		}
		if (!ModConfig.server.reskillable.customTransmutation) {
			return;
		}

		Multimap<String, JsonDoubleBlockState> transmutations;
		try {
			transmutations = JsonConfig.readJson(RLTweaker.jsonDirectory.resolve(FILE_NAME), TYPE, getDefaults());
		} catch (IOException e) {
			RLTweaker.logger.error("Failed to load reskillable transmutations config", e);
			return;
		}

		Object reskillableHandler = RLTweaker.handlers.get(ModNames.RESKILLABLE);
		if (reskillableHandler instanceof ReskillableHandler) {
			((ReskillableHandler) reskillableHandler).registerTransmutations(transmutations);
		}
	}

	private static Multimap<String, JsonDoubleBlockState> getDefaults() {
		return ImmutableMultimap.of("minecraft:stick", JsonDoubleBlockState.AIR);
	}

	public void registerTransmutations(Multimap<String, JsonDoubleBlockState> transmutations) {
		transmutations.asMap().forEach((k, v) -> {
			Item activator = ForgeRegistries.ITEMS.getValue(new ResourceLocation(k));
			if (activator == null) {
				RLTweaker.logger.warn("Skipping unregistered item in registerTransmutations: " + k);
				return;
			}

			for (JsonDoubleBlockState jdbs : v) {
				try {
					reflector.addEntryToReagent(activator, jdbs.input.getAsBlockState(), jdbs.output.getAsBlockState());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					RLTweaker.logger.error("Invocation error in registerTransmutations", e);
					ErrorUtil.logSilent("Reskillable registerTransmutations Invoke Failure");
				}
			}
		});
	}

	public class LockSkillReceiver implements IServerMessageReceiver
	{
		@Override
		public void receiveMessage(IMessage msgIn, EntityPlayer player) 
		{
			try
			{
				if(msgIn instanceof MessageReskillableLockSkill)
				{
					MessageReskillableLockSkill message = (MessageReskillableLockSkill)msgIn;
					
					IForgeRegistry<?> SKILLS = reflector.getSkillsRegistry();
					
					IForgeRegistry<?> UNLOCKABLES = reflector.getUnlockablesRegistry();
					
					if(!SKILLS.containsKey(message.skill))
						return;
					
					if(!UNLOCKABLES.containsKey(message.unlockable))
						return;
					
					Object skill = SKILLS.getValue(message.skill);
					Object unlockable = UNLOCKABLES.getValue(message.unlockable);
					Object playerData = reflector.getPlayerData(player);
					Object skillInfo = reflector.getSkillInfo(playerData, skill);
					boolean unlocked = reflector.isUnlocked(skillInfo, unlockable);
					
					if(unlocked)
					{
						//Original mod's cmd command has this swapped
						if(reflector.postLockUnlockableEventPre(player, unlockable))
							return;
						
						reflector.lockPlayerSkill(skillInfo, unlockable, player);
						reflector.saveAndSyncPlayerData(playerData);
						
						reflector.postLockUnlockableEventPost(player, unlockable);
					}
					else
					{
						ErrorUtil.logSilent("Reskillable LockSkillReceiver Unlock Asked Lock");
					}
				}
			}
			catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e)
			{
				e.printStackTrace();
				ErrorUtil.logSilent("Reskillable LockSkillReceiver Invoke");
			}
		}
	}
	
	public static class ManualSubscriber
	{
		private ReskillableReflect reflector;
		
		//ElenaiDodge caching
		private boolean canReflectOldElenai = false;
		private Class c_oldElenai_WalljumpProvider;
		private Field f_oldElenai_WalljumpProvider_WALLJUMP_CAP;
		private Class c_oldElenai_IWalljump;
		private Method m_oldElenai_IWalljump_set;

		private Class c_oldElenai_LedgegrabProvider;
		private Field f_oldElenai_LedgegrabProvider_LEDGEGRAB_CAP;
		private Class c_oldElenai_ILedgegrab;
		private Method m_oldElenai_ILedgegrab_set;
		
		private Class c_oldElenai_DodgeProvider;
		private Field f_oldElenai_DodgeProvider_DODGE_CAP;
		private Class c_oldElenai_IDodge;
		private Method m_oldElenai_IDodge_set;
		
		public ManualSubscriber(ReskillableReflect reflector)
		{
			this.reflector = reflector;
			
			//Old ElenaiDodge
			try
			{
				c_oldElenai_WalljumpProvider = Class.forName("com.elenai.elenaidodge.modpacks.capability.walljump.WalljumpProvider");
				f_oldElenai_WalljumpProvider_WALLJUMP_CAP = ReflectUtil.findField(c_oldElenai_WalljumpProvider, "WALLJUMP_CAP");
				c_oldElenai_IWalljump = Class.forName("com.elenai.elenaidodge.modpacks.capability.walljump.IWalljump");
				m_oldElenai_IWalljump_set = ReflectUtil.findMethod(c_oldElenai_IWalljump, "set");
				
				c_oldElenai_LedgegrabProvider = Class.forName("com.elenai.elenaidodge.modpacks.capability.ledgegrab.LedgegrabProvider");
				f_oldElenai_LedgegrabProvider_LEDGEGRAB_CAP = ReflectUtil.findField(c_oldElenai_LedgegrabProvider, "LEDGEGRAB_CAP");
				c_oldElenai_ILedgegrab = Class.forName("com.elenai.elenaidodge.modpacks.capability.ledgegrab.ILedgegrab");
				m_oldElenai_ILedgegrab_set = ReflectUtil.findMethod(c_oldElenai_ILedgegrab, "set");
				
				c_oldElenai_DodgeProvider = Class.forName("com.elenai.elenaidodge.modpacks.capability.trait.TraitProvider");
				f_oldElenai_DodgeProvider_DODGE_CAP = ReflectUtil.findField(c_oldElenai_DodgeProvider, "TRAIT_CAP");
				c_oldElenai_IDodge = Class.forName("com.elenai.elenaidodge.modpacks.capability.trait.ITrait");
				m_oldElenai_IDodge_set = ReflectUtil.findMethod(c_oldElenai_IDodge, "set");
				
				canReflectOldElenai = true;
			}
			catch(Exception e)
			{
				//Ignore
			}
			
		}
		
		private void subscribeToggleableTraits() throws Exception
		{
			CompatUtil.subscribeEventManually(reflector.c_LockUnlockableEvent$Post, this,ReflectUtil.findMethod(this.getClass(), "onToggleableTraitLockedPost"));
		}
		
		@SubscribeEvent
		public void onToggleableTraitLockedPost(Object uncastedEvent)
		{
			if(uncastedEvent instanceof PlayerEvent)
			{
				PlayerEvent event = (PlayerEvent)uncastedEvent;
				
				try
				{
					String lockedName = reflector.getUnlockableName(reflector.getUnlockableFromLockedEvent(event));
					if(lockedName != null && event.getEntityPlayer() != null)
						synchronizeUnlockable(event.getEntityPlayer(), lockedName);
				}
				catch(Exception e)
				{
					ErrorUtil.logSilent("Reskillable Toggleable Trait Post Handler");
				}
			}
		}
		
		public void synchronizeUnlockable(EntityPlayer player, String name)
		{
			//RLTweaker.logger.debug("Locked skill: "+name);
			switch(name)
			{
				case "elenaidodge.dodgetrait":
					clearOldElenaiDodgeTrait(player);
					break;
				case "elenaidodge.wallgrabtrait":
					clearOldElenaiLedgeGrabTrait(player);
					break;
				case "elenaidodge.walljumptrait":
					clearOldElenaiWallJumpTrait(player);
					break;
				default:
					break;
			}
		}
		
		private void clearOldElenaiDodgeTrait(EntityPlayer player)
		{
			if(!canReflectOldElenai)
				return;
			
			try
			{
				Object provided = player.getCapability((Capability<?>) f_oldElenai_DodgeProvider_DODGE_CAP.get(null), null);
				m_oldElenai_IDodge_set.invoke(provided, false);
				
			}
			catch (Exception e)
			{
				ErrorUtil.logSilent("Reskillable Toggleable Trait Old Elenai Dodge Dodge");
			}
		}
		
		private void clearOldElenaiLedgeGrabTrait(EntityPlayer player)
		{
			if(!canReflectOldElenai)
				return;
			
			try
			{
				Object provided = player.getCapability((Capability<?>) f_oldElenai_LedgegrabProvider_LEDGEGRAB_CAP.get(null), null);
				m_oldElenai_ILedgegrab_set.invoke(provided, false);
			}
			catch (Exception e)
			{
				ErrorUtil.logSilent("Reskillable Toggleable Trait Old Elenai Dodge Wall Grab");
			}
		}
		
		private void clearOldElenaiWallJumpTrait(EntityPlayer player)
		{
			if(!canReflectOldElenai)
				return;
			
			try
			{
				Object provided = player.getCapability((Capability<?>) f_oldElenai_WalljumpProvider_WALLJUMP_CAP.get(null), null);
				m_oldElenai_IWalljump_set.invoke(provided, false);
			}
			catch (Exception e)
			{
				ErrorUtil.logSilent("Reskillable Toggleable Trait Old Elenai Dodge Wall Jump");
			}
		}
	}
}
