package com.virus5600.defensive_measures.item.turrets.tier_1.ballista;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import com.virus5600.defensive_measures.item.interfaces.FuelItem;

/**
 * The class for the Ballista Bow {@link Item item}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BallistaBowItem extends Item implements FuelItem {
	public BallistaBowItem(Properties settings) {
		super(
			settings
				.stacksTo(16)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}

	public int getFuelTime() {
		return 30 * 20;
	}
}
