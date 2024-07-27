package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigCharm
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Fix enchantments appearing on incompatible items")
	@Config.Name("Fix Incorrect Item Enchantments")
	@Config.RequiresMcRestart
	public boolean fixIncorrectItemEnchantments = true;
	
	@Config.Comment("Fix Salvage not being removed from villager trades when disabled")
	@Config.Name("Fix Salvage Trade")
	public boolean fixSalvageTrade = true;
	
	@Config.Comment("Fix charged emeralds crashing when being fired from a dispenser")
	@Config.Name("Fix Charged Emerald Crash")
	public boolean fixChargedEmeraldCrash = true;
	
	@Config.Comment("Disables the effect of Magnetic without unregistering it to avoid packet issues. Do this if you experience dupes with the enchantment.")
	@Config.Name("Disable Magnetic Enchantment")
	@Config.RequiresMcRestart
	public boolean disableMagneticEnchantment = false;
	
}
