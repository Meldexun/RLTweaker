package com.charles445.rltweaker.config;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigPotionCore
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Attempt to prevent invalid jump boosts from taking effect, and tweaks controllable jump boost. Fixes some mods, but breaks others. Not recommended for use.")
	@Config.Name("Cap Jump Boost")
	@Config.RequiresMcRestart
	public boolean capJumpBoost = false;
	
	@Config.Comment("Enable to make potion sickness and effects applied by potion sickness incurable")
	@Config.Name("Incurable Potion Sickness")
	public boolean incurablePotionSickness = true;
	
	@Config.Comment("Potion effects that are incurable")
	@Config.Name("Incurable Potion Effects")
	public String[] incurablePotionEffects = {};
	@Config.Ignore
	public final Set<ResourceLocation> incurablePotionEffectsImpl = new HashSet<>();
	
	@Config.Comment("Potion effects that disable the cure potion effect")
	@Config.Name("Cure Disabling Potion Effects")
	public String[] cureDisablingPotionEffects = {};
	@Config.Ignore
	public final Set<ResourceLocation> cureDisablingPotionEffectsImpl = new HashSet<>();
	
	@Config.Comment("Enable to make tipsy and effects applied by tipsy incurable")
	@Config.Name("Incurable Tipsy")
	public boolean incurableTipsy = true;

	@RequiresMcRestart
	@Config.Comment("Requires patchPotionCoreResistance patch! Alternative mode to calculate resistance when having multiple modifiers.\n"
			+ "For example having two attribute modifiers both with amount=0.3 and operation=2 will result in\n"
			+ " Normal mode:      '(1 * (1 + 0.3) * (1 + 0.3)) - 1 = (1 * 1.3 * 1.3) - 1 = 0.69 = 69%' damage reduction\n"
			+ " Alternative mode: '1 - (1 * (1 - 0.3) * (1 - 0.3)) = 1 - (1 * 0.7 * 0.7) = 0.51 = 51%' damage reduction")
	@Config.Name("Alternative Resistance Mode")
	public boolean alternativeResistanceMode = true;

	@RequiresMcRestart
	@Config.Comment("Requires patchPotionCoreResistance patch! Amount of the resistance potion attribute modifier.")
	@Config.Name("Resistance Potion Modifier Amount")
	public double resistanceAmount = 0.2D;

	@RequiresMcRestart
	@Config.Comment("Requires patchPotionCoreResistance patch! Operation of the resistance potion attribute modifier.")
	@Config.Name("Resistance Potion Modifier Operation")
	public int resistanceOperation = 2;

	@RequiresMcRestart
	@Config.Comment("Requires patchPotionCoreResistance patch! Amount of the vulnerable potion attribute modifier.")
	@Config.Name("Vulnerable Potion Modifier Amount")
	public double vulnerableAmount = -0.5D;

	@RequiresMcRestart
	@Config.Comment("Requires patchPotionCoreResistance patch! Operation of the vulnerable potion attribute modifier.")
	@Config.Name("Vulnerable Potion Modifier Operation")
	public int vulnerableOperation = 2;

}
