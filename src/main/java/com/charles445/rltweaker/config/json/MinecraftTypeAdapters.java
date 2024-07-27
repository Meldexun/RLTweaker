package com.charles445.rltweaker.config.json;

import java.io.IOException;
import java.util.function.Function;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

class MinecraftTypeAdapters {

	static final TypeAdapter<ResourceLocation> RESOURCE_LOCATION = mapTypeAdapter(TypeAdapters.STRING, ResourceLocation::toString, ResourceLocation::new);
	static final TypeAdapter<Block> BLOCK = mapTypeAdapter(RESOURCE_LOCATION, Block::getRegistryName, ForgeRegistries.BLOCKS::getValue);

	private static <T, R> TypeAdapter<T> mapTypeAdapter(TypeAdapter<R> mappedTypeAdapater, Function<T, R> mapFunction, Function<R, T> remapFunction) {
		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				mappedTypeAdapater.write(out, mapFunction.apply(value));
			}

			@Override
			public T read(JsonReader in) throws IOException {
				return remapFunction.apply(mappedTypeAdapater.read(in));
			}
		};
	}

}
