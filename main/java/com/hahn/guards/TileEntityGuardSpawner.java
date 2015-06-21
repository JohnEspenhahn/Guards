package com.hahn.guards;

import com.hahn.guards.entity.EntityStoneGolem;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.Vec3;

public class TileEntityGuardSpawner extends TileEntityDispenser {
	public static int SPAWN_DELAY = 60;
	public static int AMNT_NEEDED = 3 * 16;
	
	private int spawnDelay = SPAWN_DELAY;
	private String ownerName;
	
	public void setOwnerName(String name) {
		this.ownerName = name;
		
		if (worldObj != null && !worldObj.isRemote) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	public String getOwnerName() {
		return this.ownerName;
	}
	
	private int getNeeded() {
		int numGuards = GuardEventHandler.getNumGuards(getOwnerName());
		System.out.println(getOwnerName() + " has " + numGuards);
		
		// Return amount needed
		if (numGuards > 1) {
			return (int) Math.ceil(AMNT_NEEDED * (numGuards * numGuards));
		} else {
			return AMNT_NEEDED;
		}
	}
	
	private int getAvaliable() {
		int amnt = 0;
        for (int i=0; i < this.getSizeInventory(); i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemFood) {
            	// reduce amnt by item heal amount
            	ItemFood food = (ItemFood) stack.getItem();
                amnt += food.func_150905_g(stack) * stack.stackSize;
            }
        }
        
        return amnt;
    }
	
	private void consume(int amnt) {
		for (int i=0; i < this.getSizeInventory(); i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemFood) {
            	// reduce amnt by item heal amount
            	ItemFood food = (ItemFood) stack.getItem();
            	int healAmnt = food.func_150905_g(stack);
            	
            	// Amount need to take from this stack to use all amnt
            	int amntToTake = (int) Math.ceil((double) amnt / healAmnt);
            	
            	if (amntToTake < stack.stackSize) {
            		amnt -= healAmnt * amntToTake;
            		stack.stackSize -= amntToTake;
            	} else if (amntToTake >= stack.stackSize) {
            		amnt -= healAmnt * stack.stackSize;
            		this.setInventorySlotContents(i, null);
            	}
            }
            
            if (amnt <= 0) return;
        }
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (!worldObj.isRemote && getOwnerName() != null && --spawnDelay <= 0) {
			spawnDelay = TileEntityGuardSpawner.SPAWN_DELAY;
			
			int has = getAvaliable();
			int need = getNeeded();
			
			if (has >= need) {
				consume(need);
				
				EntityStoneGolem golem = new EntityStoneGolem(worldObj);
				golem.setOwnerName(getOwnerName());
				golem.setHomeArea(xCoord, yCoord, zCoord, 8);
				golem.setChaseRange(32);
				golem.setFollowing(false);
				
				golem.setPosition(xCoord + 0.5, yCoord + 1, zCoord + 0.5);
				
				GuardEventHandler.addNumGuards(getOwnerName(), 1);
				
				worldObj.spawnEntityInWorld(golem);
			}
		}
	}
	
	@Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        
        if (getOwnerName() != null) {
        	nbt.setString("ownerName", getOwnerName());
        }
    }
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		if (nbt.hasKey("ownerName")) {
			setOwnerName(nbt.getString("ownerName"));
		}
	}
	
	@Override
	public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        
        if (getOwnerName() != null) {
        	nbt.setString("ownerName", getOwnerName());
        }
		
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		
		NBTTagCompound nbt = packet.func_148857_g();
		if (nbt.hasKey("ownerName")) {
			setOwnerName(nbt.getString("ownerName"));
		}
    }
	
	@Override
	public boolean isItemValidForSlot(int par1, ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemFood;
    }
	
	@Override
	public boolean canUpdate() {
        return true;
    }
	
	@Override
	public String getInventoryName() {
        return "Guard Spawner";
    }
}
