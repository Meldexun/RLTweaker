package com.charles445.rltweaker.hook;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

public class HookSRP {

	private static Class<?> c_EntityAIInfectedSearch;
	static {
		try {
			c_EntityAIInfectedSearch = Class.forName("com.dhanantry.scapeandrunparasites.entity.ai.EntityAIInfectedSearch");
		} catch (ClassNotFoundException e) {
			// ignore
		}
	}

	public static boolean hasNoInfectedSearchAI(EntityLiving entity) {
		for (EntityAITaskEntry task : entity.tasks.taskEntries)
			if (c_EntityAIInfectedSearch.isInstance(task.action))
				return false;
		return true;
	}

}
