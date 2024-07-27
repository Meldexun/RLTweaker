package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigRecurrent
{
	@Config.Comment("Master switch for this mod compatibility")
	@Config.Name("ENABLED")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.Comment("Allows for some recurrent complex event configuration")
	@Config.Name("Manage Recurrent Complex Events")
	@Config.RequiresMcRestart
	public boolean manageRCForgeEventHandler = true;
	
	@Config.Comment("Generates structures one chunk at a time. Vanilla Recurrent Complex is true")
	@Config.Name("Generate Structures Partially")
	@Config.RequiresMcRestart
	public boolean generatePartially = false;
	
	@Config.Comment("Cleans the reccomplex-structuredata.dat regularly. Vanilla Recurrent Complex is false")
	@Config.Name("Clean Structure Data")
	@Config.RequiresMcRestart
	public boolean cleanStructureData = true;
	
	@Config.Comment("How many structures need to generate before the dat is cleaned")
	@Config.Name("Clean Structure Threshold")
	@Config.RangeInt(min=1)
	public int cleanStructureThreshold = 100;
}

