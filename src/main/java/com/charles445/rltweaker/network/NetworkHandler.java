package com.charles445.rltweaker.network;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.VersionDelimiter;

import net.minecraft.entity.player.EntityPlayer;

public class NetworkHandler
{
	public static boolean serverHasVersioning = false;
	public static VersionDelimiter serverVersion = VersionDelimiter.UNKOWN;
	
	//Accessed by Netty a lot
	private static Map<UUID, VersionDelimiter> clients = new ConcurrentHashMap<>();
	
	public static void removeClient(UUID id)
	{
		if(clients.remove(id)!=null)
			RLTweaker.logger.trace("Removing NetworkHandler UUID");
	}
	
	public static void addClient(UUID id, VersionDelimiter vd)
	{
		if(clients.put(id, vd)==null)
			RLTweaker.logger.trace("Adding NetworkHandler UUID");
			
	}
	
	public static boolean isVersionAtLeast(int major, int minor, EntityPlayer player)
	{
		return isVersionAtLeast(major, minor, 0, player.getGameProfile().getId());
	}
	
	public static boolean isVersionAtLeast(int major, int minor, int patch, EntityPlayer player)
	{
		return isVersionAtLeast(major, minor, patch, player.getGameProfile().getId());
	}
	
	public static boolean isVersionAtLeast(int major, int minor, int patch, UUID uuid)
	{
		VersionDelimiter value = clients.get(uuid);
		
		if(value!=null)
		{
			return value.isSameOrNewerVersion(major, minor, patch);
		}
		else
		{
			return false;
		}
	}
}
