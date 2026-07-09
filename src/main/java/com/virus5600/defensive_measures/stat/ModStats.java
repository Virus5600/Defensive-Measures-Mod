package com.virus5600.defensive_measures.stat;

import net.minecraft.stats.StatFormatter;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * Contains all the stats registered for this mod.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModStats {
	// ///// //
	// STATS //
	// ///// //

	// v1.1.0-beta //
	public static final Identifier INTERACT_WITH_TURRET_ASSEMBLY_STATION = RegistryHelper.registerStat("interact_with_turret_assembly_station", StatFormatter.DEFAULT);
	// v1.2.0-beta //
	public static final Identifier INTERACT_WITH_WORKSHOP = RegistryHelper.registerStat("interact_with_workshop", StatFormatter.DEFAULT);

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING STATS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
