package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigSME
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Advanced Mending fixes")
	@Config.Name("Manage Advanced Mending")
	@Config.RequiresMcRestart
	public boolean manageAdvancedMending = true;
	
	@Config.Comment("Leaves in the double XP advanced mending bug. RLCraft vanilla is true")
	@Config.Name("Advanced Mending Double XP")
	public boolean advancedMendingDoubleXP = true;
	
	@Config.Comment("Arc Slash fixes")
	@Config.Name("Manage Arc Slash")
	@Config.RequiresMcRestart
	public boolean manageArcSlash = true;

	@Config.Comment("Curse of Possession fixes")
	@Config.Name("Manage Curse of Possession")
	@Config.RequiresMcRestart
	public boolean manageCurseOfPossession = true;
	
	@Config.Comment("How delayed curse of possession is. Vanilla is 1")
	@Config.Name("Curse of Possession Delay")
	@Config.RangeInt(min=1, max=40)
	public int curseOfPossessionDelay = 20;
	
	@Config.Comment("Empowered Defence fixes")
	@Config.Name("Manage Empowered Defence")
	@Config.RequiresMcRestart
	public boolean manageEmpoweredDefence = true;
	
	@Config.Comment("Evasion fixes")
	@Config.Name("Manage Evasion")
	@Config.RequiresMcRestart
	public boolean manageEvasion = true;
	
	@Config.Comment("Freezing fixes")
	@Config.Name("Manage Freezing")
	@Config.RequiresMcRestart
	public boolean manageFreezing = true;
	
	@Config.Comment("Parry fixes")
	@Config.Name("Manage Parry")
	@Config.RequiresMcRestart
	public boolean manageParry = true;
	
	@Config.Comment("Unreasonable fixes")
	@Config.Name("Manage Unreasonable")
	@Config.RequiresMcRestart
	public boolean manageUnreasonable = true;
	
	@Config.Comment("Upgraded Potentials fixes")
	@Config.Name("Manage Upgraded Potentials")
	public boolean manageUpgradedPotentials = true;
}
