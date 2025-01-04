package com.virus5600.defensive_measures.item.turrets.ballista;

import com.virus5600.defensive_measures.item.interfaces.FuelItem;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

/**
 * The class for the Ballista Arrow {@link Item item}.
 */
public class BallistaArrowItem extends Item implements FuelItem {
	public BallistaArrowItem(Settings settings) {
		super(
			settings
				.maxCount(64)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}

	public int getFuelTime() {
		return 200;
	}
}
