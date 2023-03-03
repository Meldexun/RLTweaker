package com.charles445.rltweaker.entity.attribute;

import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

public class InversedAttributeInstance extends ModifiableAttributeInstance {

	public InversedAttributeInstance(AbstractAttributeMap attributeMapIn, IAttribute genericAttributeIn) {
		super(attributeMapIn, genericAttributeIn);
	}

	@Override
	public double computeValue() {
		double d0 = this.getBaseValue();

		for (AttributeModifier attributemodifier : this.getAppliedModifiers(0)) {
			d0 -= attributemodifier.getAmount();
		}

		double d1 = d0;

		for (AttributeModifier attributemodifier1 : this.getAppliedModifiers(1)) {
			d1 -= d0 * attributemodifier1.getAmount();
		}

		for (AttributeModifier attributemodifier2 : this.getAppliedModifiers(2)) {
			d1 *= 1.0D - attributemodifier2.getAmount();
		}

		return this.genericAttribute.clampValue(d1);
	}

}
