package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigGooglyEyesClient
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Entity blacklist for googly eyes that uses the registry name instead")
	@Config.Name("Entity Blacklist")
	public String[] entityBlacklist = new String[]{"examplemod:mob"};
}
