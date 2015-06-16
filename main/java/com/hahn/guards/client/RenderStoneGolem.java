package com.hahn.guards.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.hahn.guards.entity.EntityStoneGolem;

public class RenderStoneGolem extends RenderIronGolem {
	private static final ResourceLocation stoneGolemTextures = new ResourceLocation("guards:textures/entities/stone_golem.png");

    public RenderStoneGolem() {
        super();
    }

    protected ResourceLocation getIronGolemTextures(EntityIronGolem par1EntityIronGolem) {
        return stoneGolemTextures;
    }
}
