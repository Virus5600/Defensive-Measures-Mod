package com.virus5600.defensive_measures;

import com.virus5600.defensive_measures.advancement.criterion.ModCriterion;
import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.item.ModItemGroups;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.networking.ModPackets;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point of the mod.
 * <br><br>
 * This class holds the Mod ID, name, and Logger instances, along with common initialization processes and
 * utility methods that can be used throughout the mod's development.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
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

		// Modded Stuff's Registration - Server Side
		ModDamageTypes.registerDamageTypes();
		ModItemGroups.registerModItemGroups();
		ModItems.registerModItems();
		ModSoundEvents.registerSoundEvents();
		ModBlocks.registerModBlocks();
		ModCriterion.registerModCriterion();
		ModEntities.registerModEntityAttributes();
		ModParticles.registerParticles();
		ModPackets.registerHandlers();

		LOGGER.info("{} MAIN ENTRY POINT INITIALIZED.", MOD_NAME);
	}

	/**
	 * Utility method to print an error message to the console and log file.
	 *
	 * @param e The exception that was thrown.
	 */
	public static void printErr(Exception e) {
		e.printStackTrace(System.out);

		DefensiveMeasures.LOGGER.error("");
		DefensiveMeasures.LOGGER.error("\t {} ERROR OCCURRED\t ", DefensiveMeasures.MOD_ID.toUpperCase());
		DefensiveMeasures.LOGGER.error("===== ERROR MSG START =====");
		DefensiveMeasures.LOGGER.error("LOCALIZED ERROR MESSAGE:");
		DefensiveMeasures.LOGGER.error(e.getLocalizedMessage());
		DefensiveMeasures.LOGGER.error("");
		DefensiveMeasures.LOGGER.error("ERROR MESSAGE:");
		DefensiveMeasures.LOGGER.error(e.getMessage());
		DefensiveMeasures.LOGGER.error("===== ERROR MSG END =====");
		DefensiveMeasures.LOGGER.error("");
	}
}
