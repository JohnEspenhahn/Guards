package com.hahn.guards.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityAIMoveToHome extends EntityAIBase {
    protected final EntityCreature theEntity;
    protected double movePosX;
    protected double movePosY;
    protected double movePosZ;
    protected double movementSpeed;
    
    public EntityAIMoveToHome(EntityCreature entity, double speed) {        
        if (!(entity instanceof IWanderer)) {
            throw new RuntimeException(entity + " must extend IWanderer");
        }
        
        this.theEntity = entity;
        this.movementSpeed = speed;
        
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        int homeRadius = ((IWanderer) theEntity).getStationRadius();
        if (this.isWithinRadiusOfHome(homeRadius)) {
            return false;
        } else {
            ChunkCoordinates cc = this.theEntity.getHomePosition();
            
            int dY = (homeRadius <= 4 ? 2 : 4);
            Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, homeRadius, dY, Vec3.createVectorHelper(cc.posX, cc.posY, cc.posZ));
    
            if (vec3 == null) {
                return false;
            } else {
                this.movePosX = vec3.xCoord;
                this.movePosY = vec3.yCoord;
                this.movePosZ = vec3.zCoord;
                return true;
            }
        }
    }
    
    @Override
    public boolean continueExecuting() {
        return !this.theEntity.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
    }
    
    public boolean isWithinRadiusOfHome(int radius) {
        return radius == -1.0F ? true : theEntity.getHomePosition().getDistanceSquared(MathHelper.floor_double(theEntity.posX), (int) theEntity.posY, MathHelper.floor_double(theEntity.posZ)) < radius * radius + 1;
    }
}
