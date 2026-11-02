package com.virus5600.defensive_measures.item.turrets.tier_1.cannon;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

/**
 * The class for the Cannon Stand {@link Item item}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class CannonStandItem extends Item {
	/**
	 * The {@link CookingFuel cooking fuel} for the Cannon Stand {@link Item item}.
	 * Serves as a fuel source for 30 seconds (30 seconds * 20 ticks per second) and has a default speed multiplier for cooking.
	 */
	public static final CookingFuel COOKING_FUEL = new CookingFuel(
		new ResolvableInt.Constant(30 * 20),
		ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)
	);

	public CannonStandItem(Properties settings) {
		super(
			settings
				.stacksTo(16)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)		// RARITY
				.component(DataComponents.COOKING_FUEL, COOKING_FUEL)
		);
	}
}
