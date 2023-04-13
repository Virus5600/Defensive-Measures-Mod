package com.virus5600.DefensiveMeasures.item.turrets.ballista;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class BallistaArrowItem extends Item {

	public BallistaArrowItem(final Settings settings) {
		super(
			// MAX STACK SIZE
			settings.maxCount(64)
			// RARITY
			.rarity(Rarity.COMMON)
		);

		// FUEL
		FuelRegistry.INSTANCE.add(this, 200);
	}
}
