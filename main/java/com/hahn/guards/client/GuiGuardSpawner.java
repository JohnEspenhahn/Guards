package com.hahn.guards.client;

import java.util.Map.Entry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.hahn.guards.ContainerGuardSpawner;
import com.hahn.guards.TileEntityGuardSpawner;

public class GuiGuardSpawner extends GuiContainer {
	private static final ResourceLocation dispenserGuiTextures = new ResourceLocation("guards:textures/gui/GuiGuardSpawner.png");
    public TileEntityGuardSpawner theSpawner;

    public GuiGuardSpawner(InventoryPlayer player, TileEntityGuardSpawner entity) {
        super(new ContainerGuardSpawner(player, entity));
        this.theSpawner = entity;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    	super.drawGuiContainerForegroundLayer(par1, par2);
    	
        String s = theSpawner.getOwnerName() + "'s " + theSpawner.getInventoryName(); // TODO I18n
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        
        // int yPos = 20;	
        // this.fontRendererObj.drawString(theSpawner.getOwnerName(), 8, yPos, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(dispenserGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}