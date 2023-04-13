package com.virus5600.DefensiveMeasures.item.turrets.machine_gun;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class MachineGunStandItem extends Item {

	public MachineGunStandItem(final Settings settings) {
		super(
			// MAX STACK SIZE
			settings.maxCount(16)
			// RARITY
			.rarity(Rarity.UNCOMMON)
		);
	}
}
