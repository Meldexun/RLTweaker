package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigMultiMine
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;

	@Config.Comment("Use the RLTweaker watchdog to detect and fix stalls")
	@Config.Name("Stall Watchdog")
	@Config.RequiresMcRestart
	public boolean stallWatchdog = true;
}
