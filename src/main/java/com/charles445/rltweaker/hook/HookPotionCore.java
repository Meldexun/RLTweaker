package com.charles445.rltweaker.hook;

import java.util.List;

import com.charles445.rltweaker.config.ModConfig;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class HookPotionCore
{
	public interface Incurable
	{
		boolean isIncurable();
		
		void setIncurable(boolean incurable);
	}
	
	private static final ResourceLocation POTION_SICKNESS = new ResourceLocation("potioncore", "potion_sickness");
	
	public static void addIncurableEffect(EntityLivingBase entity, List<Potion> possibleEffects)
	{
		Potion potion = possibleEffects.get(entity.getRNG().nextInt(possibleEffects.size()));
		PotionEffect potionEffect = potion.isInstant() ? new PotionEffect(potion, 1) : new PotionEffect(potion, 1200);
		if(ModConfig.server.potioncore.incurablePotionSickness)
		{
			((Incurable)potionEffect).setIncurable(true);
		}
		entity.addPotionEffect(potionEffect);
	}
	
	public static boolean isCurable(PotionEffect potionEffect)
	{
		ResourceLocation registryName = potionEffect.getPotion().getRegistryName();
		if(ModConfig.server.potioncore.incurablePotionSickness && registryName.equals(POTION_SICKNESS))
		{
			return false;
		}
		if(((Incurable)potionEffect).isIncurable())
		{
			return false;
		}
		return !ModConfig.server.potioncore.incurablePotionEffectsImpl.contains(registryName);
	}
	
	public static boolean isCureDisabled(EntityLivingBase entity)
	{
		return entity.getActivePotionEffects().stream()
				.map(PotionEffect::getPotion)
				.map(Potion::getRegistryName)
				.anyMatch(ModConfig.server.potioncore.cureDisablingPotionEffectsImpl::contains);
	}
}
