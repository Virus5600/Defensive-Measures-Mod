package com.virus5600.defensive_measures.item.turrets.tier_2.aa_turret;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class AATraversePlatformItem extends Item {
	public AATraversePlatformItem(final Properties settings) {
		super(
			settings
				.stacksTo(16)
				.rarity(Rarity.UNCOMMON)
		);
	}
}
