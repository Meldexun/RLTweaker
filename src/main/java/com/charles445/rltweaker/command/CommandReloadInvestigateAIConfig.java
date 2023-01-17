package com.charles445.rltweaker.command;

import java.io.IOException;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.JsonConfig.JsonFileException;

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
		} catch (JsonFileException e) {
			Throwable t = e;
			while (t.getCause() != null) {
				t = t.getCause();
			}
			sender.sendMessage(new TextComponentString(String.format("%sFailed to load investigate AI file '%s': %s: %s", TextFormatting.RED, e.getFile(), t.getClass().getSimpleName(), t.getMessage())));
			RLTweaker.logger.error("Failed to load investigate AI file '{}'", e.getFile(), e);
		} catch (IOException e) {
			sender.sendMessage(new TextComponentString(String.format("%sFailed to load investigate AI config files", TextFormatting.RED)));
			RLTweaker.logger.error("Failed to load investigate AI config files", e);
		}
	}

}
