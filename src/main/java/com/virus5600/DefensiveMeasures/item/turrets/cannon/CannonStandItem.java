package com.virus5600.DefensiveMeasures.item.turrets.cannon;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class CannonStandItem extends Item {

	public CannonStandItem(final Settings settings) {
		super(
			// MAX STACK SIZE
			settings.maxCount(16)
			// RARITY
			.rarity(Rarity.COMMON)
		);

		// FUEL
		FuelRegistry.INSTANCE.add(this, 600);
	}
}
