package com.charles445.rltweaker.entity.ai;

public class InvestigateAIConfig {

	private int priority;
	private float healthThreshold;
	private float executionChance;

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

}
