package com.charles445.rltweaker.entity.ai;

import java.util.HashSet;
import java.util.Set;

public class InvestigateAIConfig {

	private int priority = 0;
	private float healthThreshold = 1.0F;
	private float executionChance = 1.0F;
	private float movementSpeed = 1.0F;
	private float verticalOffsetBase = 0.0F;
	private float verticalOffsetScale = 0.0F;
	private float verticalOffsetMax = 0.0F;
	private float horizontalOffsetBase = 0.0F;
	private float horizontalOffsetScale = 0.0F;
	private float horizontalOffsetMax = 0.0F;
	private Set<CallForHelpEntry> callForHelpEntries = new HashSet<>();

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public float getHealthThreshold() {
		return healthThreshold;
	}

	public void setHealthThreshold(float healthThreshold) {
		this.healthThreshold = healthThreshold;
	}

	public float getExecutionChance() {
		return executionChance;
	}

	public void setExecutionChance(float executionChance) {
		this.executionChance = executionChance;
	}

	public float getMovementSpeed() {
		return movementSpeed;
	}

	public void setMovementSpeed(float movementSpeed) {
		this.movementSpeed = movementSpeed;
	}

	public float getVerticalOffsetBase() {
		return verticalOffsetBase;
	}

	public void setVerticalOffsetBase(float verticalOffsetBase) {
		this.verticalOffsetBase = verticalOffsetBase;
	}

	public float getVerticalOffsetScale() {
		return verticalOffsetScale;
	}

	public void setVerticalOffsetScale(float verticalOffsetScale) {
		this.verticalOffsetScale = verticalOffsetScale;
	}

	public float getVerticalOffsetMax() {
		return verticalOffsetMax;
	}

	public void setVerticalOffsetMax(float verticalOffsetMax) {
		this.verticalOffsetMax = verticalOffsetMax;
	}

	public float getHorizontalOffsetBase() {
		return horizontalOffsetBase;
	}

	public void setHorizontalOffsetBase(float horizontalOffsetBase) {
		this.horizontalOffsetBase = horizontalOffsetBase;
	}

	public float getHorizontalOffsetScale() {
		return horizontalOffsetScale;
	}

	public void setHorizontalOffsetScale(float horizontalOffsetScale) {
		this.horizontalOffsetScale = horizontalOffsetScale;
	}

	public float getHorizontalOffsetMax() {
		return horizontalOffsetMax;
	}

	public void setHorizontalOffsetMax(float horizontalOffsetMax) {
		this.horizontalOffsetMax = horizontalOffsetMax;
	}

	public Set<CallForHelpEntry> getCallForHelpEntries() {
		return callForHelpEntries;
	}

	public void setCallForHelpEntries(Set<CallForHelpEntry> callForHelpEntries) {
		this.callForHelpEntries = callForHelpEntries;
	}

	public static class CallForHelpEntry {

		private String entityName = "minecraft:null";
		private float verticalRange = 0.0F;
		private float horizontalRange = 0.0F;
		private boolean requiresVision = true;
		private float chance = 1.0F;
		private boolean ignoreParentInCombat = false;
		private boolean ignoreParentAboveHealthThreshold = false;
		private boolean ignoreParentChanceTestFailed = false;

		public String getEntityName() {
			return entityName;
		}

		public void setEntityName(String entityName) {
			this.entityName = entityName;
		}

		public float getVerticalRange() {
			return verticalRange;
		}

		public void setVerticalRange(float verticalRange) {
			this.verticalRange = verticalRange;
		}

		public float getHorizontalRange() {
			return horizontalRange;
		}

		public void setHorizontalRange(float horizontalRange) {
			this.horizontalRange = horizontalRange;
		}

		public boolean requiresVision() {
			return requiresVision;
		}

		public void setRequiresVision(boolean requiresVision) {
			this.requiresVision = requiresVision;
		}

		public float getChance() {
			return chance;
		}

		public void setChance(float chance) {
			this.chance = chance;
		}

		public boolean ignoreParentInCombat() {
			return ignoreParentInCombat;
		}

		public void setIgnoreParentInCombat(boolean ignoreParentInCombat) {
			this.ignoreParentInCombat = ignoreParentInCombat;
		}

		public boolean ignoreParentAboveHealthThreshold() {
			return ignoreParentAboveHealthThreshold;
		}

		public void setIgnoreParentAboveHealthThreshold(boolean ignoreParentAboveHealthThreshold) {
			this.ignoreParentAboveHealthThreshold = ignoreParentAboveHealthThreshold;
		}

		public boolean ignoreParentChanceTestFailed() {
			return ignoreParentChanceTestFailed;
		}

		public void setIgnoreParentChanceTestFailed(boolean ignoreParentChanceTestFailed) {
			this.ignoreParentChanceTestFailed = ignoreParentChanceTestFailed;
		}

	}

}
