package com.virus5600.defensive_measures.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * Register and store all custom blocks for the mod.
 * <br><br>
 * This class is used to register all custom blocks that are added
 * within this mod, allowing for easy access to them when needed by
 * any other class.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModBlocks {
	// 1.0.0-beta
	public final static Block BOLT_HEAD = RegistryHelper.registerBlock("bolt_head", BoltHeadBlock::new, Properties.of());

	// 1.1.0-beta
	public final static Block TURRET_ASSEMBLY_STATION = RegistryHelper.registerBlock("turret_assembly_station", TurretAssemblyStationBlock::new, Properties.of());
	public final static Block ELECTRIC_FENCE = RegistryHelper.registerBlock("electric_fence", ElectricFenceBlock::new, Properties.of());

	public static void registerModBlocks() {
		DefensiveMeasures.LOGGER.info("REGISTERING BLOCKS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
