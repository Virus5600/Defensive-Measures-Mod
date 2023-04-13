package com.virus5600.DefensiveMeasures.item.turrets.cannon;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class CannonBaseItem extends Item {

	public CannonBaseItem(final Settings settings) {
		super(
			// MAX STACK SIZE
			settings.maxCount(16)
			// RARITY
			.rarity(Rarity.COMMON)
		);

		// FUEL
		FuelRegistry.INSTANCE.add(this, 300);
	}
}
