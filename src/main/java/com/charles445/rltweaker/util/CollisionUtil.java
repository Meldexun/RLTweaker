package com.charles445.rltweaker.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.config.init.JsonConfigLessCollisions;
import com.google.gson.reflect.TypeToken;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public final class CollisionUtil {

	private static final Path FILE_NAME = Paths.get("lessCollisions.json");
	private static final Type TYPE = new TypeToken<Object2DoubleOpenHashMap<String>>() {}.getType();
	private static Object2DoubleMap<String> lessCollisions = Object2DoubleMaps.emptyMap();

	public static void loadConfig() {
		if (!ModConfig.patches.lessCollisions) {
			return;
		}

		try {
			lessCollisions = JsonConfig.readJson(RLTweaker.jsonDirectory.resolve(FILE_NAME), TYPE, JsonConfigLessCollisions.getDefaults());
		} catch (IOException e) {
			RLTweaker.logger.error("Failed to load less collisions config", e);
		}
	}

	public static double getRadiusForEntity(@Nonnull Entity entity) {
		double v = lessCollisions.getDouble(entity.getClass().getName());
		if (v < 0.0D || v > World.MAX_ENTITY_RADIUS) {
			v = World.MAX_ENTITY_RADIUS;
		}
		return v;
	}

}
