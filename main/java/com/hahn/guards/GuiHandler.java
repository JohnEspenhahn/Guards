package com.hahn.guards;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.hahn.guards.client.GuiGuard;
import com.hahn.guards.client.GuiGuardSpawner;
import com.hahn.guards.entity.EntityStoneGolem;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final byte GUARD_SPAWNER = 1, GUARD = 2;
    
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int xOrID, int y, int z) {		
		Entity e;
		TileEntity te;
	    
	    switch (id) {
		case GUARD_SPAWNER:
		    te = world.getTileEntity(xOrID, y, z);
            if (te != null && te instanceof TileEntityGuardSpawner) 
                return new ContainerGuardSpawner(player.inventory, (TileEntityGuardSpawner) te);
            break;
		case GUARD:
			return null;
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int xOrID, int y, int z) {		
	    Entity e;
	    TileEntity te;
        
	    System.out.println("Getting entity " + xOrID);
	    
        switch (id) {
        case GUARD_SPAWNER:
            te = world.getTileEntity(xOrID, y, z);
            if (te != null && te instanceof TileEntityGuardSpawner) 
                return new GuiGuardSpawner(player.inventory, (TileEntityGuardSpawner) te);
            break;
        case GUARD:
        	Entity entity = world.getEntityByID(xOrID);
        	if (entity instanceof EntityStoneGolem) {
        		return new GuiGuard((EntityStoneGolem) entity);
        	}
        	
			break;
        }
        
		return null;
	}

}
