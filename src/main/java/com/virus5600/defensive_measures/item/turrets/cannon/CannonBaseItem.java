package com.virus5600.defensive_measures.item.turrets.cannon;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

/**
 * The class for the Cannon Base {@link Item item}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class CannonBaseItem extends Item {
	public static final CookingFuel COOKING_FUEL = new CookingFuel(
		new ResolvableInt.Constant(300),
		ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)
	);

	public CannonBaseItem(Properties settings) {
		super(
			settings
				.stacksTo(16)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)		// RARITY
				.component(DataComponents.COOKING_FUEL, COOKING_FUEL)
		);
	}
}
