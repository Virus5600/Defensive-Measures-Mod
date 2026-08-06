package com.virus5600.defensive_measures.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import com.virus5600.defensive_measures.DefensiveMeasures;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModCommands {
	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING COMMON COMMANDS FOR {}...", DefensiveMeasures.MOD_NAME);

		// v1.2.0-beta
		CommandRegistrationCallback.EVENT.register(PlayAnimationCommand::register);
	}
}
