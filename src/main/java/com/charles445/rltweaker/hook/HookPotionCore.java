package com.charles445.rltweaker.hook;

import java.util.List;

import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.entity.attribute.ExtendedRangedAttribute;
import com.charles445.rltweaker.entity.attribute.InversedAttributeInstance;

import meldexun.reflectionutil.ReflectionField;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
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
	private static final ResourceLocation TIPSY = new ResourceLocation("rustic", "tipsy");
	
	public static void addPotionSicknessEffect(EntityLivingBase entity, List<Potion> possibleEffects)
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
		if(ModConfig.server.potioncore.incurableTipsy && registryName.equals(TIPSY))
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
	
	public static PotionEffect onAddTipsyEffect(PotionEffect potionEffect)
	{
		if(ModConfig.server.potioncore.incurableTipsy)
		{
			((Incurable)potionEffect).setIncurable(true);
		}
		return potionEffect;
	}

	public static final ReflectionField<IAttribute> DAMAGE_RESISTANCE = new ReflectionField<>(
			"com.tmtravlr.potioncore.PotionCoreAttributes", "DAMAGE_RESISTANCE", "DAMAGE_RESISTANCE");
	private static float originalDamage;

	public static RangedAttribute resistance_createAttribute(RangedAttribute originalAttribute) {
		if (!ModConfig.server.potioncore.alternativeResistanceMode) {
			return originalAttribute;
		}
		// override resistance attribute when alternativeResistanceMode is enabled
		return new ExtendedRangedAttribute(null, originalAttribute.getName(), originalAttribute.getDefaultValue(), 0.0D,
				Double.MAX_VALUE) {

			@Override
			public IAttributeInstance createInstance(AbstractAttributeMap attributeMap, IAttribute attribute) {
				return new InversedAttributeInstance(attributeMap, attribute);
			}

		};
	}

	public static Potion resistance_registerPotionAttributeModifier(Potion potion, IAttribute attribute,
			String uniqueId, double ammount, int operation) {
		// override registration of attribute modifier
		return potion.registerPotionAttributeModifier(attribute, uniqueId, ModConfig.server.potioncore.resistanceAmount,
				ModConfig.server.potioncore.resistanceOperation);
	}

	public static double getAdjustedDamageResistanceAttribute(EntityLivingBase entity) {
		// skip potion core damage resistance handler
		return 0.0D;
	}

	public static void preResistancePotionCalculation(float damage) {
		originalDamage = damage;
	}

	public static float postResistancePotionCalculation(EntityLivingBase entity, float damage) {
		if (!DAMAGE_RESISTANCE.isPresent()) {
			return damage;
		}
		IAttributeInstance attribute = entity.getEntityAttribute(DAMAGE_RESISTANCE.get(null));
		if (ModConfig.server.potioncore.alternativeResistanceMode) {
			return (float) (originalDamage * attribute.getAttributeValue());
		} else {
			return (float) (originalDamage * (1.0D - (attribute.getAttributeValue() - 1.0D)));
		}
	}

}
