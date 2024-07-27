package com.charles445.rltweaker.config.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

class BlockStateSerializer implements JsonSerializer<IBlockState>, JsonDeserializer<IBlockState> {

	@Override
	public JsonElement serialize(IBlockState src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		Block block = src.getBlock();
		json.add("id", context.serialize(block, Block.class));
		json.addProperty("meta", block.getMetaFromState(src));
		return json;
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		Block block = context.deserialize(jsonObject.get("id"), Block.class);
		int meta = getInt(jsonObject, "meta", 0);
		return block.getStateFromMeta(meta);
	}

	private static int getInt(JsonObject json, String key, int defaultValue) {
		JsonElement element = json.get(key);
		if (element == null) {
			return defaultValue;
		}
		return element.getAsInt();
	}

}
