package com.virus5600.defensive_measures.item.turrets.cannon;

import com.virus5600.defensive_measures.util.base.interfaces.items.FuelItem;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

/**
 * The class for the Cannon Base {@link Item item}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class CannonBaseItem extends Item implements FuelItem {
	public CannonBaseItem(Settings settings) {
		super(
			settings
				.maxCount(16)			// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
		);

	}

	// FUEL
	public int getFuelTime() {
		return 300;
	}
}
