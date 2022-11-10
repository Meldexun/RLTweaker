package com.charles445.rltweaker.config.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.charles445.rltweaker.entity.ai.InvestigateAIConfig;
import com.google.gson.reflect.TypeToken;

import net.minecraft.util.ResourceLocation;

public class JsonTypeToken
{
	public static Type get(JsonFileName jcfn)
	{
		switch(jcfn)
		{
			case lessCollisions: return new TypeToken<Map<String, Double>>(){}.getType();
			case reskillableTransmutation: return new TypeToken<Map<String, List<JsonDoubleBlockState>>>(){}.getType();
			case investigateAI: return new TypeToken<Map<String, InvestigateAIConfig>>(){}.getType();
		
			default:
				return null;
		}
	}
}
