package com.virus5600.defensive_measures.item.turrets.cannon;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * The class for the Unfinished Cannon Head {@link Item item}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class UnfinishedCannonHeadItem extends Item {
	public UnfinishedCannonHeadItem(Properties settings) {
		super(
			settings
				.stacksTo(1)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}
}
