package com.virus5600.defensive_measures.block;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.util.RegistryUtil;

public class ModBlocks {
	// 1.0.0
	public final static Block ARROWHEAD = RegistryUtil.registerBlock("arrowhead", ArrowheadBlock::new, Settings.create());

	public static void registerModBlocks() {
		DefensiveMeasures.LOGGER.info("REGISTERING BLOCKS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
