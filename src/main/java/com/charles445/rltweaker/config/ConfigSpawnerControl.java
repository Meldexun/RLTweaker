package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigSpawnerControl
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Attempt to fix concurrency crashes by synchronizing spawner iteration.")
	@Config.Name("Synchronize Spawner Iteration")
	@Config.RequiresMcRestart
	public boolean synchronizeSpawnerIteration = true;
	
	@Config.Comment("Removes world ticks, which are used to create smoke particles and remove obsolete spawners, to save performance.")
	@Config.Name("Remove World Ticks")
	@Config.RequiresMcRestart
	public boolean removeWorldTicks = true;
}
