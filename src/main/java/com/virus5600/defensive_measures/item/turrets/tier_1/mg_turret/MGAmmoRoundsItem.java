package com.virus5600.defensive_measures.item.turrets.tier_1.mg_turret;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class MGAmmoRoundsItem extends Item {
	public MGAmmoRoundsItem(final Properties settings) {
		super(
			settings
				.stacksTo(32)
				.rarity(Rarity.COMMON)
		);
	}
}
