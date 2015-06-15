package com.hahn.guards;

import com.hahn.guards.client.GuiGuardSpawner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final byte GUARD_SPAWNER = 1;
    
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {		
		Entity e;
		TileEntity te;
	    
	    switch (id) {
		case GUARD_SPAWNER:
		    te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityGuardSpawner) 
                return new ContainerGuardSpawner(player.inventory, (TileEntityGuardSpawner) te);
            break;
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {		
	    Entity e;
	    TileEntity te;
        
        switch (id) {
        case GUARD_SPAWNER:
            te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityGuardSpawner) 
                return new GuiGuardSpawner(player.inventory, (TileEntityGuardSpawner) te);
            break;
        }
        
		return null;
	}

}
