package com.charles445.rltweaker.command;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.handler.InvestigateAIHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandReloadInvestigateAIConfig extends CommandBase {

	@Override
	public String getName() {
		return "rl_reloadInvestigateAI";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/rl_reloadInvestigateAI";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Exception e = InvestigateAIHandler.loadConfig();
		if (e == null) {
			sender.sendMessage(new TextComponentString(String.format("Loaded %d investigate AI entries", InvestigateAIHandler.getConfigurations().size())));
		} else {
			sender.sendMessage(new TextComponentString(String.format("%sFailed to load investigate AI config files", TextFormatting.RED)));
			RLTweaker.logger.error("Failed to load investigate AI config files", e);
		}
	}

}
