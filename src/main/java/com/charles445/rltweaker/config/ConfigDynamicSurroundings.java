package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigDynamicSurroundings
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Delay the environment service for village detection so it runs every second instead of every tick")
	@Config.Name("Environment Service Delay")
	@Config.RequiresMcRestart
	public boolean environmentServiceDelay = true;
}
