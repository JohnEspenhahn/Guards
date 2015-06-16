package com.hahn.guards.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.hahn.guards.entity.EntityStoneGolem;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SynchGuardStance implements IMessage {
	private int entityId;
	private NBTTagCompound nbt;

	public SynchGuardStance() {
		// need this constructor
	}

	public SynchGuardStance(int entityId, NBTTagCompound nbt) {
		this.entityId = entityId;
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = ByteBufUtils.readVarInt(buf, 4);
		nbt = ByteBufUtils.readTag(buf); // this class is very useful in generalfor writing more complex objects
		// DEBUG
		System.out.println("fromBytes");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, entityId, 4);
		ByteBufUtils.writeTag(buf, nbt);

		// DEBUG
		System.out.println("toBytes encoded");
	}

	public static class Handler implements IMessageHandler<SynchGuardStance, IMessage> {
		
		@Override
		public IMessage onMessage(SynchGuardStance message, MessageContext ctx) {
			EntityPlayer player = (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : ctx.getServerHandler().playerEntity);
			EntityStoneGolem guard = (EntityStoneGolem) player.worldObj.getEntityByID(message.entityId);

			guard.readGuardStance(message.nbt);

			return null; // no response in this case
		}
		
	}
}
