package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigInfernalMobs
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Infernal mobs adhere to the enchantment blacklist in the rltweaker minecraft config section")
	@Config.Name("Use Enchantment Blacklist")
	public boolean useEnchantmentBlacklist = true;

	@Config.Comment("Use the RLTweaker watchdog to detect and fix stalls")
	@Config.Name("Stall Watchdog")
	@Config.RequiresMcRestart
	public boolean stallWatchdog = true;
}
