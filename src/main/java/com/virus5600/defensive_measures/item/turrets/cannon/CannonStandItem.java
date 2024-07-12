package com.virus5600.defensive_measures.item.turrets.cannon;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

/**
 * The class for the Cannon Stand {@link Item item}.
 */
public class CannonStandItem extends Item {
	public CannonStandItem(Settings settings) {
		super(
			settings
				.maxCount(16)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);

		// FUEL
		FuelRegistry.INSTANCE.add(this, 600);
	}
}
