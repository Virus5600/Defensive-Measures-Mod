package com.virus5600.defensive_measures.color;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class ModColorProviderRegistry {
	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING COLOR PROVIDERS FOR {}...", DefensiveMeasures.MOD_NAME);

		ModBlockColors.init();
	}
}
