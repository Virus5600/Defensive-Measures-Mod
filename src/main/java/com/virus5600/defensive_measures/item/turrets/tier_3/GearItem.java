package com.virus5600.defensive_measures.item.turrets.tier_3;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * The class for the Gear {@link Item item}.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class GearItem extends Item {
	public GearItem(final Properties settings) {
		super(
			settings
				.stacksTo(64)
				.rarity(Rarity.UNCOMMON)
		);
	}
}
