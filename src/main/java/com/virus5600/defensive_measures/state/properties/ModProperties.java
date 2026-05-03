package com.virus5600.defensive_measures.state.properties;

import com.virus5600.defensive_measures.block.BoltHeadBlock;
import net.minecraft.state.property.IntProperty;

/**
 * Contains all block and fluid state properties for Defensive Measures mod.
 */
public class ModProperties {
	/**
	 * A property that specifies the amount of bolt heads in an {@link BoltHeadBlock Bolt Head Block}.
	 * Value ranges from {@code 1} to {@code 4}.
	 */
	public static final IntProperty BOLT_HEADS = IntProperty.of("bolt_heads", 1, 4);
}
