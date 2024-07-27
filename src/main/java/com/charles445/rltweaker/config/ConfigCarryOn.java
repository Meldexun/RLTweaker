package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigCarryOn
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Prevent creating glitched Quark chests with Carry On")
	@Config.Name("Quark Chest Fix")
	@Config.RequiresMcRestart
	public boolean quarkChestFix = true;
	
	@Config.Comment("More safety checks for dropping tile entities in the world")
	@Config.Name("Tile Entity Drop Safety Check")
	@Config.RequiresMcRestart
	public boolean tileEntityDropSafetyCheck = true;
}
