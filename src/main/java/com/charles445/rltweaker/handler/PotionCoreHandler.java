package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import meldexun.reflectionutil.ReflectionField;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PotionCoreHandler
{
	boolean controllableJumpBoost = false;
	
	public PotionCoreHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
		try
		{
			if(ModConfig.server.potioncore.capJumpBoost)
			{
				CompatUtil.wrapSpecificHandler("PCJump", PCJump::new, "com.tmtravlr.potioncore.PotionCoreEventHandler", "onLivingJump");
			}
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup PotionCoreHandler!", e);
			ErrorUtil.logSilent("PotionCore Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	public class PCJump
	{
		private IEventListener handler;
		
		public PCJump(IEventListener handler)
		{
			this.handler = handler;
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		//Running late will override other jump events
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void onJump(LivingEvent.LivingJumpEvent event)
		{
			EntityLivingBase living = event.getEntityLiving();
			
			PotionEffect jumpEffect = living.getActivePotionEffect(MobEffects.JUMP_BOOST);
			
			//Exit on invalid jump boost
			if (jumpEffect != null && (jumpEffect.getAmplifier() < 0 || jumpEffect.getAmplifier() > 127))
			{
				if(living.world.isRemote)
				{
					living.getEntityData().setBoolean("Potion Core - Jump Boost Jumping", false);
				}
				
				return;
			}
			
			handler.invoke(event);
			
			//Clean up jump effect on client if necessary
			if(jumpEffect == null && living.world.isRemote)
			{
				living.getEntityData().setBoolean("Potion Core - Jump Boost Jumping", false);
			}
		}
		
		@SubscribeEvent
		public void onPlayerUpdate(PlayerTickEvent event)
		{
			if(event.phase == TickEvent.Phase.END)
				return;
			
			EntityPlayer player = event.player;
			
			if(player == null)
				return;
			
			World world = player.world;
			
			if(world == null)
				return;
			
			if(!world.isRemote)
				return;
			
			double jbh = event.player.getEntityData().getDouble("Potion Core - Jump Boost Height");
			
			if(jbh > 127)
			{
				ErrorUtil.logSilent("PotionCore Invalid Jump Boost Height");
				event.player.getEntityData().setDouble("Potion Core - Jump Boost Height", 1.75D);
			}
		}
		
		@SubscribeEvent
		public void potionApplicable(PotionApplicableEvent event)
		{
			EntityLivingBase living = event.getEntityLiving();
			PotionEffect newEffect = event.getPotionEffect();
			
			if(newEffect == null)
				return;
			
			if(!(living instanceof EntityPlayer))
				return;
			
			if(!living.world.isRemote)
				return;
			
			//Client only
			
			if(newEffect.getPotion() == MobEffects.JUMP_BOOST)
			{
				PotionEffect oldEffect = living.getActivePotionEffect(MobEffects.JUMP_BOOST);
				if(oldEffect == null)
					return;
				
				if(newEffect.getAmplifier() < 0 && oldEffect.getAmplifier() >= 0)
				{
					//Got sent a negative number from server, remove the potion effect to let it through
					living.removePotionEffect(MobEffects.JUMP_BOOST);
				}
			}
		}
	}

	public static final ReflectionField<IAttribute> DAMAGE_RESISTANCE = new ReflectionField<>(
			"com.tmtravlr.potioncore.PotionCoreAttributes", "DAMAGE_RESISTANCE", "DAMAGE_RESISTANCE");

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (!DAMAGE_RESISTANCE.isPresent()) {
			return;
		}
		if (!ModConfig.patches.patchPotionCoreResistance) {
			return;
		}
		if (event.getEntity().world.isRemote) {
			return;
		}
		if (!(event.getEntity() instanceof EntityLivingBase)) {
			return;
		}
		EntityLivingBase entity = (EntityLivingBase) event.getEntity();
		PotionEffect potionEffect = entity.getActivePotionEffect(MobEffects.RESISTANCE);
		if (potionEffect != null) {
			Potion potion = potionEffect.getPotion();
			potion.removeAttributesModifiersFromEntity(entity, entity.getAttributeMap(), potionEffect.getAmplifier());
			potion.applyAttributesModifiersToEntity(entity, entity.getAttributeMap(), potionEffect.getAmplifier());
		}
	}

}
