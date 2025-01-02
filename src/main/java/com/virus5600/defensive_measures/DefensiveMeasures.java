package com.virus5600.defensive_measures;

import com.virus5600.defensive_measures.advancement.criterion.ModCriterion;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.item.ModItemGroups;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
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
		LOGGER.info("INITIALIZING MAIN ENTRY POINT FOR {}...", MOD_NAME);

		// Use https://github.com/Khazoda/basic-weapons/blob/latest-stable/src/main/java/com/seacroak/basicweapons/util/Reggie.java as a reference

		// Modded Stuff's Registration
		ModItemGroups.registerModItemGroups();
		ModItems.registerModItems();
		ModSoundEvents.registerSoundEvents();
		ModCriterion.registerModCriterion();
		ModParticles.registerParticles();
		ModEntities.registerModEntityAttributes();

		// Networking part

		LOGGER.info("{} MAIN ENTRY POINT INITIALIZED.", MOD_NAME);
	}
}
