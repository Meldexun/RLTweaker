package com.charles445.rltweaker.handler;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.MultiMineReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ServerRunnable;
import com.charles445.rltweaker.util.Watchdog;

import atomicstryker.multimine.common.MultiMineServer;

public class MultiMineHandler
{
	MultiMineReflect reflector;
	Field serverInstance;
	
	public MultiMineHandler()
	{
		try
		{
			reflector = new MultiMineReflect();
			
			if(ModConfig.server.multimine.stallWatchdog)
				Watchdog.addRoutine("MultiMine Stall", new MultiMineStallRoutine());
		}
		catch(Exception e)
		{
			RLTweaker.logger.error("Failed to setup MultiMineHandler!", e);
			ErrorUtil.logSilent("MultiMine Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}

		if(ModConfig.patches.multiMineMemoryLeakPatch)
		{
			try {
				serverInstance = MultiMineServer.class.getDeclaredField("serverInstance");
				serverInstance.setAccessible(true);
				RLTweaker.serverRunnables.put("StaleReferenceHelper", new StaleReferenceHelper());
			} catch (NoSuchFieldException e) {
				RLTweaker.logger.error("Failed to setup MultiMineServer handler!", e);
			}
		}
	}

	public class StaleReferenceHelper implements ServerRunnable
	{
		@Override
		public void onServerStarting() {

		}

		@Override
		public void onServerStopping()
		{
			try {
				MultiMineHandler.this.serverInstance.set(null, null);
            } catch (IllegalAccessException e) {
				RLTweaker.logger.error("Failed to remove MultiMineServer server instance!", e);
			}
		}
	}
	
	public class MultiMineStallRoutine extends Watchdog.Routine
	{
		private AtomicInteger semaphoreCount = new AtomicInteger(0);

		//Runs on a separate thread
		@Override
		public void run() throws Exception
		{
			if(reflector.getSemaphor())
			{
				int sem = this.semaphoreCount.incrementAndGet();
				if(sem > 5)
				{
					Watchdog.logger.warn("MultiMine may be stalling, attempting recovery");
					reflector.setSemaphor(false);
					this.semaphoreCount.set(0);
				}
			}
			else
			{
				this.semaphoreCount.set(0);
			}
		}
	}
}
