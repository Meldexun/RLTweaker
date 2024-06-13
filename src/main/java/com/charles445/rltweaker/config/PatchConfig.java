package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import com.charles445.rltweaker.config.annotation.SpecialEnum;

import net.minecraftforge.common.config.Config;

public class PatchConfig
{
	//Do NOT provide a Config.Name!
	//DO NOT!
	
	//Fields are to be appended with
	//general.patches.
	
	//general.patches.ENABLED
	@Config.RequiresMcRestart
	@Config.Comment("Master switch for the coremod")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean ENABLED = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enable to write modified classes to disk.")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean export = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes the particle queue threaded. Fixes concurrency issue with logical server creating physical client particles.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean particleThreading = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes some entities stop checking for large entity collisions. Not needed without a max entity radius changing mod.")
	@RLConfig.SpecialSignature(value = SpecialEnum.MAX_ENTITY_RADIUS_HIGH, pass = "true", fail = "false")
	//@RLConfig.ImprovementsOnly("true")
	//@RLConfig.RLCraftTwoEightTwo("true")
	//@RLConfig.RLCraftTwoNine("true")
	public boolean lessCollisions = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for mounted combat with BetterCombat")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("false") //Tragic, really. It's client side so we don't want anyone getting confused.
	@RLConfig.RLCraftTwoNine("true")
	public boolean betterCombatMountFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes RealBench dupe bug")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean realBenchDupeBugFix = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fixes Lycanites Pet Dupe in older LycanitesMobs versions than 2.0.8.0, may cause crashes in newer versions.")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean lycanitesPetDupeFix = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes mobs having trouble pathing through open doors")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean doorPathfindingFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Reduces search size for finding some entities like players and items. Not needed without a max entity radius changing mod. Helps with Quark Monster Box lag.")
	@RLConfig.SpecialSignature(value = SpecialEnum.MAX_ENTITY_RADIUS_HIGH, pass = "true", fail = "false")
	//@RLConfig.ImprovementsOnly("false")
	//@RLConfig.RLCraftTwoEightTwo("false")
	//@RLConfig.RLCraftTwoNine("true")
	public boolean reducedSearchSize = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables config option to tweak broadcasted sounds.")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchBroadcastSounds = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables config option to blacklist enchantments.")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchEnchantments = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes the motion checker even more aggressive.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean aggressiveMotionChecker = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables Entity Block Destroy Blacklist")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchEntityBlockDestroy = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Patches a dupe with modded item frames, specifically Quark")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchItemFrameDupe = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables Entity Push Prevention")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchPushReaction = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes some mod related anvil dupes")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true)")
	public boolean patchAnvilDupe = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for client side overlay text configuration")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchOverlayMessage = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for some hopper tweaks")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchHopper = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for critical events with BetterCombat")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("false") //Has balancing implications
	@RLConfig.RLCraftTwoNine("true")
	public boolean betterCombatCriticalsFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes crash with bound scrolls and return scrolls, and removes their unexpected spawn setting")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean fixWaystoneScrolls = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes ghost chunkloading when creating pathfinding chunk caches")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean pathfindingChunkCacheFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Attempt to be compatible with alternative server software")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean serverCompatibility = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables features for ticking chunk blocks")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean chunkTicks = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables config options to tweak what effects are incurable")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchCurePotion = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enable to make wine potion effect modifications configurable through RLTweaker")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchRusticWineEffects = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes modifier books from BountifulBaubles changing the original item when put into an anvil")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchModifierBooks = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes Chest of Drawers from BetterNether not dropping its content when broken")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchChestOfDrawers = true;

	@Config.RequiresMcRestart
	@Config.Comment("Requires patchCustomAttributeInstances patch! Fixes Potion Core Resistance calculations")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchPotionCoreResistance = true;

	@Config.RequiresMcRestart
	@Config.Comment("Enables custom attribute instances. Required by other patches.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchCustomAttributeInstances = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fixes falling block portal dupe.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchFallingBlockPortalDupe = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fix for item linking from Quark.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchItemLinking = true;

	@Config.RequiresMcRestart
	@Config.Comment("Optimizes World#getCollisionBoxes which often requires a lot of cpu time on servers.")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean patchFasterBlockCollision = true;

	@Config.RequiresMcRestart
	@Config.Comment("Allows cleaning up structure .dat files.")
	public boolean patchCleanupStructureWorldgenFiles = true;

	@Config.RequiresMcRestart
	@Config.Comment("Improves performance and fixes bugs of EntityAIInfectedSearch and EntityAINearestAttackableTargetStatus.")
	public boolean patchSRPAI = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fix potion changes on players causing unnecessary entity tracker updates.")
	public boolean patchPotionEntityTrackerUpdate = true;

	@Config.RequiresMcRestart
	@Config.Comment("Replaces the random update handler with a faster alternative.")
	public boolean patchSereneSeasonsMelting = true;
}
