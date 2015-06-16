package com.hahn.guards.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

import com.hahn.guards.EntityStoneGolem;

public class GuardEntitySelector implements IEntitySelector {
	EntityStoneGolem guard;
	
	public GuardEntitySelector(EntityStoneGolem guard) {
		this.guard = guard;
	}
	
	@Override
	public boolean isEntityApplicable(Entity entity) {
		return guard.isSuitableTarget(entity);
	}
}
