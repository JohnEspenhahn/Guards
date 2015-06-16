package com.hahn.guards.entity;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

import com.hahn.guards.EntityStoneGolem;
import com.hahn.guards.Util;

public class EntityAIGuardFollow extends EntityAIBase {
    public static final int START_DIST = 3, TELEPORT_DIST = 13;
    
    private EntityStoneGolem guard;
    private EntityPlayer owner;
    
    private float speed;
    private int checkDelay;

    public EntityAIGuardFollow(EntityStoneGolem guard, float speed) {
        this.guard = guard;
        this.speed = speed;
        
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        EntityPlayer owner = guard.getOwner();

        if (owner == null) {
            return false;
        } else if (!guard.isFollowing()) {
            return false;
        } else if (guard.getDistanceSqToEntity(owner) < START_DIST * START_DIST) {
            return false;
        } else {
            this.owner = owner;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return owner.isEntityAlive() && guard.isFollowing() && !guard.getNavigator().noPath() && guard.getDistanceSqToEntity(this.owner) > START_DIST * START_DIST + 1;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.checkDelay = 0;
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        guard.getNavigator().clearPathEntity();
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        this.guard.getLookHelper().setLookPositionWithEntity(owner, 10.0F, guard.getVerticalFaceSpeed());

        if (--checkDelay <= 0) {
            checkDelay = 10;

            Vec3 vec = Vec3.createVectorHelper(owner.posX - guard.posX, owner.posY - guard.posY, owner.posZ - guard.posZ);
            vec = vec.normalize();
            
            PathNavigate navigator = guard.getNavigator();            
            if (!navigator.tryMoveToXYZ(owner.posX - vec.xCoord*START_DIST, owner.posY - vec.yCoord*START_DIST, owner.posZ - vec.zCoord*START_DIST, speed)) {
                // Teleport if too far
                if (guard.getDistanceSqToEntity(owner) >= TELEPORT_DIST * TELEPORT_DIST) {
                    boolean tped = Util.teleportToPlayer(guard, owner);
                    if (tped) guard.getNavigator().clearPathEntity();
                }
            }
        }
    }
}
