package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigLostCities
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Whether the chunk generation cache should be cleaned regularly to avoid memory leaks.")
	@Config.Name("GenerationCacheCleanupEnabled")
	public boolean generationCacheCleanupEnabled = false;
	
	@Config.Comment("How many entries the caches are allowed to get before they are cleared. Default is recommended.")
	@Config.Name("GenerationCacheMaxCount")
	@Config.RangeInt(min=0)
	public int generationCacheMaxCount = 200;
}
