package com.virus5600.defensive_measures.block;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

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
	// 1.0.0-beta
	public final static Block BOLT_HEAD = RegistryHelper.registerBlock("bolt_head", BoltHeadBlock::new, Settings.create());

	// 1.1.0-beta
	public final static Block TURRET_ASSEMBLY_STATION = RegistryHelper.registerBlock("turret_assembly_station", TurretAssemblyStationBlock::new, Settings.create());
	public final static Block ELECTRIC_FENCE = RegistryHelper.registerBlock("electric_fence", ElectricFenceBlock::new, Settings.create());

	public static void registerModBlocks() {
		DefensiveMeasures.LOGGER.info("REGISTERING BLOCKS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
