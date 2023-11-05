package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import com.charles445.rltweaker.hook.StructureCleanupMode;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeDouble;

public class ConfigMinecraft
{
	@Config.Comment("Debug mode, sends messages to all players and otherwise spams, do not enable unless you are privately testing!")
	@Config.Name("Debug Mode")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean debug = false;
	
	@Config.Comment("Aggressively checks for invalid living entity movement and attempts to fix it")
	@Config.Name("Motion Checker")
	@RLConfig.ImprovementsOnly("true")	//Very helpful in other packs, honestly, I see plenty of modded mobs getting NaN rotation
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean motionChecker = true;
	
	@Config.Comment("Speed cap for the motion checker, living entities are not allowed to move faster than this")
	@Config.Name("Motion Checker Speed Cap")
	@Config.RangeDouble(min=1.0D)
	@RLConfig.ImprovementsOnly("96.0")
	@RLConfig.RLCraftTwoEightTwo("48.0")
	@RLConfig.RLCraftTwoNine("96.0")
	public double motionCheckerSpeedCap = 96.0D;
	
	@Config.Comment("Synchronizes dismounts with players more aggressively")
	@Config.Name("Player Dismount Sync")
	@RLConfig.ImprovementsOnly("true")	//TODO finish implementing the delayed mount registration to avoid rare compatibility issues in other packs
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean playerDismountSync = true;
	
	@Config.Comment("Synchronizes arrows with players more aggressively")
	@Config.Name("Player Arrow Sync")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean playerArrowSync = true;
	
	@Config.Comment("Removes some blacksmith chest loot to match TAN")
	@Config.Name("Blacksmith Chest Tweak")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false") //And I did all this work and everything!
	public boolean blacksmithChestTweak = false;
	
	@Config.Comment("In Minecraft 1.2.5, players who get knocked back have their camera tilted in the direction of the attack.")
	@Config.Name("Damage Tilt Effect")
	@RLConfig.ImprovementsOnly("false")	//If people want it they can turn it on, no need to force it in front of them
	@RLConfig.RLCraftTwoEightTwo("true") //Except people who update their tweakers for 2.8.2 servers then heck yeah
	@RLConfig.RLCraftTwoNine("true")
	public boolean damageTilt = false;
	
	@Config.Comment("Requires lessCollisions patch, change that config value instead, don't change this unless you are benchmarking")
	@Config.Name("Less Collisions")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean lessCollisions = true;

	@Config.Comment("Cleans up structure .dat files regularly to lower RAM usage. May break mods that use this data. (Stronghold, Village, Mineshaft, Temple, Monument, Mansion, Fortress, EndCity)")
	@Config.Name("Cleanup Structure Worldgen Files Structures")
	public String[] cleanupStructureWorldgenFilesStructures = { "Village", "Mineshaft" };

	@Config.Comment({
			"ALWAYS: All structures get always deleted",
			"ALWAYS_COMPONENTS_ONLY: All components get always deleted",
			"GENERATED: Structures that are fully generated get deleted",
			"GENERATED_COMPONENTS: Components that are fully generated and structures that have no components get deleted",
			"GENERATED_COMPONENTS_ONLY: Components that are fully generated get deleted",
			"DISABLED: Nothing gets deleted" })
	@Config.Name("Cleanup Structure Worldgen Files Mode")
	public StructureCleanupMode cleanupStructureWorldgenFilesMode = StructureCleanupMode.GENERATED_COMPONENTS_ONLY;

	@Config.Comment("Structure (or structure component) bounding boxes larger than this value on one axis will be ignored.")
	@Config.Name("Cleanup Structure Worldgen Files Size Limit")
	public int cleanupStructureWorldgenFilesSizeLimit = 1 << 12;

	@Config.Comment("Replace thrown witch potions with configured potions")
	@Config.Name("Witch Potion Replacements")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean witchPotionReplacements = false;
	
	@Config.Comment("Replace thrown witch harming potions with configured potions")
	@Config.Name("Witch Potion Replacements - Harming")
	@RLConfig.ImprovementsOnly("minecraft:harming")
	@RLConfig.RLCraftTwoEightTwo("minecraft:harming")
	@RLConfig.RLCraftTwoNine("minecraft:harming")
	public String[] witchHarmingReplacements = {"minecraft:harming"};
	
	@Config.Comment("Replace thrown witch slowness potions with configured potions")
	@Config.Name("Witch Potion Replacements - Slowness")
	@RLConfig.ImprovementsOnly("minecraft:slowness")
	@RLConfig.RLCraftTwoEightTwo("minecraft:slowness")
	@RLConfig.RLCraftTwoNine("minecraft:slowness")
	public String[] witchSlownessReplacements = {"minecraft:slowness"};
	
	@Config.Comment("Replace thrown witch poison potions with configured potions")
	@Config.Name("Witch Potion Replacements - Poison")
	@RLConfig.ImprovementsOnly("minecraft:poison")
	@RLConfig.RLCraftTwoEightTwo("minecraft:poison")
	@RLConfig.RLCraftTwoNine("minecraft:poison")
	public String[] witchPoisonReplacements = {"minecraft:poison"};
	
	@Config.Comment("Replace thrown witch weakness potions with configured potions")
	@Config.Name("Witch Potion Replacements - Weakness")
	@RLConfig.ImprovementsOnly("minecraft:weakness")
	@RLConfig.RLCraftTwoEightTwo("minecraft:weakness")
	@RLConfig.RLCraftTwoNine("minecraft:weakness")
	public String[] witchWeaknessReplacements = {"minecraft:weakness"};
	
	@Config.Comment("Allows all zombies to break doors")
	@Config.Name("All Zombies Break Doors")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("true")
	public boolean allZombiesBreakDoors = false;
	
	@Config.Comment("Distance in chunks lightning can be heard by a player. Default setting disables this tweak")
	@Config.Name("Lightning Sound Chunk Distance")
	@RangeDouble(min=2.0d, max=3000000.0d)
	@RLConfig.ImprovementsOnly("10000.0")
	@RLConfig.RLCraftTwoEightTwo("10000.0")
	@RLConfig.RLCraftTwoNine("100.0")
	public double lightningSoundChunkDistance = 10000.0d;
	
	@Config.Comment("REQUIRES Patch patchBroadcastSounds. Globally broadcasted sounds are only sent to players this close by, in blocks. Ignores dimension.")
	@Config.Name("Broadcasted Sounds Distance Limit")
	@RangeDouble(min=2.0d, max=40000000d)
	@RLConfig.ImprovementsOnly("1000.0")
	@RLConfig.RLCraftTwoEightTwo("1000.0")
	@RLConfig.RLCraftTwoNine("1000.0")
	public double broadcastedSoundsDistanceLimit = 1000.0d;
	
	@Config.Comment("REQUIRES Patch patchEnchantments. Blacklisted enchantments do not show up as random enchantments. May still show up via other mods.")
	@Config.Name("Enchantment Blacklist")
	@RLConfig.ImprovementsOnly("examplemod:enchantment")
	@RLConfig.RLCraftTwoEightTwo("examplemod:enchantment")
	@RLConfig.RLCraftTwoNine("examplemod:enchantment")
	public String[] blacklistedEnchantments = {"examplemod:enchantment"};
	
	@Config.Comment("Container class names to enforce player distance (to prevent dupes and other glitchy behavior). Must be the full qualified class name of the containers.")
	@Config.Name("Container Distance Classes")
	@RLConfig.ImprovementsOnly("examplemod.container.ExampleContainer")
	@RLConfig.RLCraftTwoEightTwo("examplemod.container.ExampleContainer")
	@RLConfig.RLCraftTwoNine("examplemod.container.ExampleContainer")
	public String[] containerDistanceClasses = {"examplemod.container.ExampleContainer"};
	
	@Config.Comment("REQUIRES patch patchEntityBlockDestroy. Prevents entities from destroying these blocks.")
	@Config.Name("Entity Block Destroy Blacklist")
	@RLConfig.ImprovementsOnly("examplemod:block")
	@RLConfig.RLCraftTwoEightTwo("examplemod:block")
	@RLConfig.RLCraftTwoNine("bountiful:bountyboard|waystones:waystone")
	public String[] entityBlockDestroyBlacklist = {"examplemod:block"};
	
	@Config.Comment("REQUIRES patch patchPushReaction. Prevents specified entities from being pushed by pistons.")
	@Config.Name("Entity Push Prevention")
	@RLConfig.ImprovementsOnly("examplemod:entity")
	@RLConfig.RLCraftTwoEightTwo("examplemod:entity")
	@RLConfig.RLCraftTwoNine("battletowers:golem")
	public String[] entityPushPrevention = {"examplemod:entity"};
	
	@Config.Comment("REQUIRES patch patchHopper. Prevents hoppers from pulling from or inserting into specific blocks.")
	@Config.Name("Hopper Block Blacklist")
	@RLConfig.ImprovementsOnly("examplemod:block")
	@RLConfig.RLCraftTwoEightTwo("examplemod:block")
	@RLConfig.RLCraftTwoNine("bountiful:bountyboard") //TODO fix all these
	public String[] hopperBlockBlacklist = {"examplemod:block"};
	
	@Config.Comment("Whether lightning should destroy items")
	@Config.Name("Lightning Destroys Items")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("false") //Already handled by ISeeDragons
	@RLConfig.RLCraftTwoNine("false") //Already handled by ISeeDragons
	public boolean lightningDestroysItems = true;
	
	@Config.Comment("Enables watchdog related features")
	@Config.Name("Watchdog")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean watchdog = true;

	@Config.Comment("Enables investigate AI for all entities defined in the 'investigateAI.json' file.")
	@Config.Name("Investigate AI")
	@Config.RequiresMcRestart
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean investigateAi = false;

	@Config.Comment("Enables investigate AI for non-player attacks. Might cause lags.")
	@Config.Name("Investigate non-player Attacks")
	@RLConfig.ImprovementsOnly("false")
	@RLConfig.RLCraftTwoEightTwo("false")
	@RLConfig.RLCraftTwoNine("false")
	public boolean investigateNonPlayerAttacks = false;

	@Config.Comment("Enables investigate AI for non-player attacks. Might cause lags.")
	@Config.Name("Investigate non-player Attacks")
	@RLConfig.ImprovementsOnly("true")
	@RLConfig.RLCraftTwoEightTwo("true")
	@RLConfig.RLCraftTwoNine("true")
	public boolean fixDropDisconnectLagDupe = true;

}
