package com.hahn.guards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.minecraft.block.Block;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import com.hahn.guards.command.CommandPeace;
import com.hahn.guards.command.CommandRelations;
import com.hahn.guards.command.CommandWar;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
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
    	proxy.registerRenderers();
    	
		GuardSpawnerBlock = new GuardSpawnerBlock().setHardness(0.5F).setStepSound(Block.soundTypeStone).setBlockName("guardSpawner").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("guards:guardspawner");
		GameRegistry.registerBlock(GuardSpawnerBlock, "guardSpawner");
		GameRegistry.addShapedRecipe(new ItemStack(GuardSpawnerBlock), "AAA", "A A", "AAA", 'A', Blocks.stonebrick);
		
		GameRegistry.registerTileEntity(TileEntityGuardSpawner.class, "guardSpawnerEntity");
		
		EntityRegistry.registerGlobalEntityID(EntityStoneGolem.class, "StoneGolem", EntityRegistry.findGlobalUniqueEntityId(), 52135, 265734);
        EntityRegistry.registerModEntity(EntityStoneGolem.class, "StoneGolem", EntityRegistry.findGlobalUniqueEntityId(), this, 64, 3, true);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new GuardEventHandler());
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        try {
			GuardEventHandler.read(new ObjectInputStream(new FileInputStream(getSaveFile())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        ServerCommandManager manager = (ServerCommandManager) MinecraftServer.getServer().getCommandManager();
        manager.registerCommand(new CommandWar());
        manager.registerCommand(new CommandPeace());
        manager.registerCommand(new CommandRelations());
    }
    
    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        try {
			GuardEventHandler.save(new ObjectOutputStream(new FileOutputStream(getSaveFile())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public File getSaveFile() {
    	return new File(DimensionManager.getCurrentSaveRootDirectory(), "guards.data");
    }
}
