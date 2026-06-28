package com.virus5600.defensive_measures.item.turrets.tier_2.flame_turret;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class FlameBaseItem extends Item {
	public FlameBaseItem(final Properties settings) {
		super(
			settings
				.stacksTo(16)
				.rarity(Rarity.UNCOMMON)
		);
	}
}
