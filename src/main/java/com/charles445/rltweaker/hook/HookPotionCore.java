package com.charles445.rltweaker.hook;

import java.util.List;

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
		((Incurable)potionEffect).setIncurable(true);
		entity.addPotionEffect(potionEffect);
	}
	
	public static boolean isCurable(PotionEffect potionEffect)
	{
		return !potionEffect.getPotion().getRegistryName().equals(POTION_SICKNESS) && !((Incurable)potionEffect).isIncurable();
	}
}
