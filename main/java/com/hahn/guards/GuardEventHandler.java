package com.hahn.guards;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuardEventHandler implements Serializable {
	private static Map<String, Map<String, Byte>> relations = new ConcurrentHashMap<String, Map<String, Byte>>();
	private static Map<String, Integer> numGuards = new ConcurrentHashMap<String, Integer>();
	
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent e) {
		if (!e.entity.worldObj.isRemote && e.entity.getEntityData().hasKey("ownerName")) {
			String ownerName = e.entity.getEntityData().getString("ownerName");
			GuardEventHandler.addNumGuards(ownerName, -1);
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
				System.out.println("Attacked golem of " + ownerName);
				updateRelations(world, ownerName, factionName, -1);
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
	
	public static void updateRelations(World world, String thisName, String otherName, int add) {
		int i = getRelations(thisName, otherName) + add;		
		if (i > Byte.MAX_VALUE) i = Byte.MAX_VALUE;
		else if (i < Byte.MIN_VALUE) i = Byte.MIN_VALUE;
		
		setRelations(world, thisName, otherName, (byte) i);
	}
	
	public static boolean otherHasRelations(String thisName, String otherName) {
		if (GuardEventHandler.relations.containsKey(otherName)) {
			return true;
		} else if (GuardEventHandler.relations.containsKey(thisName)) {
			Map<String, Byte> relationsMap = getRelationsMap(thisName);
			if (relationsMap.containsKey(otherName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Map<String, Byte> getRelationsMap(String thisName) {
		Map<String, Byte> relationsMap = GuardEventHandler.relations.get(thisName);
		if (relationsMap == null) {
			relationsMap = new HashMap<String, Byte>();
			GuardEventHandler.relations.put(thisName, relationsMap);
		}
		
		return relationsMap;
	}
	
	public static Byte getRelations(String thisName, String otherName) {
		if (thisName.equals(otherName)) return 0;
		
		Map<String, Byte> relationsMap = getRelationsMap(thisName);		
		Byte value = relationsMap.get(otherName);
		if (value == null) {
			value = 3;
			relationsMap.put(otherName, value);
		}
		
		return value;
	}
	
	private static void setRelations(World world, String thisName, String otherName, byte value) {
		if (thisName.equals(otherName)) return;
		
		Map<String, Byte> relationsMap = getRelationsMap(thisName);
		byte oldValue = relationsMap.put(otherName, value);
		
		if (oldValue >= 0 && value < 0) {			
			// Send messages
			EntityPlayer thisPlayer = world.getPlayerEntityByName(thisName);
			EntityPlayer otherPlayer = world.getPlayerEntityByName(otherName);
			
			if (thisPlayer != null) thisPlayer.addChatMessage(new ChatComponentText("You declared war on " + otherName + "!"));
			if (otherPlayer != null) otherPlayer.addChatMessage(new ChatComponentText(thisName + " declared war on you!"));
			
			// Make other faction angry
			updateRelations(world, otherName, thisName, (byte) -100);
		} else if (oldValue < 0 && value >= 0) {
			// Send messages
			EntityPlayer thisPlayer = world.getPlayerEntityByName(thisName);
			EntityPlayer otherPlayer = world.getPlayerEntityByName(otherName);
			
			if (thisPlayer != null) thisPlayer.addChatMessage(new ChatComponentText("You are no longer an aggressor towards " + otherPlayer));
			if (otherPlayer != null) otherPlayer.addChatMessage(new ChatComponentText(thisName + " is no longer an aggressor towards you"));
		}
	}
	
	public static void addNumGuards(String thisName, int add) {
		System.out.println("Adding " + add + " for " + thisName);
		
		setNumGuards(thisName, getNumGuards(thisName) + add);
	}
	
	public static int getNumGuards(String thisName) {
		Integer numGuards = GuardEventHandler.numGuards.get(thisName);
		if (numGuards == null) {
			GuardEventHandler.numGuards.put(thisName, 0);
			return 0;
		} else {
			return numGuards;
		}
	}
	
	private static void setNumGuards(String thisName, int value) {
		GuardEventHandler.numGuards.put(thisName, value);
	}
	
	public static void save(ObjectOutputStream oos) {
		try {
			oos.writeObject(relations);
			oos.writeObject(numGuards);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void read(ObjectInputStream ois) {
		try {
			relations = (ConcurrentHashMap<String, Map<String, Byte>>) ois.readObject();
			numGuards = (ConcurrentHashMap<String, Integer>) ois.readObject();
		} catch (Exception e) {
			relations = new ConcurrentHashMap<String, Map<String, Byte>>();
			numGuards = new ConcurrentHashMap<String, Integer>();
			
			System.out.println("Faield to load GuardEventHanlder details! Error:");
			e.printStackTrace();
		}
	}
}
