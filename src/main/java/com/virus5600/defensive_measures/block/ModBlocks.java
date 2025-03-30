package com.virus5600.defensive_measures.block;

import com.virus5600.defensive_measures.util.BlockUtil;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.util.RegistryUtil;

/**
 * Register and store all custom blocks for the mod.
 * <br><br>
 * This class is used to register all custom blocks that are added
 * within this mod, allowing for easy access to them when needed by
 * any other class.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModBlocks {
	// 1.0.0
	public final static Block ARROWHEAD = RegistryUtil.registerBlock(
		"arrowhead",
		ArrowheadBlock::new, Settings.create(),
		BlockUtil.BlockCategory.STONE
	);

	public static void registerModBlocks() {
		DefensiveMeasures.LOGGER.info("REGISTERING BLOCKS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
