package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigPotionCoreClient
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Fix for Magic Shielding HUD Color affecting the hunger bar")
	@Config.Name("Magic Shielding HUD Fix")
	public boolean magicShieldingHUDFix = true;
	
	@Config.Comment("Whether to render Potion Core's armor icons on the hud")
	@Config.Name("Render Armor Icons")
	public boolean renderArmorIcons = true;
	
	@Config.Comment("Whether to render Potion Core's resistance red armor outlines on the hud")
	@Config.Name("Render Armor Resistance")
	public boolean renderArmorResistance = true;
	
	@Config.Comment("Compatibility for Overloaded Armor Bar, rendering magic shielding properly")
	@Config.Name("Overloaded Armor Bar Compatibility")
	public boolean overloadedArmorBarCompatibility = true;
	
}
