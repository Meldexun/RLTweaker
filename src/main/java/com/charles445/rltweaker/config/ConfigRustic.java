package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigRustic
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Fix for rustic log and wood not having their tool set to axe. May act strangely if not matched on the client.")
	@Config.Name("Wood Harvest Tool Fix")
	@Config.RequiresMcRestart
	public boolean woodHarvestToolFix = false;
	
	@Config.Comment("Validates containers when players have the UI open to prevent dupes")
	@Config.Name("Validate Containers")
	public boolean validateContainers = true;
	
	@Config.Comment("When drinking wildberry wine the amplifier of positive potion effects gets increased/decreased by this value. Requires 'patchRusticWineEffects' to be true!")
	@Config.Name("Wildberry Wine Amplifier Modifier")
	public int wildberryWineAmplifierModifier = 1;
	
	@Config.Comment("When drinking wildberry wine the amplifier of positive potion effects can get at most increased up to this value. Requires 'patchRusticWineEffects' to be true!")
	@Config.Name("Wildberry Wine Amplifier Maximum")
	public int wildberryWineAmplifierMaximum = 2;
	
	@Config.Comment("When drinking wine the duration of positive potion effects gets increased/decreased by this value. Requires 'patchRusticWineEffects' to be true!")
	@Config.Name("Wine Duration Modifier")
	public int wineDurationModifier = 2400;
	
	@Config.Comment("When drinking wildberry wine the duration of positive potion effects can get at most increased up to this value. Requires 'patchRusticWineEffects' to be true!")
	@Config.Name("Wine Duration Maximum")
	public int wineDurationMaximum = 12000;
}
