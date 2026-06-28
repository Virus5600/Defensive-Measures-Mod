package com.virus5600.defensive_measures.item.turrets.tier_1.cannon;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * The class for the Cannon Head {@link Item item}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class CannonHeadItem extends Item {
	public CannonHeadItem(Properties settings) {
		super(
			settings
				.stacksTo(1)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}
}
