package com.virus5600.defensive_measures.advancement.criterion;

import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import com.virus5600.defensive_measures.DefensiveMeasures;

/**
 * Registers custom criteria for the mod.
 * <br><br>
 * This is used to create custom criteria for the mod, which can be used
 * to trigger advancements when certain conditions are met. Custom criteria
 * can also be used for player statistics, which can be used to track player
 * progress.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModCriterion {

	public static final TurretItemRetrievedCriterion TURRET_ITEM_RETRIEVED_CRITERION = register("turret_item_retrieved", new TurretItemRetrievedCriterion());

	public static void registerModCriterion() {
		DefensiveMeasures.LOGGER.info("REGISTERING CRITERION FOR {}...", DefensiveMeasures.MOD_NAME);
	}

	private static <T extends CriterionTrigger<?>> T register(final String name, final T criterion) {
		return (T)(Registry.register(BuiltInRegistries.TRIGGER_TYPES, name, criterion));
	}
}
