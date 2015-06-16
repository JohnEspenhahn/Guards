package com.hahn.guards.client;

import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

public class RenderStoneGolem extends RenderIronGolem {
	private static final ResourceLocation stoneGolemTextures = new ResourceLocation("guards:textures/entities/stone_golem.png");

    public RenderStoneGolem() {
        super();
    }

    protected ResourceLocation getIronGolemTextures(EntityIronGolem par1EntityIronGolem) {
        return stoneGolemTextures;
    }
}
