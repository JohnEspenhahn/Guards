package com.hahn.guards.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

import com.hahn.guards.EntityStoneGolem;

public class EntityAIGuardFindAttackableTarget extends EntityAINearestAttackableTarget {
    private final EntityStoneGolem guard;

    public EntityAIGuardFindAttackableTarget(EntityStoneGolem guard, boolean par4, boolean par5, GuardEntitySelector selector) {
        super(guard, EntityLivingBase.class, 0, par4, par5, selector);
        
        this.guard = guard; 
    }
    
    @Override
    public boolean shouldExecute() {
        return guard.shouldAttack() && super.shouldExecute();
    }
    
    @Override
    public boolean continueExecuting() {
        if (!guard.shouldAttack()) {
            return false;
        } else {
            return super.continueExecuting();
        }
    }
}
