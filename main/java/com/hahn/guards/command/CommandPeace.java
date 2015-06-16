package com.hahn.guards.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.hahn.guards.GuardEventHandler;

public class CommandPeace extends CommandBase {

	@Override
	public String getCommandName() {
		return "peace";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
            return "Try to reach peace with another player /peace 'playername'";
        } else {
        	return "Only players can use /peace";
        }
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayer) {
			if (args.length >= 1) {
				if (sender.getCommandSenderName().equals(args[0])) return;
				
				GuardEventHandler.updateRelations(sender.getEntityWorld(), sender.getCommandSenderName(), args[0], 100);
				
				EntityPlayer p = sender.getEntityWorld().getPlayerEntityByName(args[0]);
				boolean hasRelations = GuardEventHandler.otherHasRelations(sender.getCommandSenderName(), args[0]);
				if (p == null && !hasRelations) {
					sender.addChatMessage(new ChatComponentText("The player " + args[0] + " could not be found. They may just not be logged in."));
				}
			} else {
				sender.addChatMessage(new ChatComponentText("You must specify someone to try and reach peace with!"));
			}
		}
	}

}
