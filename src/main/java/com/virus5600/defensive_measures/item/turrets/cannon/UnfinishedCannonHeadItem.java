package com.virus5600.defensive_measures.item.turrets.cannon;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

/**
 * The class for the Unfinished Cannon Head {@link Item item}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class UnfinishedCannonHeadItem extends Item {
	public UnfinishedCannonHeadItem(Settings settings) {
		super(
			settings
				.maxCount(1)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);
	}
}
