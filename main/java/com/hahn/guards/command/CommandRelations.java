package com.hahn.guards.command;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.hahn.guards.GuardEventHandler;

public class CommandRelations extends CommandBase {

	@Override
	public String getCommandName() {
		return "relations";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
            return "Print your relations with other players";
        } else {
        	return "Only players can use /relations";
        }
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayer) {
			Map<String, Byte> relations = GuardEventHandler.getRelationsMap(sender.getCommandSenderName());
			for (Entry<String, Byte> r: relations.entrySet()) {
				String state = (r.getValue() < 0 ? "WAR" : "PEACE");
				sender.addChatMessage(new ChatComponentText(r.getKey() + " [" + state + "] " + r.getValue()));
			}
		}
	}

}
