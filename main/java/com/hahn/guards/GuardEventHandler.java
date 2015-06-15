package com.hahn.guards;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuardEventHandler {
	
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent e) {
		if (!e.entity.worldObj.isRemote && e.entity.getEntityData().hasKey("ownerName")) {
			String ownerName = e.entity.getEntityData().getString("ownerName");
			EntityPlayer p = e.entity.worldObj.getPlayerEntityByName(ownerName);
			if (p != null) GuardEventHandler.addNumGuards(p, -1);
		}
	}
	
	@SubscribeEvent
	public void onLivingAttackEvent(LivingAttackEvent e) {
		if (!e.entity.worldObj.isRemote && e.entity.getEntityData().hasKey("ownerName")) {
			// TODO
		}
	}
	
	public static int getNumGuards(EntityPlayer p) {
		if (p.getEntityData().hasKey("numGuards")) {
			return p.getEntityData().getInteger("numGuards");
		} else {
			p.getEntityData().setInteger("numGuards", 0);
			return 0;
		}
	}
	
	public static void addNumGuards(EntityPlayer p, int add) {
		NBTTagCompound nbt = p.getEntityData();
		if (nbt.hasKey("numGuards")) {
			nbt.setInteger("numGuards", nbt.getInteger("numGuards") + add);
		} else {
			if (add > 0) nbt.setInteger("numGuards", add);
			else nbt.setInteger("numGuards", 0);
		}
	}
}
