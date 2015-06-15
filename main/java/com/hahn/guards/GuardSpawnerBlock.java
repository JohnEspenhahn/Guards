package com.hahn.guards;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.world.World;

public class GuardSpawnerBlock extends Block implements ITileEntityProvider {

	public GuardSpawnerBlock() {
		super(Material.rock);
		this.setHardness(7.5f);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
	public int quantityDropped(int i, int j, Random rand) {
		return 3 + rand.nextInt(4);
	}

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        return Item.getItemFromBlock(Blocks.cobblestone);
    }
    
    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
        if (world.isRemote) {
            return true;
        } else {
        	TileEntityGuardSpawner spawner = (TileEntityGuardSpawner) world.getTileEntity(x, y, z);
            if (spawner != null) {
            	if (spawner.getOwnerName() == null) {
            		spawner.setOwnerName(player.getCommandSenderName());
            	}
            	
                player.openGui(Guards.instance, GuiHandler.GUARD_SPAWNER, world, x, y, z);
            }

            return true;
        }
    }

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityGuardSpawner();
	}
	
}
