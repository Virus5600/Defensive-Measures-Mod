package com.virus5600.defensive_measures.item.turrets.mg_turret;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class MGHeadItem extends Item {
	public MGHeadItem(final net.minecraft.item.Item.Settings settings) {
		super(
			settings
				.maxCount(16)
				.rarity(Rarity.UNCOMMON)
		);
	}
}
