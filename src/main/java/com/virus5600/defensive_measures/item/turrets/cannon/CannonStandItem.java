package com.virus5600.defensive_measures.item.turrets.cannon;

import com.virus5600.defensive_measures.item.interfaces.FuelItem;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

/**
 * The class for the Cannon Stand {@link Item item}.
 */
public class CannonStandItem extends Item implements FuelItem {
	public CannonStandItem(Settings settings) {
		super(
			settings
				.maxCount(16)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}

	@Override
	public int getFuelTime() {
		return 600;
	}
}
