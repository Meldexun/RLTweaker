package com.charles445.rltweaker.config;

import net.minecraftforge.common.config.Config;

public class ConfigMinecraftClient
{
	@Config.Comment("REQUIRES Patch patchOverlayMessage. Whether overlay text should have a dropshadow")
	@Config.Name("Overlay Text Drop Shadow")
	public boolean overlayTextDropShadow = false;
	
	@Config.Comment("REQUIRES Patch patchOverlayMessage. Moves overlay text up by this amount (can be negative to move down)")
	@Config.Name("Overlay Text Offset")
	public int overlayTextOffset = 0;
}
