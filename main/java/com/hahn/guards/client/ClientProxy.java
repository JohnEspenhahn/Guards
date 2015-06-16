package com.hahn.guards.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.DimensionManager;

import com.hahn.guards.CommonProxy;
import com.hahn.guards.EntityStoneGolem;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityStoneGolem.class, new RenderStoneGolem());
	}

}
