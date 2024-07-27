package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigBaubles
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Fixes Miner's Ring Dupe")
	@Config.Name("Miner's Ring Dupe Fix")
	public boolean minersRingDupeFix = true;
}
