package com.virus5600.defensive_measures.item.interfaces;

public interface FuelItem {
	/**
	 * Returns the amount of time the item can be used as fuel in ticks.
	 * One tick is 1/20 of a second so a fuel time of 20 would be 1 second.
	 *
	 * @return the burn time of the item in ticks.
	 */
	public int getFuelTime();
}
