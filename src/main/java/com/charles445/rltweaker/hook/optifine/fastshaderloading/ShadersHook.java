package com.charles445.rltweaker.hook.optifine.fastshaderloading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimaps;

import meldexun.reflectionutil.ReflectionField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLLog;
import net.optifine.util.TextureUtils;

public class ShadersHook {

	public static ModelLoader modelLoader;
	private static final ReflectionField<RegistrySimple<ModelResourceLocation, IBakedModel>> f_bakedRegistry = new ReflectionField<>(ModelBakery.class, "field_177605_n", "bakedRegistry");
	private static final ReflectionField<Map<ModelResourceLocation, IBakedModel>> f_registryObjects = new ReflectionField<>(RegistrySimple.class, "field_82596_a", "registryObjects");
	private static final ReflectionField<Map<ModelResourceLocation, IModel>> f_stateModels = new ReflectionField<>(ModelLoader.class, "stateModels", "");
	private static final ReflectionField<IModel> f_missingModel = new ReflectionField<>(ModelLoader.class, "missingModel", "");
	private static final Function<ResourceLocation, TextureAtlasSprite> TEXTURE_GETTER = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

	private static final ReflectionField<ModelManager> f_modelManager = new ReflectionField<>(Minecraft.class, "field_175617_aL", "modelManager");
	private static final ReflectionField<IBakedModel> f_defaultModel = new ReflectionField<>(ModelManager.class, "field_174955_d", "defaultModel");
	private static final ReflectionField<BlockModelShapes> f_modelProvider = new ReflectionField<>(ModelManager.class, "field_174957_c", "modelProvider");

	private static final ReflectionField<Map<?, ?>> f_baseQuads = new ReflectionField<>("mirror.normalasm.client.models.bucket.NormalBakedDynBucket", "baseQuads", "");
	private static final ReflectionField<Map<?, ?>> f_flippedBaseQuads = new ReflectionField<>("mirror.normalasm.client.models.bucket.NormalBakedDynBucket", "flippedBaseQuads", "");
	private static final ReflectionField<Map<?, ?>> f_coverQuads = new ReflectionField<>("mirror.normalasm.client.models.bucket.NormalBakedDynBucket", "coverQuads", "");
	private static final ReflectionField<Map<?, ?>> f_flippedCoverQuads = new ReflectionField<>("mirror.normalasm.client.models.bucket.NormalBakedDynBucket", "flippedCoverQuads", "");

	public static Object reloadResources(Minecraft mc) {
		if (modelLoader != null) {
			RegistrySimple<ModelResourceLocation, IBakedModel> bakedRegistry = f_bakedRegistry.get(modelLoader);
			IModel missingModel = f_missingModel.get(modelLoader);
			IBakedModel missingBaked = missingModel.bake(missingModel.getDefaultState(), DefaultVertexFormats.ITEM, TEXTURE_GETTER);

			f_registryObjects.set(bakedRegistry, new ConcurrentHashMap<>());
			Multimaps.invertFrom(Multimaps.forMap(f_stateModels.get(modelLoader)), ArrayListMultimap.create())
					.asMap()
					.entrySet()
					.parallelStream()
					.forEach(entry -> {
						IModel model = entry.getKey();
						Collection<ModelResourceLocation> locations = entry.getValue();
						IBakedModel bakedModel;
						if (model == missingModel) {
							bakedModel = missingBaked;
						} else {
							try {
								bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, TEXTURE_GETTER);
							} catch (Exception e) {
								FMLLog.log.error("Exception baking model for location(s) {}:", locations, e);
								bakedModel = missingBaked;
							}
						}
						for (ModelResourceLocation location : locations) {
							bakedRegistry.putObject(location, bakedModel);
						}
					});

			ModelManager modelManager = f_modelManager.get(mc);
			if (modelManager != null) {
				f_defaultModel.set(modelManager, missingBaked);
				ForgeHooksClient.onModelBake(modelManager, bakedRegistry, modelLoader);
				f_modelProvider.get(modelManager).reloadModels();
			}
		}

		mc.getRenderItem().onResourceManagerReload(mc.getResourceManager());

		TextureUtils.resourcesReloaded(mc.getResourceManager());

		if (f_baseQuads.isPresent()) f_baseQuads.get(null).clear();
		if (f_flippedBaseQuads.isPresent()) f_flippedBaseQuads.get(null).clear();
		if (f_coverQuads.isPresent()) f_coverQuads.get(null).clear();
		if (f_flippedCoverQuads.isPresent()) f_flippedCoverQuads.get(null).clear();

		if (mc.renderGlobal != null) {
			mc.renderGlobal.loadRenderers();
		}

		return null;
	}

}
