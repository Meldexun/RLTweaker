package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SRParasitesHandler
{
	
	public SRParasitesHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);

		//TODO wrap properly to remove need for mc restart
		try
		{
			if(!ModConfig.server.srparasites.parasitesSleepPrevention)
				CompatUtil.findAndRemoveHandlerFromEventBus("com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus", "playerSleep");
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup SRParasitesHandler!", e);
			ErrorUtil.logSilent("SRParasites Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote)
			return;
		
		//Server side
		if(ModConfig.server.srparasites.parasitesDimensionBlacklistEnabled)
		{
			int[] blacklist = ModConfig.server.srparasites.parasitesDimensionBlacklist;
			
			//Remove parasites from blacklisted dimensions
			for(int i = 0; i < blacklist.length; i++)
			{
				if(event.getEntity().dimension == blacklist[i])
				{
					ResourceLocation rl = EntityList.getKey(event.getEntity());
					if(rl != null && rl.getNamespace().equals("srparasites"))
					{
						event.setCanceled(true);
					}
				}
			}
		}
		
		if(ModConfig.server.srparasites.parasitesBiomassCrashFix)
		{
			if(event.getEntity() instanceof EntityFireball && event.getEntity().getClass().getName().equals("com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileBiomass"))
			{
				EntityFireball fireball = (EntityFireball)event.getEntity();
				if(fireball.shootingEntity == null)
					event.setCanceled(true);
			}
		}
	}
}
