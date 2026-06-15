package com.virus5600.defensive_measures.item.turrets.mg_turret;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class MGAmmoCaseItem extends Item {
	public MGAmmoCaseItem(final Properties settings) {
		super(
			settings
				.stacksTo(16)
				.rarity(Rarity.COMMON)
		);
	}
}
