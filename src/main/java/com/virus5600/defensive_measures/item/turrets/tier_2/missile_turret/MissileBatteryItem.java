package com.virus5600.defensive_measures.item.turrets.tier_2.missile_turret;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class MissileBatteryItem extends Item {
	public MissileBatteryItem(final Properties settings) {
		super(
			settings
				.stacksTo(16)
				.rarity(Rarity.UNCOMMON)
		);
	}
}
