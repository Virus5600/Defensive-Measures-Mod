package com.virus5600.defensive_measures.item.turrets.tier_1.mg_turret;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * The class for the MG Ammo Case {@link Item item}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MGAmmoCaseItem extends Item {
	public MGAmmoCaseItem(final Properties settings) {
		super(
			settings
				.stacksTo(16)
				.rarity(Rarity.COMMON)
		);
	}
}
