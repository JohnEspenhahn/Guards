package com.hahn.guards;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.hahn.guards.entity.EntityAIMoveToHome;
import com.hahn.guards.entity.EntityAIGuardFollow;
import com.hahn.guards.entity.GuardEntitySelector;
import com.hahn.guards.entity.IWanderer;

public class EntityStoneGolem extends EntityIronGolem implements IWanderer {
    private int chaseRange;
    private int wanderRange;
    private boolean following;
	
	public EntityStoneGolem(World world) {
        super(world);
        
        this.chaseRange = 32;
        this.wanderRange = 12;
        this.following = false;
        
        this.tasks.taskEntries.clear();
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAIMoveToHome(this, 1.0D));
        this.tasks.addTask(3, new EntityAIGuardFollow(this, 1.0f));
        this.tasks.addTask(3, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(5, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, new GuardEntitySelector(this)));
    }
	
	public String getOwnerName() {
		return getEntityData().getString("ownerName");
	}
	
	public EntityPlayer getOwner() {
		String ownerName = getOwnerName();
		
		if (ownerName == null) return null;
		else return worldObj.getPlayerEntityByName(ownerName);
	}
	
	public boolean isSuitableTarget(Entity e) {
		if (e == null) {
			return false;
		} else if (e instanceof EntityMob && !(e instanceof EntityCreeper)) {
			return true;
		} else {
			String factionName = GuardEventHandler.getFactionName(e);
			if (factionName == null) return false;
			else return GuardEventHandler.getRelations(getOwnerName(), factionName) < 0;
		}
	}
	
	public boolean shouldAttack() {
		return isSuitableTarget(getAttackTarget());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setAttackTarget(EntityLivingBase entity) {
	    // Try targeting old target's family
		EntityLivingBase target = getAttackTarget();
	    if (entity == null && target != null) {
	        NBTTagCompound targetNBT = target.getEntityData();
	        
	        if (targetNBT.hasKey("ownerName")) {
	        	String targetOwner = targetNBT.getString("ownerName");
	        	
	            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(target.posX-20, target.posY-10, target.posZ-20, target.posX+20, target.posY+10, target.posZ+20);
                List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
                for (EntityLivingBase newTarget: list) {
                	// Target from same owner
                	String factionName = GuardEventHandler.getFactionName(newTarget);
                    if (factionName != null && factionName.equals(targetOwner)) {
                        super.setAttackTarget(newTarget);
                        return;
                    }
                }
	        }
	    }
	    
	    // Otherwise default
	    super.setAttackTarget(entity);
	}
	
	@Override
	public void onDeath(DamageSource sourceObj) {
		if (sourceObj.getSourceOfDamage() != null) {
			Entity sourceEntity = sourceObj.getSourceOfDamage();
			String factionName = GuardEventHandler.getFactionName(sourceEntity);
			if (factionName != null) {
				// Killing guard causes -10 reputation
				GuardEventHandler.updateRelations(worldObj, getOwnerName(), factionName, -10);
			}
		}
	}
	
	@Override
	protected void dropFewItems(boolean par1, int par2) { }
	
	@Override
	public int getStationRadius() {
		return wanderRange;
	}
	
	public boolean isFollowing() {
		return following;
	}
}
