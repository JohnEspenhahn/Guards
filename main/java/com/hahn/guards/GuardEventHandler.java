package com.hahn.guards;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuardEventHandler {	
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent e) {
		if (!e.entity.worldObj.isRemote && e.entity.getEntityData().hasKey("ownerName")) {
			String ownerName = e.entity.getEntityData().getString("ownerName");
			GuardEventHandler.addNumGuards(e.entity.worldObj, ownerName, -1);
		}
	}
	
	@SubscribeEvent
	public void onLivingAttackEvent(LivingAttackEvent e) {
		// Damage reputation when just attacking an entity
		World world = e.entity.worldObj;
		if (!world.isRemote && e.source.getEntity() != null && e.entity.getEntityData().hasKey("ownerName")) {
			String factionName = getFactionName(e.source.getEntity());
			
			if (factionName != null) {
				String ownerName = e.entity.getEntityData().getString("ownerName");
				updateReputation(world, ownerName, factionName, -1);
			}
		}
	} 
	
	/**
	 * Attempt to get the faction of the given entity
	 * @param e An entity
	 * @return Null or the faction (aka owner's) name
	 */
	public static String getFactionName(Entity e) {
		if (e == null) {
			return null;
		} else if (e instanceof EntityPlayer) {
			return e.getCommandSenderName();
		} else if (e.getEntityData().hasKey("ownerName")) {
			return e.getEntityData().getString("ownerName");
		} else {
			return null;
		}
	}
	
	public static int getNumGuards(World world, String thisName) {
		if (world.isRemote) return 0;
		
		String key = getNumGuardKey(thisName);
		NBTTagCompound wNBT = getWorldNBT(world);
		
		if (wNBT.hasKey(key)) {
			return wNBT.getInteger(key);
		} else {
			wNBT.setInteger(key, 0);
			return 0;
		}
	}
	
	public static void addNumGuards(World world, String thisName, int add) {
		if (world.isRemote) return;
		
		String key = getNumGuardKey(thisName);
		NBTTagCompound wNBT = getWorldNBT(world);
		
		if (wNBT.hasKey(key)) {
			wNBT.setInteger(key, wNBT.getInteger(key) + add);
		} else {
			if (add > 0) wNBT.setInteger(key, add);
			else wNBT.setInteger(key, 0);
		}
	}
	
	public static int getReputation(World world, String thisName, String otherName) {
		if (world.isRemote) return 0;
		
		String key = getRepKey(thisName, otherName);
		NBTTagCompound nbt = getWorldNBT(world);
		
		if (nbt.hasKey(key)) {
			return nbt.getByte(key);
		} else {
			nbt.setByte(key, (byte) 3);
			return 3;
		}
	}
	
	public static void updateReputation(World world, String thisName, String otherName, int add) {
		if (world.isRemote) return;
		
		int i = getReputation(world, thisName, otherName) + add;		
		if (i > Byte.MAX_VALUE) i = Byte.MAX_VALUE;
		else if (i < Byte.MIN_VALUE) i = Byte.MIN_VALUE;
		
		String key = getRepKey(thisName, otherName);
		NBTTagCompound nbt = getWorldNBT(world);
		nbt.setByte(key, (byte) i);
	}
	
	private static String getNumGuardKey(String thisName) {
		return thisName + "NumGuard";
	}
	
	private static String getRepKey(String thisName, String otherName) {
		return thisName + "_" + otherName + "Rep";
	}
	
	private static NBTTagCompound getWorldNBT(World world) {
		return world.getWorldInfo().getNBTTagCompound();
	}
}
