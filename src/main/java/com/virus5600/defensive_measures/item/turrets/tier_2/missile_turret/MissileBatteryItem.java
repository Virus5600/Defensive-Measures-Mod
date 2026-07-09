package com.virus5600.defensive_measures.item.turrets.tier_2.missile_turret;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * The class for the Missile Battery {@link Item item}.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MissileBatteryItem extends Item {
	public MissileBatteryItem(final Properties settings) {
		super(
			settings
				.stacksTo(16)
				.rarity(Rarity.UNCOMMON)
		);
	}
}
