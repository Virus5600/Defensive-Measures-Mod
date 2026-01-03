package com.virus5600.defensive_measures.state.properties;

import net.minecraft.state.property.IntProperty;

/**
 * Contains all block and fluid state properties for Defensive Measures mod.
 */
public class ModProperties {
	/**
	 * A property that specifies the amount of arrowheads in an {@link com.virus5600.defensive_measures.block.ArrowheadBlock Arrowhead Block}.
	 * Value ranges from {@code 1} to {@code 4}.
	 */
	public static final IntProperty ARROWHEADS = IntProperty.of("arrowheads", 1, 4);
}
