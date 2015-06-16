package com.hahn.guards.client;

import java.util.Random;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.hahn.guards.Guards;
import com.hahn.guards.entity.EntityStoneGolem;
import com.hahn.guards.network.SynchGuardStance;
import com.hahn.guards.util.Text;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGuard extends GuiScreen {
	public static final ResourceLocation background = new ResourceLocation("guards:textures/gui/GuiGuard.png");
	public static final ResourceLocation icons = new ResourceLocation("guards:textures/gui/icons.png");
	
	private static final int TOGGLE = 0;
	
    private EntityStoneGolem guard;
    
    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;

    /** The Y size of the inventory window in pixels. */
    protected int ySize = 166;
    
    protected GuiButton toggleBtn;

    // For rendering
    private Random rand = new Random();
    private int updateCounter = 0;
    
    public GuiGuard(EntityStoneGolem guard) {
        super();
        
        this.guard = guard;
    }
    
    @Override
    public void initGui() {
    	super.initGui();
    	
    	toggleBtn = new GuiButton(TOGGLE, width / 2 - 100, height - 48, "Toggle Stance");
        buttonList.add(toggleBtn);        
        toggleBtn.enabled = true;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {    	
        this.drawDefaultBackground();
        
        // Background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.renderEngine.bindTexture(background);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;             
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        
        // Alignment vars
        int lShift = 32, rShift = 60;
        int lStep = ySize / 8, sStep = ySize / 10;   
        x += 35;
        y += 24;
        
        // Foreground        
        Text.drawString(fontRendererObj, Text.Align.LEFT, guard.isFollowing() ? "Following" : "Stationed", x, y, 14737632);
        y += lStep;
        
        Text.drawString(fontRendererObj, Text.Align.LEFT, "Chase Distance: " + guard.getChaseRange(), x, y, 14737632);
        y += lStep;
        
        if (!guard.isFollowing()) {
        	Text.drawString(fontRendererObj, Text.Align.LEFT, "Station Radius: " + guard.getStationRadius(), x, y, 14737632);
        	y += lStep;
        }
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        
        renderHealth(x - 15, (height - ySize) / 2 + 22, guard);
        
        super.drawScreen(par1, par2, par3);
    }
    
    @Override
    protected void actionPerformed(GuiButton btn) {
        if (btn.enabled) {
        	if (btn.id == TOGGLE) {
        		NBTTagCompound nbt = new NBTTagCompound();
        		if (guard.isFollowing()) {
        			EntityStoneGolem.writeGuardStance(nbt, 32, 8, false);
        			
        			Guards.net.sendToServer(new SynchGuardStance(guard.getEntityId(), nbt));
        		} else {
        			EntityStoneGolem.writeGuardStance(nbt, 32, 0, true);
        			
        			Guards.net.sendToServer(new SynchGuardStance(guard.getEntityId(), nbt));
        		}
        	}
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    public void renderHealth(int left, int top, EntityLiving entity) {
        boolean highlight = entity.hurtResistantTime / 3 % 2 == 1;

        if (entity.hurtResistantTime < 10) {
            highlight = false;
        }

        IAttributeInstance attrMaxHealth = entity.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        int health = MathHelper.ceiling_float_int(entity.getHealth());
        int healthLast = MathHelper.ceiling_float_int(entity.prevHealth);
        float healthMax = (float) attrMaxHealth.getAttributeValue();
        float absorb = entity.getAbsorptionAmount();

        int regen = -1;
        if (entity.isPotionActive(Potion.regeneration)) {
            regen = updateCounter % 25;
        }

        final int TOP = 9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;

        if (entity.isPotionActive(Potion.poison)) MARGIN += 36;
        else if (entity.isPotionActive(Potion.wither)) MARGIN += 72;
        float absorbRemaining = absorb;

        for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            int x = left;
            int y = top + i % 10 * 8;

            if (health <= 4) y += rand.nextInt(2);
            if (i == regen) y -= 2;

            drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

            if (highlight) {
                if (i * 2 + 1 < healthLast) 
                    drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); // 6
                else if (i * 2 + 1 == healthLast) 
                    drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); // 7
            }

            if (absorbRemaining > 0.0F) {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                    drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); // 17
                else
                    drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); // 16
                absorbRemaining -= 2.0F;
            } else {
                if (i * 2 + 1 < health) 
                    drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); // 4
                else if (i * 2 + 1 == health) 
                    drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); // 5
            }
        }
    }
}