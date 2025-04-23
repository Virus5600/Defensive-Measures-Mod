package com.virus5600.defensive_measures.util.base.interfaces.items;

/**
 * An interface that <b>should</b> be used by any items that can
 * be used as fuel in a furnace or other fuel consuming blocks.
 * <br><br>
 * This interface is used to get the burn time of the item in ticks
 * using the {@link #getFuelTime()} method, which is defined by the
 * implementing class.
 * <br><br>
 * This interface is created due to the limitations of the vanilla
 * API, which does not allow for custom items to easily define
 * themselves as fuel items without potentially breaking other
 * vanilla mechanics.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public interface FuelItem {
	/**
	 * Returns the amount of time the item can be used as fuel in ticks.
	 * One tick is 1/20 of a second so a fuel time of 20 would be 1 second.
	 *
	 * @return the burn time of the item in ticks.
	 */
	int getFuelTime();
}
