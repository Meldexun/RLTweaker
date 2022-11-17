package com.charles445.rltweaker.command;

import java.io.IOException;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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
		try {
			JsonConfig.loadInvestigateAIConfig();
			sender.sendMessage(new TextComponentString(String.format("Loaded %d investigate AI entries", JsonConfig.investigateAI.size())));
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			Throwable t = e;
			while (t.getCause() != null) {
				t = t.getCause();
			}
			sender.sendMessage(new TextComponentString(String.format("%sFailed loading investigate AI: %s: %s", TextFormatting.RED, t.getClass().getSimpleName(), t.getMessage())));
			RLTweaker.logger.error("Failed to load investigate AI config files: ", e);
		}
	}

}
