package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraftforge.common.config.Config;

public class ConfigIceAndFire
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Prevent statues from picking up items (and other mob griefing events)")
	@Config.Name("Statue Mob Griefing Fix")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean statueMobGriefingFix = true;
	
	@Config.Comment("Prevent statues from saving some data they should not")
	@Config.Name("Statue Data Fixes")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean statueDataFixes = true;
	
	@Config.Comment("Interact with dragons with only the right hand")
	@Config.Name("Right Hand Dragon Interaction")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean rightHandDragonInteraction = true;
	
	@Config.Comment("Fixes Myrmex Queen trades that generate with the wrong type of resin")
	@Config.Name("Myrmex Queen Trade Fix")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean myrmexQueenTradeFix = true;
	
	@Config.Comment("Stop deathworm egg drops from being affected by looting")
	@Config.Name("Deathworm Eggs Ignore Looting")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean deathwormEggsIgnoreLooting = false;
	
	@Config.Comment("Prevents gorgons from targeting player statues. The presence of player statues will still give their AI trouble.")
	@Config.Name("Stop Gorgon Targeting Player Statues")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean stopGorgonTargetingPlayerStatues = true;
	
	@Config.Comment("Removes snow trade for RLCraft balancing. Only works in Ice and Fire 1.7.1")
	@Config.Name("RLCraft Trade Balancing")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean rlcraftTradeBalancing = false;
	
	@Config.Comment("Requires patchAmphithereTameDamage patch! Damage that Amphitheres deal when a player tries to tame them.")
	@Config.Name("Amphithere Tame Damage")
	@RLConfig.ImprovementsOnly("1.0")
	@RLConfig.RLCraftTwoEightTwo("1.0")
	@RLConfig.RLCraftTwoNine("1.0")
	public float amphithereTameDamage = 1.0F;
}
