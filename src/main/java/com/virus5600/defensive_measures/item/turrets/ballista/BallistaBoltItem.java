package com.virus5600.defensive_measures.item.turrets.ballista;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import com.virus5600.defensive_measures.item.interfaces.FuelItem;

/**
 * The class for the Ballista Bolt {@link Item item}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BallistaBoltItem extends Item implements FuelItem {
	public BallistaBoltItem(Properties settings) {
		super(
			settings
				.stacksTo(64)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)		// RARITY
		);
	}

	public int getFuelTime() {
		return 200;
	}
}
