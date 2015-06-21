package com.hahn.guards.command;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.hahn.guards.GuardEventHandler;

public class CommandGuards extends CommandBase {

	@Override
	public String getCommandName() {
		return "guards";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
            return "/guards  Print details on your guards";
        } else {
        	return "Only players can use /guards";
        }
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayer) {
			int numGuards = GuardEventHandler.getNumGuards(sender.getCommandSenderName());
			if (numGuards == 0) {
				sender.addChatMessage(new ChatComponentText("You have no guards"));
			} else if (numGuards > 1) {
				sender.addChatMessage(new ChatComponentText("Your have " + numGuards + " guards"));
			} else {
				sender.addChatMessage(new ChatComponentText("You have 1 guard"));
			}
		}
	}

}
