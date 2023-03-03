package com.charles445.rltweaker.entity.attribute;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public abstract class ExtendedRangedAttribute extends RangedAttribute implements AttributeInstanceFactory {

	public ExtendedRangedAttribute(IAttribute parentIn, String unlocalizedNameIn, double defaultValue,
			double minimumValueIn, double maximumValueIn) {
		super(parentIn, unlocalizedNameIn, defaultValue, minimumValueIn, maximumValueIn);
	}

}
