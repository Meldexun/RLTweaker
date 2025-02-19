package com.charles445.rltweaker.hook.compat;

import java.io.File;
import java.lang.reflect.Field;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.DimensionManager;

public class HookLootManagement
{
	private static Field f_LootTableManager_baseFolder = ReflectUtil.findFieldAnyOrNull(LootTableManager.class, "field_186528_d", "baseFolder");
	
	//com/charles445/rltweaker/hook/compat/HookLootManagement
	//fixLootFilePath
	//(Ljava/io/File;Lnet/minecraft/util/ResourceLocation;)Ljava/io/File;
	public static File fixLootFilePath(File file, ResourceLocation resource)
	{
		if(!file.exists())
		{
			if(f_LootTableManager_baseFolder == null)
			{
				ErrorUtil.logSilent("Loot Management LootTableManager baseFolder Setup");
				return file;
			}
			
			World world = DimensionManager.getWorld(0);
			
			if(world == null)
				return file;
			
			LootTableManager manager = world.getLootTableManager();
			
			if(manager == null)
				return file;
			
			try
			{
				File baseFolder = (File) f_LootTableManager_baseFolder.get(manager);
				if(baseFolder == null)
					return file;
				
				File fileOverworld = new File(new File(baseFolder, resource.getNamespace()), resource.getPath() + ".json");
				
				if(fileOverworld.exists() && fileOverworld.isFile())
					return fileOverworld;
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				ErrorUtil.logSilent("Loot Management LootTableManager baseFolder Invoke");
			}
		}
		
		return file;
	}
}
