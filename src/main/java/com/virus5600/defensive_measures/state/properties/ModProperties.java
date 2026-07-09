package com.virus5600.defensive_measures.state.properties;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

import com.virus5600.defensive_measures.block.traps.tier1.BoltHeadBlock;

/**
 * Contains all block and fluid state properties for Defensive Measures mod.
 *
 * @since 1.1.2-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModProperties {
	/**
	 * A property that specifies the amount of bolt heads in an {@link BoltHeadBlock Bolt Head Block}.
	 * Value ranges from {@code 1} to {@code 4}.
	 */
	public static final IntegerProperty BOLT_HEADS = IntegerProperty.create("bolt_heads", 1, 4);
}
