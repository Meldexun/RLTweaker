package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigSRParasites
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Whether parasites should prevent players from sleeping")
	@Config.Name("Parasites Sleep Prevention")
	@Config.RequiresMcRestart
	public boolean parasitesSleepPrevention = true;
	
	@Config.Comment("Forcefully remove parasites from certain dimensions")
	@Config.Name("Parasites Dimension Blacklist Enabled")
	public boolean parasitesDimensionBlacklistEnabled = false;
	
	@Config.Comment("Dimension blacklist for parasites")
	@Config.Name("Parasites Dimension Blacklist")
	public int[] parasitesDimensionBlacklist = new int[0];
	
	@Config.Comment("Attempt to fix biomass crash")
	@Config.Name("Parasites Biomass Crash Fix")
	public boolean parasitesBiomassCrashFix = true;
}
