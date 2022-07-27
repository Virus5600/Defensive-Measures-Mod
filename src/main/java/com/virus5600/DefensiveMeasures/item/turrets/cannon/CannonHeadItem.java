package com.virus5600.DefensiveMeasures.item.turrets.cannon;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class CannonHeadItem extends Item {

	public CannonHeadItem(Settings settings) {
		super(
			// MAX STACK SIZE
			settings.maxCount(1)
			// RARITY
			.rarity(Rarity.COMMON)
		);
	}
}