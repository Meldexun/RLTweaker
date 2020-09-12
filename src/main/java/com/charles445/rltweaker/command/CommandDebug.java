package com.charles445.rltweaker.command;

import java.util.Map;

import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.util.ErrorUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandDebug extends CommandBase
{
	@Override
	public String getName()
	{
		return "rldebugserver";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/rldebugserver";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length>0)
		{
			try
			{
				double val = Double.parseDouble(args[0]);
				if(val < 2.0d)
					val = 2.0d;
				if(val > 100.0d)
					val = 100.0d;
				inform("Setting MAX_ENTITY_RADIUS to "+val, sender);
				
				sender.getEntityWorld().MAX_ENTITY_RADIUS = val;
			}
			catch(NumberFormatException e)
			{
				
			}
		}
	}
	
	public void inform(String s, ICommandSender sender)
	{
		sender.sendMessage(new TextComponentString(s));
	}
}
