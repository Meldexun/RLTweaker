package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigFBPClient
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Fixes placing ghost blocks when placement is blocked")
	@Config.Name("Fix Placement Ghost Blocks")
	@Config.RequiresMcRestart
	public boolean fixPlacementGhostBlocks = true;
}
