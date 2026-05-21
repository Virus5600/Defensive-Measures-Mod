package com.virus5600.defensive_measures.command;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class ModClientCommands {
	public static void registerCommands() {
		DefensiveMeasures.LOGGER.info("REGISTERING CLIENT COMMANDS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
