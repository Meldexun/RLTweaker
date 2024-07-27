package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigWaystones
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Remove biome name from new Waystones in villages")
	@Config.Name("Remove Waystone Biome Name")
	public boolean removeWaystoneBiome = false;
}
