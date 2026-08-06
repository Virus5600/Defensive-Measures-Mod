package com.virus5600.defensive_measures.color;

import com.virus5600.defensive_measures.DefensiveMeasures;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModClientColorProviderRegistry {
	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING COLOR PROVIDERS FOR {}...", DefensiveMeasures.MOD_NAME);

		ModClientBlockColors.init();
		ModClientItemColors.init();
	}
}
