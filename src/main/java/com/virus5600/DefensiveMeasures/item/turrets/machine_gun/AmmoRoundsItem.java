package com.virus5600.DefensiveMeasures.item.turrets.machine_gun;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class AmmoRoundsItem extends Item {

	public AmmoRoundsItem(Settings settings) {
		super(
			// MAX STACK SIZE
			settings.maxCount(32)
			// RARITY
			.rarity(Rarity.UNCOMMON)
		);
	}
}