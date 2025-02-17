package com.charles445.rltweaker.asm;

import java.lang.reflect.Field;

import org.objectweb.asm.ClassWriter;

import com.charles445.rltweaker.asm.configloader.EarlyConfigLoader;
import com.charles445.rltweaker.asm.patch.PatchAggressiveMotionChecker;
import com.charles445.rltweaker.asm.patch.PatchAnvilDupe;
import com.charles445.rltweaker.asm.patch.PatchBetterCombatCriticalsFix;
import com.charles445.rltweaker.asm.patch.PatchBetterCombatMountFix;
import com.charles445.rltweaker.asm.patch.PatchBetterNether;
import com.charles445.rltweaker.asm.patch.PatchBountifulBaubles;
import com.charles445.rltweaker.asm.patch.PatchBroadcastSounds;
import com.charles445.rltweaker.asm.patch.PatchCleanStructureFiles;
import com.charles445.rltweaker.asm.patch.PatchConcurrentParticles;
import com.charles445.rltweaker.asm.patch.PatchCurePotion;
import com.charles445.rltweaker.asm.patch.PatchCustomAttributeInstances;
import com.charles445.rltweaker.asm.patch.PatchDoorPathfinding;
import com.charles445.rltweaker.asm.patch.PatchEnchant;
import com.charles445.rltweaker.asm.patch.PatchEntityBlockDestroy;
import com.charles445.rltweaker.asm.patch.PatchFallingBlockPortalDupe;
import com.charles445.rltweaker.asm.patch.PatchFasterBlockCollision;
import com.charles445.rltweaker.asm.patch.PatchHopper;
import com.charles445.rltweaker.asm.patch.PatchItemFrameDupe;
import com.charles445.rltweaker.asm.patch.PatchItemLinking;
import com.charles445.rltweaker.asm.patch.PatchLessCollisions;
import com.charles445.rltweaker.asm.patch.PatchLycanitesDupe;
import com.charles445.rltweaker.asm.patch.PatchOverlayMessage;
import com.charles445.rltweaker.asm.patch.PatchPathfindingChunkCache;
import com.charles445.rltweaker.asm.patch.PatchPotionCoreResistance;
import com.charles445.rltweaker.asm.patch.PatchPotionUpdate;
import com.charles445.rltweaker.asm.patch.PatchPushReaction;
import com.charles445.rltweaker.asm.patch.PatchRealBench;
import com.charles445.rltweaker.asm.patch.PatchReducedSearchSize;
import com.charles445.rltweaker.asm.patch.PatchRustic;
import com.charles445.rltweaker.asm.patch.PatchSRPAI;
import com.charles445.rltweaker.asm.patch.PatchWaystoneScroll;
import com.charles445.rltweaker.asm.patch.compat.PatchBrokenTransformers;
import com.charles445.rltweaker.asm.patch.compat.PatchCatServer;
import com.charles445.rltweaker.asm.patch.compat.PatchCraftBukkit;
import com.charles445.rltweaker.asm.patch.compat.PatchLootManagement;
import com.charles445.rltweaker.asm.patch.contentcreator.RenderUtilMemoryLeakPatch;
import com.charles445.rltweaker.asm.patch.crafttweaker.FastEntityDefinitionPatch;
import com.charles445.rltweaker.asm.patch.epicsiegemod.ChunkCacheMemoryLeakPatch;
import com.charles445.rltweaker.asm.patch.epicsiegemod.ReducedDamagePatch;
import com.charles445.rltweaker.asm.patch.fancymenu.PatchAnimationLoading;
import com.charles445.rltweaker.asm.patch.infernalmobs.InfernalOnReloadPatch;
import com.charles445.rltweaker.asm.patch.infernalmobs.InfernalTargetingCreativePatch;
import com.charles445.rltweaker.asm.patch.minecraft.PreventStructureRecreationPatch;
import com.charles445.rltweaker.asm.patch.optifine.FastShaderLoadingPatch;
import com.charles445.rltweaker.asm.patch.otg.ChunkGeneratorMemoryLeakPatch;
import com.charles445.rltweaker.asm.patch.otg.FastInternalNamePatch;
import com.charles445.rltweaker.asm.patch.otg.NearbyStructureCheckPatch;
import com.charles445.rltweaker.asm.patch.sereneseasons.PatchRandomUpdateHandler;
import com.charles445.rltweaker.config.PatchConfig;
import com.google.common.collect.BiMap;

import meldexun.asmutil2.HashMapClassNodeClassTransformer;
import meldexun.asmutil2.IClassTransformerRegistry;
import meldexun.asmutil2.NonLoadingClassWriter;
import meldexun.asmutil2.reader.ClassUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class RLTweakerASM extends HashMapClassNodeClassTransformer implements IClassTransformer {

	private static final ClassUtil.Configuration REMAPPING_CONFIGURATION;
	static {
		try {
			Field f = FMLDeobfuscatingRemapper.class.getDeclaredField("classNameBiMap");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			BiMap<String, String> classNameBiMap = (BiMap<String, String>) f.get(FMLDeobfuscatingRemapper.INSTANCE);
			REMAPPING_CONFIGURATION = new ClassUtil.Configuration(RLTweakerASM.class.getClassLoader(), classNameBiMap.inverse(), classNameBiMap);
		} catch (ReflectiveOperationException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	protected void registerTransformers(IClassTransformerRegistry registry) {
		PatchConfig config = EarlyConfigLoader.loadConfigEarly("rltweaker", "general.patches", new PatchConfig());

		if (!config.ENABLED) {
			return;
		}

		if (config.aggressiveMotionChecker) PatchAggressiveMotionChecker.registerTransformers(registry);
		if (config.betterCombatCriticalsFix) PatchBetterCombatCriticalsFix.registerTransformers(registry);
		if (config.betterCombatMountFix) PatchBetterCombatMountFix.registerTransformers(registry);
		if (config.contentCreatorRenderUtilMemoryLeak) RenderUtilMemoryLeakPatch.registerTransformers(registry);
		if (config.craftTweakerFastEntityDefinition) FastEntityDefinitionPatch.registerTransformers(registry);
		if (config.doorPathfindingFix) PatchDoorPathfinding.registerTransformers(registry);
		if (config.epicSiegeChunkCacheMemoryLeak) ChunkCacheMemoryLeakPatch.registerTransformers(registry);
		if (config.epicSiegeReducedDamage) ReducedDamagePatch.registerTransformers(registry);
		if (config.fixWaystoneScrolls) PatchWaystoneScroll.registerTransformers(registry);
		if (config.infernalMobsInfernalOnReload) InfernalOnReloadPatch.registerTransformers(registry);
		if (config.infernalMobsTargetingCreative) InfernalTargetingCreativePatch.registerTransformers(registry);
		if (config.lessCollisions) PatchLessCollisions.registerTransformers(registry);
		if (config.lycanitesPetDupeFix) PatchLycanitesDupe.registerTransformers(registry);
		if (config.optifineFastShaderLoading) FastShaderLoadingPatch.registerTransformers(registry);
		if (config.otgChunkGeneratorMemoryLeak) ChunkGeneratorMemoryLeakPatch.registerTransformers(registry);
		if (config.otgFastInternalName) FastInternalNamePatch.registerTransformers(registry);
		if (config.otgNearbyStructureCheck) NearbyStructureCheckPatch.registerTransformers(registry);
		if (config.particleThreading) PatchConcurrentParticles.registerTransformers(registry);
		if (config.patchAnvilDupe) PatchAnvilDupe.registerTransformers(registry);
		if (config.patchBroadcastSounds) PatchBroadcastSounds.registerTransformers(registry);
		if (config.patchChestOfDrawers) PatchBetterNether.registerTransformers(registry);
		if (config.patchCleanupStructureWorldgenFiles) PatchCleanStructureFiles.registerTransformers(registry);
		if (config.patchCurePotion) PatchCurePotion.registerTransformers(registry);
		if (config.patchCustomAttributeInstances) PatchCustomAttributeInstances.registerTransformers(registry);
		if (config.patchEnchantments) PatchEnchant.registerTransformers(registry);
		if (config.patchEntityBlockDestroy) PatchEntityBlockDestroy.registerTransformers(registry);
		if (config.patchFMAnimationLoading) PatchAnimationLoading.registerTransformers(registry);
		if (config.patchFallingBlockPortalDupe) PatchFallingBlockPortalDupe.registerTransformers(registry);
		if (config.patchFasterBlockCollision) PatchFasterBlockCollision.registerTransformers(registry);
		if (config.patchHopper) PatchHopper.registerTransformers(registry);
		if (config.patchItemFrameDupe) PatchItemFrameDupe.registerTransformers(registry);
		if (config.patchItemLinking) PatchItemLinking.registerTransformers(registry);
		if (config.patchModifierBooks) PatchBountifulBaubles.registerTransformers(registry);
		if (config.patchOverlayMessage) PatchOverlayMessage.registerTransformers(registry);
		if (config.patchPotionCoreResistance) PatchPotionCoreResistance.registerTransformers(registry);
		if (config.patchPotionEntityTrackerUpdate) PatchPotionUpdate.registerTransformers(registry);
		if (config.patchPushReaction) PatchPushReaction.registerTransformers(registry);
		if (config.patchRusticWineEffects) PatchRustic.registerTransformers(registry);
		if (config.patchSRPAI) PatchSRPAI.registerTransformers(registry);
		if (config.patchSSRandomUpdateHandler) PatchRandomUpdateHandler.registerTransformers(registry);
		if (config.pathfindingChunkCacheFix) PatchPathfindingChunkCache.registerTransformers(registry);
		if (config.preventStructureRecreation) PreventStructureRecreationPatch.registerTransformers(registry);
		if (config.realBenchDupeBugFix) PatchRealBench.registerTransformers(registry);
		if (config.reducedSearchSize) PatchReducedSearchSize.registerTransformers(registry);
		if (config.serverCompatibility) {
			boolean hasSponge = false;
			boolean catServer = false;
			boolean mohist = false;
			try {
				Class.forName("org.spongepowered.mod.SpongeMod", false, RLTweakerASM.class.getClassLoader());
				hasSponge = true;
			} catch (ClassNotFoundException e) {
				// ignore
			}
			try {
				Class.forName("catserver.server.CatServer", false, RLTweakerASM.class.getClassLoader());
				catServer = true;
			} catch (ClassNotFoundException e) {
				// ignore
			}
			try {
				Class.forName("com.mohistmc.MohistMC", false, RLTweakerASM.class.getClassLoader());
				mohist = true;
			} catch (ClassNotFoundException e) {
				// ignore
			}
			if (hasSponge || catServer || mohist) {
				PatchLootManagement.registerTransformers(registry);
			}
			if (catServer || mohist) {
				PatchBrokenTransformers.registerTransformers(registry);
				PatchCraftBukkit.registerTransformers(registry);
			}
			if (catServer) {
				PatchCatServer.registerTransformers(registry);
			}
		}
	}

	@Override
	protected ClassWriter createClassWriter(int flags) {
		return new NonLoadingClassWriter(flags) {
			@Override
			protected ClassUtil.Configuration getClassUtilConfiguration() {
				return REMAPPING_CONFIGURATION;
			}
		};
	}

}
