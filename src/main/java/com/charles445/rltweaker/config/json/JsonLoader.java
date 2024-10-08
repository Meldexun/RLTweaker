package com.charles445.rltweaker.config.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class JsonLoader {

	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapterFactory(new HashMultimapAdapterFactory())
			.registerTypeAdapter(ResourceLocation.class, MinecraftTypeAdapters.RESOURCE_LOCATION)
			.registerTypeAdapter(Block.class, MinecraftTypeAdapters.BLOCK)
			.registerTypeAdapter(IBlockState.class, new BlockStateSerializer())
			.setPrettyPrinting()
			.create();

	public static <T> T readJson(Path path, Type type, T defaultValue) throws JsonIOException, JsonSyntaxException, IOException {
		if (!Files.exists(path)) {
			writeJsonToFile(path, type, defaultValue);
		}
		return readJsonFromFile(path, type);
	}

	public static <T> T readJson(Path path, Class<? extends T> type, T defaultValue) throws JsonIOException, JsonSyntaxException, IOException {
		if (!Files.exists(path)) {
			writeJsonToFile(path, type, defaultValue);
		}
		return readJsonFromFile(path, type);
	}

	public static <T> T readJsonFromFile(Path path, Type type) throws JsonIOException, JsonSyntaxException, IOException {
		try (Reader in = Files.newBufferedReader(path)) {
			return GSON.fromJson(in, type);
		}
	}

	public static <T> T readJsonFromFile(Path path, Class<T> type) throws JsonIOException, JsonSyntaxException, IOException {
		try (Reader in = Files.newBufferedReader(path)) {
			return GSON.fromJson(in, type);
		}
	}

	public static void writeJsonToFile(Path path, Type type, Object src) throws JsonIOException, IOException {
		Files.createDirectories(path.getParent());
		try (Writer out = Files.newBufferedWriter(path)) {
			GSON.toJson(src, type, out);
		}
	}

	public static <V> Map<ResourceLocation, V> readJsons(Path dir, Class<V> type) throws JsonIOException, JsonSyntaxException, IOException {
		Map<ResourceLocation, V> map = new HashMap<>();
		if (!Files.exists(dir)) {
			return map;
		}
		for (Path subDir : Files.list(dir).filter(Files::isDirectory).toArray(Path[]::new)) {
			String modid = subDir.getFileName().toString();
			for (Path file : Files.list(subDir).filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json")).toArray(Path[]::new)) {
				String fileName = file.getFileName().toString();
				ResourceLocation key = new ResourceLocation(modid, fileName.substring(0, fileName.lastIndexOf('.')));
				V value;
				try {
					value = readJsonFromFile(file, type);
				} catch (JsonIOException e) {
					throw new JsonIOException("Failed reading json from file " + RLTweaker.jsonDirectory.relativize(file), e);
				} catch (JsonSyntaxException e) {
					throw new JsonSyntaxException("Failed reading json from file " + RLTweaker.jsonDirectory.relativize(file), e);
				} catch (IOException e) {
					throw new IOException("Failed reading json from file " + RLTweaker.jsonDirectory.relativize(file), e);
				}
				map.put(key, value);
			}
		}
		return map;
	}

}
