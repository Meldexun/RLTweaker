package com.charles445.rltweaker.command;

import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ModNames;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class CommandAdvisor extends CommandBase
{
	@Override
	public String getName()
	{
		return "rladvisor";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/rladvisor";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		//Advise the user on what to change in the config
		World world = sender.getEntityWorld();
		
		inform("RLTweaker Advisor", sender);
		
		informPatches(server, sender, world);
		
	}
	
	public void informPatches(MinecraftServer server, ICommandSender sender, World world)
	{	
		//Patches
		boolean bigRadius = world.MAX_ENTITY_RADIUS > 2.0d;
		
		if(!ModConfig.patches.particleThreading)
		{
			inform("Safety patch, set patches / particleThreading to true", sender);
		}
		
		if(ModConfig.patches.lessCollisions && !bigRadius)
		{
			inform("Unnecessary patch, set patches / lessCollisions to false", sender);
		}
		else if(!ModConfig.patches.lessCollisions && bigRadius)
		{
			inform("Performance patch, set patches / lessCollisions to true", sender);
		}
		
		if(!ModConfig.patches.betterCombatMountFix && Loader.isModLoaded(ModNames.BETTERCOMBAT))
		{
			inform("Improvement patch, set patches / betterCombatMountFix to true", sender);
		}
		
		if(!ModConfig.patches.realBenchDupeBugFix)
		{
			try
			{
				Class.forName("pw.prok.realbench.WorkbenchTile");
				inform("Improvement patch, set patches / realBenchDupeBugFix to true", sender);
			}
			catch(Exception e)
			{
				//Nothing
			}
		}
		
		//lycanitesPetDupeFix //temporary, don't give advice
		
		if(!ModConfig.patches.doorPathfindingFix)
		{
			inform("Improvement patch, set patches / doorPathfindingFix to true", sender);
		}
		
		if(ModConfig.patches.reducedSearchSize && !bigRadius)
		{
			inform("Unnecessary patch, set patches / reducedSearchSize to false", sender);
		}
		else if(!ModConfig.patches.reducedSearchSize && bigRadius)
		{
			inform("Performance patch, set patches / reducedSearchSize to true", sender);
		}
		
		if(!ModConfig.patches.patchBroadcastSounds)
		{
			inform("Optional patch, set patches / patchBroadcastSounds to true", sender);
		}
		
		if(ModConfig.patches.patchEnchantments && hasExamplesOnly(ModConfig.server.minecraft.blacklistedEnchantments))
		{
			inform("Unnecessary patch, set patches / patchEnchantments to false", sender);
		}
		else if(!ModConfig.patches.patchEnchantments && !hasExamplesOnly(ModConfig.server.minecraft.blacklistedEnchantments))
		{
			inform("Necessary patch for blacklisting enchantments, set patches / patchEnchantments to true", sender);
		}
		
		if(!ModConfig.patches.aggressiveMotionChecker)
		{
			inform("Improvement patch, set patches / aggressiveMotionChecker to true", sender);
		}
		
		if(ModConfig.patches.patchEntityBlockDestroy && hasExamplesOnly(ModConfig.server.minecraft.entityBlockDestroyBlacklist))
		{
			inform("Unnecessary patch, set patches / patchEntityBlockDestroy to false", sender);
		}
		else if(!ModConfig.patches.patchEntityBlockDestroy && !hasExamplesOnly(ModConfig.server.minecraft.entityBlockDestroyBlacklist))
		{
			inform("Necessary patch for blacklisting entity block destruction, set patches / patchEntityBlockDestroy to true", sender);
		}
		
		if(!ModConfig.patches.patchItemFrameDupe)
		{
			inform("Improvement patch, set patches patchItemFrameDupe to true", sender);
		}
		
		if(ModConfig.patches.patchPushReaction && hasExamplesOnly(ModConfig.server.minecraft.entityPushPrevention))
		{
			inform("Unnecessary patch, set patches / patchPushReaction to false", sender);
		}
		else if(!ModConfig.patches.patchPushReaction && !hasExamplesOnly(ModConfig.server.minecraft.entityPushPrevention))
		{
			inform("Necessary patch for entity push prevention, set patches / patchPushReaction to true", sender);
		}
		
		if(!ModConfig.patches.patchAnvilDupe)
		{
			inform("Improvement patch, set patches / patchAnvilDupe to true", sender);
		}
		
		if(!ModConfig.patches.patchOverlayMessage)
		{
			inform("Optional patch, set patches / patchOverlayMessage to true", sender);
		}
		
		if(ModConfig.patches.patchHopper && hasExamplesOnly(ModConfig.server.minecraft.hopperBlockBlacklist))
		{
			inform("Unnecessary patch, set patches / patchHopper to false", sender);
		}
		else if(!ModConfig.patches.patchHopper && !hasExamplesOnly(ModConfig.server.minecraft.hopperBlockBlacklist))
		{
			inform("Necessary patch for hopper blacklist, set patches / patchHopper to true", sender);
		}
		
		if(!ModConfig.patches.betterCombatCriticalsFix && Loader.isModLoaded(ModNames.BETTERCOMBAT))
		{
			inform("Improvement patch, set patches / betterCombatCriticalsFix to true", sender);
		}

		if(!ModConfig.patches.ENABLED)
		{
			inform("All patches disabled, set patches / ENABLED to true", sender);
		}
		
		//No advisory to set ENABLED to false
		
	}
	
	private boolean hasExamplesOnly(String[] list)
	{
		if(list.length == 0)
			return true;
		
		for(String s : list)
		{
			if(!s.startsWith("example"))
				return false;
		}
		
		return true;
	}
	
	public void inform(String s, ICommandSender sender)
	{
		sender.sendMessage(new TextComponentString(s));
	}
}
