package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigRustic
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Fix for rustic log and wood not having their tool set to axe. May act strangely if not matched on the client.")
	@Config.Name("Wood Harvest Tool Fix")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean woodHarvestToolFix = false;
	
	@Config.Comment("Validates containers when players have the UI open to prevent dupes")
	@Config.Name("Validate Containers")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean validateContainers = true;
	
	@Config.Comment("When drinking wildberry wine the amplifier of positive potion effects gets increased/decreased by this value. Requires 'patchRusticWineEffects' to be true!")
	@Config.Name("Wildberry Wine Amplifier Modifier")
	@RLConfig.ImprovementsOnly("1")
	@RLConfig.RLCraftTwoEightTwo("1")
	@RLConfig.RLCraftTwoNine("1")
	public int wildberryWineAmplifierModifier = 1;
	
	@Config.Comment("When drinking wildberry wine the amplifier of positive potion effects can get at most increased up to this value. Requires 'patchRusticWineEffects' to be true!")
	@Config.Name("Wildberry Wine Amplifier Maximum")
	@RLConfig.ImprovementsOnly("2")
	@RLConfig.RLCraftTwoEightTwo("2")
	@RLConfig.RLCraftTwoNine("2")
	public int wildberryWineAmplifierMaximum = 2;
	
	@Config.Comment("When drinking wine the duration of positive potion effects gets increased/decreased by this value. Requires 'patchRusticWineEffects' to be true!")
	@Config.Name("Wine Duration Modifier")
	@RLConfig.ImprovementsOnly("2400")
	@RLConfig.RLCraftTwoEightTwo("2400")
	@RLConfig.RLCraftTwoNine("2400")
	public int wineDurationModifier = 2400;
	
	@Config.Comment("When drinking wildberry wine the duration of positive potion effects can get at most increased up to this value. Requires 'patchRusticWineEffects' to be true!")
	@Config.Name("Wine Duration Maximum")
	@RLConfig.ImprovementsOnly("12000")
	@RLConfig.RLCraftTwoEightTwo("12000")
	@RLConfig.RLCraftTwoNine("12000")
	public int wineDurationMaximum = 12000;
}
