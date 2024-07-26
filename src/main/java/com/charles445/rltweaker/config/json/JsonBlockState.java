package com.charles445.rltweaker.config.json;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;
import com.google.gson.annotations.SerializedName;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class JsonBlockState
{
	public static final JsonBlockState AIR = new JsonBlockState("minecraft:air");
	
	@SerializedName("id")
	public String registryName;
	
	public int metadata;
	
	private transient IBlockState asBlockState;
	
	public JsonBlockState(String registryName)
	{
		this.registryName = registryName;
		this.metadata = -1;

		this.asBlockState = getAsBlockState();
		
		this.metadata = asBlockState.getBlock().getMetaFromState(asBlockState);
	}
	
	public JsonBlockState(String registryName, int metadata)
	{
		this.registryName = registryName;
		this.metadata = metadata;
		
		this.asBlockState = getAsBlockState();
	}
	
	@SuppressWarnings("deprecation")
	public IBlockState getAsBlockState()
	{
		if(asBlockState==null)
		{
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
			
			if(block==null)
			{
				RLTweaker.logger.warn("Couldn't getAsBlockState resource: "+registryName);
				ErrorUtil.logSilent("JsonBlockState getAsBlockState");
				return Blocks.AIR.getDefaultState();
			}
			
			//State from meta?
			if(this.metadata!=-1)
			{
				//Get with metadata
				return block.getStateFromMeta(this.metadata);
			}
			else
			{
				//Get default
				return block.getDefaultState();
			}
		}
		else
		{
			return asBlockState;
		}
	}
}
