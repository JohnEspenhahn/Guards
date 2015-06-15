package com.hahn.guards;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Guards.MODID, name = "Guards", version = Guards.VERSION)
public class Guards {
    public static final String MODID = "guards";
    public static final String VERSION = "1.0";
    
    @Instance(MODID)
    public static Guards instance;
   
    @SidedProxy(clientSide="com.hahn.guards.client.ClientProxy", serverSide="com.hahn.guards.CommonProxy")
    public static CommonProxy proxy;
    
    public static Block GuardSpawnerBlock;
    
    @EventHandler
    public void init(FMLInitializationEvent event) {    	
		GuardSpawnerBlock = new GuardSpawnerBlock().setHardness(0.5F).setStepSound(Block.soundTypeStone).setBlockName("guardSpawner").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("guards:guardspawner");
		GameRegistry.registerBlock(GuardSpawnerBlock, "guardSpawner");
		GameRegistry.addShapedRecipe(new ItemStack(GuardSpawnerBlock), "AAA", "A A", "AAA", 'A', Blocks.stonebrick);
		
		GameRegistry.registerTileEntity(TileEntityGuardSpawner.class, "guardSpawnerEntity");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new GuardEventHandler());
    }
}
