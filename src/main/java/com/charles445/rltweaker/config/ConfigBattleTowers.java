package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigBattleTowers
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Make tower explosions belong to nobody, avoids Infernal Mobs issues")
	@Config.Name("Change Tower Explosion Owner")
	public boolean towerExplosionNoCredit = false;
	
	@Config.Comment("Golems who are attacked will get angry more consistently, and from farther away")
	@Config.Name("Golem High Aggression")
	public boolean golemHighAggression = false;
	
	@Config.Comment("Make golems stop building up speed when dormant")
	@Config.Name("Golem Dormant Speed Fix")
	public boolean golemDormantSpeedFix = false;
	
	@Config.Comment("Prevent golems from drowning")
	@Config.Name("Golem Drowning Fix")
	public boolean golemDrowningFix = false;
	
	@Config.Comment("Prevent golems from suffocating in walls")
	@Config.Name("Golem Suffocating Fix")
	public boolean golemSuffocatingFix = false;
	
	@Config.Comment("Prevent falling block damage for golems")
	@Config.Name("Golem Falling Block Fix")
	public boolean golemFallingBlockFix = false;
	
	@Config.Comment("Prevent anvil damage for golems")
	@Config.Name("Golem Anvil Fix")
	public boolean golemAnvilFix = false;
	
	@Config.Comment("Prevent golems taking damage from lycanites fluids")
	@Config.Name("Golem Lycanites Fluid Fix")
	public boolean golemLycanitesFluidFix = false;
	
	@Config.Comment("Max Golem XZ movement to make using tools like fishing rods harder. Set to a negative number to disable")
	@Config.Name("Golem Speed Cap")
	@Config.RangeDouble(min=-1.0d)
	public double golemSpeedCap = -1.0d;
	
	@Config.Comment("Max Golem Y movement (upwards) to make launching him harder. Set to a negative number to disable")
	@Config.Name("Golem Speed Cap Upwards")
	@Config.RangeDouble(min=-1.0d)
	public double golemSpeedCapUpwards = -1.0d;
	
	@Config.Comment("Prevent golems from riding anything, like boats or minecarts")
	@Config.Name("Golem Auto Dismount")
	public boolean golemAutoDismount = false;
	
	@Config.Comment("Replace the golem's projectile with a LycanitesMobs Projectile (Requires LycanitesMobs)")
	@Config.Name("Golem Lycanites Projectile")
	public boolean golemLycanitesProjectile = false;
	
	@Config.Comment("The name of the projectile to use")
	@Config.Name("Golem Lycanites Projectile Name")
	public String golemLycanitesProjectileName = "demonicblast";
	
	@Config.Comment("Scale modifier for the lycanites projectile")
	@Config.Name("Golem Lycanites Projectile Scale Modifier")
	public double golemLycanitesProjectileScaleModifier = 1.0d;
	
	@Config.Comment("Speed modifier for the lycanites projectile")
	@Config.Name("Golem Lycanites Projectile Speed Modifier")
	public double golemLycanitesProjectileSpeedModifier = 9.0d;
	
	@Config.Comment("Use dimension blacklist")
	@Config.Name("Dimension Blacklist Enabled")
	@Config.RequiresMcRestart
	public boolean dimensionBlacklistEnabled = false;
	
	@Config.Comment("Dimension blacklist of dimension IDs")
	@Config.Name("Dimension Blacklist")
	@Config.RequiresMcRestart
	public int[] dimensionBlacklistIds = {111};
	
	@Config.Comment("Whether the dimension blacklist is a whitelist")
	@Config.Name("Dimension Blacklist Is Whitelist")
	@Config.RequiresMcRestart
	public boolean dimensionBlacklistIsWhitelist = false;
	
	@Config.Comment("Make towers generate consistently instead of as soon as possible, avoiding the need for the positions file. Be warned, this disables tower commands.")
	@Config.Name("Consistent Tower Generation")
	@Config.RequiresMcRestart
	public boolean consistentTowerGeneration = false;
	
	@Config.Comment("Removes tower destroyers from unloaded worlds. Prevents potential leaks and crashes.")
	@Config.Name("Remove Unloaded Tower Destroyers")
	public boolean removeUnloadedTowerDestroyers = true;
	
	@Config.Comment("Prevents clients from changing the tower destruction config")
	@Config.Name("Enforce Tower Destruction Config")
	public boolean enforceTowerDestructionConfig = true;
}
