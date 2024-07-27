package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigRoguelike
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Prevent the Mining Fatigue crash")
	@Config.Name("Prevent Fatigue Crash")
	public boolean preventFatigueCrash = true;
}
