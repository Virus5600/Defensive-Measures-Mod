package com.virus5600.defensive_measures.item.turrets.mg_turret;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class MGAmmoRoundsItem extends Item {
	public MGAmmoRoundsItem(final net.minecraft.item.Item.Settings settings) {
		super(
			settings
				.maxCount(32)
				.rarity(Rarity.UNCOMMON)
		);
	}
}
