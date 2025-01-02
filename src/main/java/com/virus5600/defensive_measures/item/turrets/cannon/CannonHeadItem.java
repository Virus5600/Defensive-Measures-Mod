package com.virus5600.defensive_measures.item.turrets.cannon;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

/**
 * The class for the Cannon Head {@link Item item}.
 */
public class CannonHeadItem extends Item {
	public CannonHeadItem(Settings settings) {
		super(
			settings
				.maxCount(1)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}
}
