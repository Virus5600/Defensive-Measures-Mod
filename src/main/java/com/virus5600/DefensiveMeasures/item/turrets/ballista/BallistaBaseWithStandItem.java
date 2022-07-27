package com.virus5600.DefensiveMeasures.item.turrets.ballista;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class BallistaBaseWithStandItem extends Item {

	public BallistaBaseWithStandItem(Settings settings) {
		super(		
			// MAX STACK SIZE
			settings.maxCount(16)
			// RARITY
			.rarity(Rarity.COMMON)
		);
		
		// FUEL
		FuelRegistry.INSTANCE.add(this, 600);
	}
}