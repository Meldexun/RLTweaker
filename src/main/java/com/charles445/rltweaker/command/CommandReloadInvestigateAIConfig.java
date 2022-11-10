package com.charles445.rltweaker.command;

import com.charles445.rltweaker.config.JsonConfig;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandReloadInvestigateAIConfig extends CommandBase {

	@Override
	public String getName() {
		return "rlreloadinvestigateai";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/rlreloadinvestigateai";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		sender.sendMessage(new TextComponentString("Reloading investigate AI config..."));
		JsonConfig.loadInvestigateAIConfig();
	}

}
