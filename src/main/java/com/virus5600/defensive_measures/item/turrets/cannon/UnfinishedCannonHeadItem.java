package com.virus5600.defensive_measures.item.turrets.cannon;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class UnfinishedCannonHeadItem extends Item {
	public UnfinishedCannonHeadItem(Settings settings) {
		super(
			settings
				.maxCount(1)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}
}
