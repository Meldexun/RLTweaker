package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigBetterSurvival
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Enables blacklist for blocks with the tunneling enchantment")
	@Config.Name("Tunneling Blacklist Enabled")
	public boolean tunnelingBlacklistEnabled = false;
	
	@Config.Comment("Whether the tunneling blacklist is a whitelist. Acting as a whitelist may cause buggy behavior. Use with caution.")
	@Config.Name("Tunneling Blacklist Is Whitelist")
	public boolean tunnelingBlacklistIsWhitelist = false;
	
	@Config.Comment("Blocks that are blacklisted from interacting with the tunneling enchantment")
	@Config.Name("Tunneling Blacklist")
	public String[] tunnelingBlacklist = {"minecraft:chest"};
	
	@Config.Comment("Whether tunneling should fail if the center block break event fails. By default, Better Survival has this false")
	@Config.Name("Tunneling Cancelable")
	public boolean tunnelingCancelable = true;
	
	@Config.Comment("Whether tunneling can break tile entities")
	@Config.Name("Tunneling Breaks Tile Entities")
	public boolean tunnelingBreaksTileEntities = false;
	
	@Config.Comment("Speed multiplier for the Range enchantment. Default is 2")
	@Config.Name("Range Speed Multiplier")
	public double rangeSpeedMultiplier = 2.0d;
	
	@Config.Comment("Whether blindness affects mobs at all")
	@Config.Name("Mob Blindness")
	public boolean mobBlindness = true;
	
	@Config.Comment("Replaces the blindness and combo handler entirely for performance")
	@Config.Name("Replace Combo Handler")
	@Config.RequiresMcRestart
	public boolean replaceComboHandler = true;
	
	@Config.Comment("Replaces the enchantment handler entirely for performance")
	@Config.Name("Replace Enchantment Handler")
	@Config.RequiresMcRestart
	public boolean replaceEnchantmentHandler = true;
	
	@Config.Comment("How strong the reduced follow range for mobs is with blindness")
	@Config.Name("Mob Blindness Percentage")
	@Config.RangeDouble(min=0.0d, max=100.0d)
	public double mobBlindnessPercentage = 80.0d;
	
	@Config.Comment("Blacklist for mob blindness")
	@Config.Name("Mob Blindness Blacklist")
	public String[] mobBlindnessBlacklist = new String[]{"examplemod:mob"};
}
