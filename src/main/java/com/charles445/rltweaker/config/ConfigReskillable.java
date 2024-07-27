package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigReskillable
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Enables JSON transmutation additions")
	@Config.Name("Custom Transmutation")
	@Config.RequiresMcRestart
	public boolean customTransmutation = false;
	
	@Config.Comment("Enables toggling and refunding traits by clicking on them")
	@Config.Name("Toggleable Traits")
	@Config.RequiresMcRestart
	public boolean toggleableTraits = false;
}
