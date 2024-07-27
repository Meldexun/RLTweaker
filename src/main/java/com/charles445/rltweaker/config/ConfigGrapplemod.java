package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigGrapplemod
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Allow and fix fall damage with grappling hooks")
	@Config.Name("Grappling Hook Fall Damage")
	public boolean grapplingHookFallDamage = false;
	
	
	@Config.Comment("Hinders the player grappling below themselves while falling")
	@Config.Name("Hinder Grappling While Falling")
	public boolean hinderGrapplingWhileFalling = false;
}
