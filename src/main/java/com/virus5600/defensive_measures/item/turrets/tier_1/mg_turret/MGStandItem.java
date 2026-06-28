package com.virus5600.defensive_measures.item.turrets.tier_1.mg_turret;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class MGStandItem extends Item {
	public MGStandItem(final Properties settings) {
		super(
			settings
				.stacksTo(16)
				.rarity(Rarity.COMMON)
		);
	}
}
