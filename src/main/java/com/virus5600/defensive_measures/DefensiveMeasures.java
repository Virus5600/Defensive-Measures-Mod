package com.virus5600.defensive_measures;

import com.virus5600.defensive_measures.entities.ModEntities;
import com.virus5600.defensive_measures.items.ModItemGroups;
import com.virus5600.defensive_measures.items.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point of the mod.
 *
 * This class holds the Mod ID, name, and Logger instances, along with common initialization processes and
 * utility methods that can be used throughout the mod's development.
 */
public class DefensiveMeasures implements ModInitializer {
	/**
	 * Defines the mod's unique identifier.
	 */
	public static final String MOD_ID = "dm";
	/**
	 * Defines the mod's name.
	 */
	public static final String MOD_NAME = "DefensiveMeasures";
	/**
	 * Defines the mod's logger instance.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(DefensiveMeasures.MOD_NAME);

	/**
	 * Main entry point of the mod. Initializes all server side logic.
	 */
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Mod...");

		// Modded Stuff's Registration

		ModItemGroups.registerModItemGroups();
		ModItems.registerModItems();
		ModEntities.registerModEntityAttributes();

		// Networking part

		LOGGER.info("Mod Initialized.");
	}
}
