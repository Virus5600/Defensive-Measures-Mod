package com.virus5600.defensive_measures.color;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class ModClientColorProviderRegistry {
	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING COLOR PROVIDERS FOR {}...", DefensiveMeasures.MOD_NAME);

		ModClientBlockColors.init();
	}
}
