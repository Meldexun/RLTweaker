package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigLevelUpTwo
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Whether pets can use skills")
	@Config.Name("Pets Use Skills")
	public boolean petsUseSkills = true;
	
	@Config.Comment("Overhauls the stealth mechanic")
	@Config.Name("Stealth Overhaul")
	public boolean stealthOverhaul = false;
	
	@Config.Comment("Should stealth overhaul apply to Lycanites Mobs. Only does anything if Stealth Overhaul is enabled")
	@Config.Name("Stealth Overhaul Lycanites")
	public boolean stealthOverhaulLycanites = true;
	
	@Config.Comment("Base distance mobs can see you at while sneaking for stealth calculations. Only applies if stealth level is over zero.")
	@Config.Name("Stealth Overhaul Base Distance")
	public double stealthOverhaulBaseDistance = 16.0d;
	
	@Config.Comment("How much closer in blocks mobs need to be to see a sneaking player per stealth level.")
	@Config.Name("Stealth Overhaul Distance Per Level")
	public double stealthOverhaulDistancePerLevel = 0.8d;
}
