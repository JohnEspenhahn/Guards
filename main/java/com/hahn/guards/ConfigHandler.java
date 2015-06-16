package com.hahn.guards;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static void init(File file) {
		Configuration config = new Configuration(file);

		config.load();

		TileEntityGuardSpawner.SPAWN_DELAY = config.get("Guard Spawner", "Delay", 60).getInt();
		TileEntityGuardSpawner.AMNT_NEEDED = config.get("Guard Spawner", "Cost", 3*16).getInt();

		config.save();
	}

}
