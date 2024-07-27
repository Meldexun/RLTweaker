package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigClassyHatsClient
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Mo Bends Compatibility")
	@Config.Name("Mo Bends Compatibility")
	@Config.RequiresMcRestart
	public boolean mobendsCompatibility = true;
	
	@Config.Comment("Fixes Bag Screen")
	@Config.Name("Fix Bag Screen")
	@Config.RequiresMcRestart
	public boolean fixBagScreen = true;
}
