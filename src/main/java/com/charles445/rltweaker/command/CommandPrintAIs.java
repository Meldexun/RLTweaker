package com.charles445.rltweaker.command;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandPrintAIs extends CommandBase {

	@Override
	public String getName() {
		return "rl_printAIs";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/rl_printAIs <entity>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new WrongUsageException(getUsage(sender));
		}
		EntityLiving entity = getEntity(server, sender, args[0], EntityLiving.class);
		notifyCommandListener(sender, this, String.format("AIs of %s:", entity));
		notifyCommandListener(sender, this, " priority mutex name");
		for (EntityAITaskEntry taskEntry : entity.tasks.taskEntries) {
			notifyCommandListener(sender, this, String.format(" %d %d %s", taskEntry.priority, taskEntry.action.getMutexBits(), taskEntry.action.getClass().getSimpleName()));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "@e[type=!player,r=3]");
		}
		return Collections.emptyList();
	}

}
