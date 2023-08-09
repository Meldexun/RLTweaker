package com.charles445.rltweaker.hook;

import java.util.UUID;

import com.charles445.rltweaker.config.ModConfig;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;

public class HookQuark {

	private static final Object2LongMap<UUID> lastTimeItemLinked = new Object2LongOpenHashMap<>();

	public static boolean tryLinkItem(EntityPlayer player) {
		long lastTime = lastTimeItemLinked.getLong(player.getPersistentID());
		if (player.world.getTotalWorldTime() < lastTime + ModConfig.server.quark.itemLinkingCooldown) {
			return false;
		}
		lastTimeItemLinked.put(player.getPersistentID(), player.world.getTotalWorldTime());
		return true;
	}

}
