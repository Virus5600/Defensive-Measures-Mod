package com.virus5600.defensive_measures.advancement.criterion;

import com.virus5600.defensive_measures.DefensiveMeasures;

import net.minecraft.advancement.criterion.Criteria;

/**
 * Registers custom criteria for the mod.
 * <br><br>
 * This is used to create custom criteria for the mod, which can be used
 * to trigger advancements when certain conditions are met. Custom criteria
 * can also be used for player statistics, which can be used to track player
 * progress.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModCriterion {

	public static final TurretItemRetrievedCriterion TURRET_ITEM_RETRIEVED_CRITERION = Criteria.register("turret_item_retrieved", new TurretItemRetrievedCriterion());

	public static void registerModCriterion() {
		DefensiveMeasures.LOGGER.info("REGISTERING CRITERION FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
