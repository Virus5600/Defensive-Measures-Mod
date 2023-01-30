package com.virus5600.DefensiveMeasures.advancement.criterion;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;

public class ModCriterion {

	public static final TurretItemRetrievedCriterion TURRET_ITEM_RETRIEVED_CRITERION = new TurretItemRetrievedCriterion();

	private static Criterion<?> registerCriterion(String name, Criterion<?> criterion) {
		return Criteria.register(criterion);
	}

	public static void registerModCriterion() {
		DefensiveMeasures.LOGGER.debug("REGISTERING CRITERIONS FOR " + DefensiveMeasures.MOD_NAME);

		registerCriterion("turret_item_retrieved", TURRET_ITEM_RETRIEVED_CRITERION);
	}
}