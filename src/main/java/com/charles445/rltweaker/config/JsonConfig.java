package com.charles445.rltweaker.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig.JsonFileException;
import com.charles445.rltweaker.config.init.JsonConfigLessCollisions;
import com.charles445.rltweaker.config.json.JsonDoubleBlockState;
import com.charles445.rltweaker.config.json.JsonFileName;
import com.charles445.rltweaker.config.json.JsonTypeToken;
import com.charles445.rltweaker.entity.ai.InvestigateAIConfig;
import com.charles445.rltweaker.handler.ReskillableHandler;
import com.charles445.rltweaker.util.CollisionUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

public class JsonConfig
{
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new TypeAdapter<ResourceLocation>()
	{
		@Override
		public void write(JsonWriter out, ResourceLocation value) throws IOException
		{
			TypeAdapters.STRING.write(out, value.toString());
		}

		@Override
		public ResourceLocation read(JsonReader in) throws IOException
		{
			return new ResourceLocation(TypeAdapters.STRING.read(in));
		}
	}).setPrettyPrinting().create();
	
	public static Map<String, Double> lessCollisions = new HashMap<>();
	public static Map<String, List<JsonDoubleBlockState>> reskillableTransmutation = new HashMap<>();
	public static Map<ResourceLocation, InvestigateAIConfig> investigateAI = new HashMap<>();
	
	public static void init()
	{
		if(ModConfig.patches.lessCollisions)
		{
			lessCollisions.putAll(JsonConfigLessCollisions.getDefaults());
			
			Map<String, Double> lcJson = processJson(JsonFileName.lessCollisions, lessCollisions, true);
			if(lcJson!=null)
			{
				try
				{
					lessCollisions.putAll(lcJson);
					manuallyWriteToJson(JsonFileName.lessCollisions, lessCollisions);
				}
				catch(Exception e)
				{
					RLTweaker.logger.error("Failed to merge write lessCollisions!");
					ErrorUtil.logSilent("JSON Merge Write LessCollisions");
				}
			}
			
			if(lessCollisions == null)
				lessCollisions = new HashMap<>();
			
			CollisionUtil.instance.addToStringReference(lessCollisions);
		}
		
		if(Loader.isModLoaded(ModNames.RESKILLABLE) && ModConfig.server.reskillable.enabled && ModConfig.server.reskillable.customTransmutation)
		{
			reskillableTransmutation.clear();
			reskillableTransmutation.put("minecraft:stick", Arrays.asList(new JsonDoubleBlockState[]{JsonDoubleBlockState.AIR}));
			
			reskillableTransmutation = processJson(JsonFileName.reskillableTransmutation, reskillableTransmutation, false);
			if(reskillableTransmutation!=null)
			{
				Object reskillableHandler = RLTweaker.handlers.get(ModNames.RESKILLABLE);
				if(reskillableHandler instanceof ReskillableHandler)
				{
					((ReskillableHandler)reskillableHandler).registerTransmutations();
				}
			}
		}
		
		try
		{
			loadInvestigateAIConfig();
		}
		catch(JsonFileException e)
		{
			RLTweaker.logger.error("Failed to load investigate AI file '{}'", e.getFile(), e);
		}
		catch(IOException e)
		{
			RLTweaker.logger.error("Failed to load investigate AI config files", e);
		}
	}
	
	public static void loadInvestigateAIConfig() throws JsonFileException, IOException
	{
		investigateAI = readJsons(RLTweaker.jsonDirectory.resolve("investigateAI"), InvestigateAIConfig.class);
	}
	
	/** Nullable when forMerging is true */
	@Nullable
	public static <T> T processJson(JsonFileName jfn, final T container, boolean forMerging)
	{
		try
		{
			return processUncaughtJson(jfn, container, forMerging);
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Error managing JSON File: "+jfn.get(), e);
			ErrorUtil.logSilent("JSON Error: "+jfn.get());
			if(forMerging)
			{
				return null;
			}
			else
			{
				return container;
			}
		}
	}
	
	@Nullable
	public static <T> T processUncaughtJson(JsonFileName jfn, final T container, boolean forMerging) throws Exception
	{
		String jsonFileName = jfn.get();
		Type type = JsonTypeToken.get(jfn);
		
		Path jsonFile = RLTweaker.jsonDirectory.resolve(jsonFileName);
		if(Files.exists(jsonFile))
		{
			//Read
			return readJsonFromFile(jsonFile, type);
		}
		else
		{
			//Write
			writeJsonToFile(jsonFile, type, container);
			
			if(forMerging)
			{
				return null;
			}
			else
			{
				return container;
			}
		}
	}
	
	private static <T> void manuallyWriteToJson(JsonFileName jfn, final T container) throws Exception
	{
		String jsonFileName = jfn.get();
		Type type = JsonTypeToken.get(jfn);
		
		Path jsonFile = RLTweaker.jsonDirectory.resolve(jsonFileName);
		writeJsonToFile(jsonFile, type, container);
	}
	
	private static <T> T readJsonFromFile(Path path, Type type) throws JsonIOException, JsonSyntaxException, IOException
	{
		try(Reader in = Files.newBufferedReader(path))
		{
			return GSON.fromJson(in, type);
		}
	}
	
	private static <T> T readJsonFromFile(Path path, Class<T> type) throws JsonIOException, JsonSyntaxException, IOException
	{
		try(Reader in = Files.newBufferedReader(path))
		{
			return GSON.fromJson(in, type);
		}
	}
	
	private static void writeJsonToFile(Path path, Type type, Object src) throws JsonIOException, IOException
	{
		Files.createDirectories(path.getParent());
		try(Writer out = Files.newBufferedWriter(path))
		{
			GSON.toJson(src, type, out);
		}
	}
	
	private static <V> Map<ResourceLocation, V> readJsons(Path dir, Class<V> type) throws JsonFileException, IOException
	{
		Map<ResourceLocation, V> map = new HashMap<>();
		if(!Files.exists(dir))
		{
			return map;
		}
		for(Path subDir : Files.list(dir).filter(Files::isDirectory).toArray(Path[]::new))
		{
			String modid = subDir.getFileName().toString();
			for(Path file : Files.list(subDir).filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json")).toArray(Path[]::new))
			{
				String fileName = file.getFileName().toString();
				ResourceLocation key = new ResourceLocation(modid, fileName.substring(0, fileName.lastIndexOf('.')));
				V value;
				try
				{
					value = readJsonFromFile(file, type);
				}
				catch(JsonIOException | JsonSyntaxException e)
				{
					throw new JsonFileException(dir.relativize(file), e);
				}
				map.put(key, value);
			}
		}
		return map;
	}
	
	@SuppressWarnings("serial")
	public static class JsonFileException extends Exception
	{
		
		private final Path file;
		
		public JsonFileException(Path file, Throwable cause)
		{
			super(cause);
			this.file = file;
		}
		
		public Path getFile()
		{
			return file;
		}
		
	}
}
