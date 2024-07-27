package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigLycanites
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Allow checking for rltweaker.lycanitesboss as entity data to allow for specific entities to become bosses. Use rltweaker.lycanitesbossrange for range of effect in blocks.")
	@Config.Name("Enable Entity Data LycanitesBoss")
	public boolean enableEntityDataLycanitesBoss = true;
}
