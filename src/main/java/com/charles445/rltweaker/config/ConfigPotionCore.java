package com.charles445.rltweaker.config;

import java.util.HashSet;
import java.util.Set;

import com.charles445.rltweaker.config.annotation.RLConfig;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

public class ConfigPotionCore
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean enabled = true;
	
	@Config.Comment("Attempt to prevent invalid jump boosts from taking effect, and tweaks controllable jump boost. Fixes some mods, but breaks others. Not recommended for use.")
	@Config.Name("Cap Jump Boost")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean capJumpBoost = false;
	
	@Config.Comment("Enable to make potion sickness and effects applied by potion sickness incurable")
	@Config.Name("Incurable Potion Sickness")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean incurablePotionSickness = true;
	
	@Config.Comment("Potion effects that are incurable")
	@Config.Name("Incurable Potion Effects")
	@RLConfig.ImprovementsOnly("rustic:tipsy")
	@RLConfig.RLCraftTwoEightTwo("rustic:tipsy")
	@RLConfig.RLCraftTwoNine("rustic:tipsy")
	public String[] incurablePotionEffects = {"rustic:tipsy"};
	@Config.Ignore
	public final Set<ResourceLocation> incurablePotionEffectsImpl = new HashSet<>();
}
