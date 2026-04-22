package com.virus5600.defensive_measures.stat;

import com.virus5600.defensive_measures.DefensiveMeasures;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures._helper.RegistryHelper;

public class ModStats {
	public static final Identifier INTERACT_WITH_TURRET_ASSEMBLY_STATION = RegistryHelper.registerStat("interact_with_turret_assembly_station", StatFormatter.DEFAULT);

	public static void registerStats() {
		DefensiveMeasures.LOGGER.info("REGISTERING STATS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
