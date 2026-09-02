package com.charles445.rltweaker.handler;

import java.util.concurrent.atomic.AtomicInteger;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.MultiMineReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.Watchdog;

public class MultiMineHandler
{
	MultiMineReflect reflector;
	
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
