package com.virus5600.defensive_measures.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class ModCommands {
	public static void registerCommands() {
		DefensiveMeasures.LOGGER.info("REGISTERING COMMON COMMANDS FOR {}...", DefensiveMeasures.MOD_NAME);

		// v1.1.0-beta
		CommandRegistrationCallback.EVENT.register(SetPoseCommand::register);
		CommandRegistrationCallback.EVENT.register(GetPoseCommand::register);
	}
}
