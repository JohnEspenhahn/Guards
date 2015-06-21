package com.hahn.guards;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static void init(File file) {
		Configuration config = new Configuration(file);

		config.load();

		TileEntityGuardSpawner.SPAWN_DELAY = config.get("Guard Spawner", "Delay", 30*30).getInt();
		System.out.println(Guards.MODID + " " + Guards.VERSION + " guard spawner delay " + TileEntityGuardSpawner.SPAWN_DELAY);
		
		TileEntityGuardSpawner.AMNT_NEEDED = config.get("Guard Spawner", "Cost", 3*16).getInt();
		System.out.println(Guards.MODID + " " + Guards.VERSION + " guard spawner cost " + TileEntityGuardSpawner.AMNT_NEEDED);

		config.save();
	}

}
