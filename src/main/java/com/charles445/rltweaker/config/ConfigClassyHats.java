package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigClassyHats
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Fixes a hat dupe")
	@Config.Name("Hat Dupe Fix")
	@Config.RequiresMcRestart
	public boolean hatDupeFix = true;
}
