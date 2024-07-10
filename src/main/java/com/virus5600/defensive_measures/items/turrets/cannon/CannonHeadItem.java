package com.virus5600.defensive_measures.items.turrets.cannon;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class CannonHeadItem extends Item {
	public CannonHeadItem(Settings settings) {
		super(
			settings
				.maxCount(1)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}
}
