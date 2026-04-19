package com.virus5600.defensive_measures.item.turrets.ballista;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

import com.virus5600.defensive_measures.item.interfaces.FuelItem;

/**
 * The class for the Ballista Bolt {@link Item item}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class BallistaBoltItem extends Item implements FuelItem {
	public BallistaBoltItem(net.minecraft.item.Item.Settings settings) {
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
