package com.charles445.rltweaker.entity.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public class InvestigateAIConfig {

	public int priority = 0;
	public List<Entry> entries = new ArrayList<>();

	public static class Entry {

		public boolean executeOnAttacked = true;
		public boolean executeOnCalled = true;
		public float healthThreshold = 1.0F;
		public float executionChance = 1.0F;
		public float movementSpeed = 1.0F;
		public float verticalOffsetBase = 0.0F;
		public float verticalOffsetScale = 0.0F;
		public float verticalOffsetMax = 0.0F;
		public float horizontalOffsetBase = 0.0F;
		public float horizontalOffsetScale = 0.0F;
		public float horizontalOffsetMax = 0.0F;
		public List<CallForHelpEntry> callForHelpEntries = new ArrayList<>();

		public static class CallForHelpEntry {

			public Set<ResourceLocation> entityNames = new HashSet<>();
			public float verticalRange = 0.0F;
			public float horizontalRange = 0.0F;
			public boolean requiresVision = true;
			public boolean ignoreParentInCombat = true;
			public boolean ignoreParentChanceTestFailed = true;

		}

	}

}
