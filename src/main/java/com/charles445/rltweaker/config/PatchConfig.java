package com.charles445.rltweaker.config;

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
	public boolean ENABLED = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enable to write modified classes to disk.")
	public boolean export = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes the particle queue threaded. Fixes concurrency issue with logical server creating physical client particles.")
	public boolean particleThreading = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes some entities stop checking for large entity collisions. Not needed without a max entity radius changing mod.")
	public boolean lessCollisions = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for mounted combat with BetterCombat")
	public boolean betterCombatMountFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes RealBench dupe bug")
	public boolean realBenchDupeBugFix = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fixes Lycanites Pet Dupe in older LycanitesMobs versions than 2.0.8.0, may cause crashes in newer versions.")
	public boolean lycanitesPetDupeFix = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes mobs having trouble pathing through open doors")
	public boolean doorPathfindingFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Reduces search size for finding some entities like players and items. Not needed without a max entity radius changing mod. Helps with Quark Monster Box lag.")
	public boolean reducedSearchSize = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables config option to tweak broadcasted sounds.")
	public boolean patchBroadcastSounds = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables config option to blacklist enchantments.")
	public boolean patchEnchantments = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Makes the motion checker even more aggressive.")
	public boolean aggressiveMotionChecker = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables Entity Block Destroy Blacklist")
	public boolean patchEntityBlockDestroy = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Patches a dupe with modded item frames, specifically Quark")
	public boolean patchItemFrameDupe = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables Entity Push Prevention")
	public boolean patchPushReaction = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes some mod related anvil dupes")
	public boolean patchAnvilDupe = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for client side overlay text configuration")
	public boolean patchOverlayMessage = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for some hopper tweaks")
	public boolean patchHopper = false;
	
	@Config.RequiresMcRestart
	@Config.Comment("Allows for critical events with BetterCombat")
	public boolean betterCombatCriticalsFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes crash with bound scrolls and return scrolls, and removes their unexpected spawn setting")
	public boolean fixWaystoneScrolls = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes ghost chunkloading when creating pathfinding chunk caches")
	public boolean pathfindingChunkCacheFix = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Attempt to be compatible with alternative server software")
	public boolean serverCompatibility = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enables config options to tweak what effects are incurable")
	public boolean patchCurePotion = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Enable to make wine potion effect modifications configurable through RLTweaker")
	public boolean patchRusticWineEffects = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes modifier books from BountifulBaubles changing the original item when put into an anvil")
	public boolean patchModifierBooks = true;
	
	@Config.RequiresMcRestart
	@Config.Comment("Fixes Chest of Drawers from BetterNether not dropping its content when broken")
	public boolean patchChestOfDrawers = true;

	@Config.RequiresMcRestart
	@Config.Comment("Requires patchCustomAttributeInstances patch! Fixes Potion Core Resistance calculations")
	public boolean patchPotionCoreResistance = true;

	@Config.RequiresMcRestart
	@Config.Comment("Enables custom attribute instances. Required by other patches.")
	public boolean patchCustomAttributeInstances = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fixes falling block portal dupe.")
	public boolean patchFallingBlockPortalDupe = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fix for item linking from Quark.")
	public boolean patchItemLinking = true;

	@Config.RequiresMcRestart
	@Config.Comment("Optimizes World#getCollisionBoxes which often requires a lot of cpu time on servers.")
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
	@Config.Comment("Replaces the random update handler from Serene Seasons with a faster alternative.")
	public boolean patchSSRandomUpdateHandler = true;

	@Config.RequiresMcRestart
	@Config.Comment("Replaces the animation loader from FancyMenu with a faster alternative which also reduces memory usage significantly.")
	public boolean patchFMAnimationLoading = true;

	@Config.RequiresMcRestart
	@Config.Comment("Improves OTG's check for nearby structures which is slow and throws exceptions which are catched but ignored.")
	public boolean otgNearbyStructureCheck = true;

	@Config.RequiresMcRestart
	@Config.Comment("Prevents normally unnecessary structure recreation.")
	public boolean preventStructureRecreation = true;

	@Config.RequiresMcRestart
	@Config.Comment("Drastically reduces the time to load shaders in Optifine.")
	public boolean optifineFastShaderLoading = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fixes memory leak in EpicSiege's chunk cache class.")
	public boolean epicSiegeChunkCacheMemoryLeak = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fixes memory leak in ContentCreator's render util class.")
	public boolean contentCreatorRenderUtilMemoryLeak = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fix epic siege reducing damage of all mobs that are modified to 1.")
	public boolean epicSiegeReducedDamage = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fix infernal mobs modifiers targeting creative players.")
	public boolean infernalMobsTargetingCreative = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fix infernal mobs trying to make mobs infernal on reload.")
	public boolean infernalMobsInfernalOnReload = true;

	@Config.RequiresMcRestart
	@Config.Comment("Fixes craft tweaker's entity definition creation iterating over the entire entity registry.")
	public boolean craftTweakerFastEntityDefinition = true;

	@Config.RequiresMcRestart
	@Config.Comment("Changes OTG's toInternalName function to only work with valid entity id's which can improve loading times significantly.")
	public boolean otgFastInternalName = true;
}
