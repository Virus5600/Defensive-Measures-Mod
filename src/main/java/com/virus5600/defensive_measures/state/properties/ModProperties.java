package com.virus5600.defensive_measures.state.properties;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

import com.virus5600.defensive_measures.block.BoltHeadBlock;

/**
 * Contains all block and fluid state properties for Defensive Measures mod.
 */
public class ModProperties {
	/**
	 * A property that specifies the amount of bolt heads in an {@link BoltHeadBlock Bolt Head Block}.
	 * Value ranges from {@code 1} to {@code 4}.
	 */
	public static final IntegerProperty BOLT_HEADS = IntegerProperty.create("bolt_heads", 1, 4);
}
