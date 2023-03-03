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
	@RLConfig.ImprovementsOnly("")
	@RLConfig.RLCraftTwoEightTwo("")
	@RLConfig.RLCraftTwoNine("")
	public String[] incurablePotionEffects = {};
	@Config.Ignore
	public final Set<ResourceLocation> incurablePotionEffectsImpl = new HashSet<>();
	
	@Config.Comment("Potion effects that disable the cure potion effect")
	@Config.Name("Cure Disabling Potion Effects")
	@RLConfig.ImprovementsOnly("")
	@RLConfig.RLCraftTwoEightTwo("")
	@RLConfig.RLCraftTwoNine("")
	public String[] cureDisablingPotionEffects = {};
	@Config.Ignore
	public final Set<ResourceLocation> cureDisablingPotionEffectsImpl = new HashSet<>();
	
	@Config.Comment("Enable to make tipsy and effects applied by tipsy incurable")
	@Config.Name("Incurable Tipsy")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean incurableTipsy = true;

	@Config.Comment("Requires patchPotionCoreResistance patch!")
	@Config.Name("Alternative Resistance Mode")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean alternativeResistanceMode = true;

	@Config.Comment("Requires patchPotionCoreResistance patch! Resistance of the resistance potion attribute modifier.")
	@Config.Name("Resistance Potion Modifier Amount")
	@RLConfig.ImprovementsOnly("0.2")
	@RLConfig.RLCraftTwoEightTwo("0.2")
	@RLConfig.RLCraftTwoNine("0.2")
	public double resistanceAmount = 0.2D;

	@Config.Comment("Requires patchPotionCoreResistance patch! Operation of the resistance potion attribute modifier.")
	@Config.Name("Resistance Potion Modifier Operation")
	@RLConfig.ImprovementsOnly("2")
	@RLConfig.RLCraftTwoEightTwo("2")
	@RLConfig.RLCraftTwoNine("2")
	public int resistanceOperation = 2;

}
