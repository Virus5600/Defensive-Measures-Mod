package com.virus5600.DefensiveMeasures;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.virus5600.DefensiveMeasures.registry.ItemGroupRegistry;
import com.virus5600.DefensiveMeasures.registry.ItemRegistry;

public class DefensiveMeasures implements ModInitializer {
	/**
	 * Initialized Logger with the Mod ID as its identifier.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger("dm");

	@Override
	/**
	 * Runs as soon as Minecraft is in a mod-load-ready state. However, some things (like resources) may still be uninitialized.
	 * <b>Proceed with mild caution.</b>
	 * 
	 * Registers all new components and objects added to the game.
	 */
	public void onInitialize() {
		ItemGroupRegistry.register();
		ItemRegistry.register();
	}
}