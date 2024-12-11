package com.virus5600.defensive_measures.advancement.criterion;

import com.virus5600.defensive_measures.DefensiveMeasures;

import net.minecraft.advancement.criterion.Criteria;

public class ModCriterion {

	public static final TurretItemRetrievedCriterion TURRET_ITEM_RETRIEVED_CRITERION = Criteria.register("turret_item_retrieved", new TurretItemRetrievedCriterion());

	public static void registerModCriterion() {
		DefensiveMeasures.LOGGER.info("REGISTERING CRITERION FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
