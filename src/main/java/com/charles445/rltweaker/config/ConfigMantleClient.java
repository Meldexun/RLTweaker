package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigMantleClient
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Whether to remove Mantle's health bar override")
	@Config.Name("Remove Mantle Health Bar")
	@Config.RequiresMcRestart
	public boolean removeMantleHealthBar = false;
}
