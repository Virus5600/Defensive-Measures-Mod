package com.virus5600.defensive_measures.block;

import net.minecraft.world.level.block.Block;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.block.defenses.tier2.*;
import com.virus5600.defensive_measures.block.entity.ModBlockEntities;
import com.virus5600.defensive_measures.block.misc.tier1.*;
import com.virus5600.defensive_measures.block.misc.tier2.*;
import com.virus5600.defensive_measures.block.misc.tier3.*;
import com.virus5600.defensive_measures.block.traps.tier1.*;
import com.virus5600.defensive_measures.block.traps.tier3.*;

import java.util.List;

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
	// v1.0.0-beta
	public final static Block BOLT_HEAD = RegistryHelper.registerBlock("bolt_head", BoltHeadBlock::new);

	// v1.1.0-beta
	public final static Block TURRET_ASSEMBLY_STATION = RegistryHelper.registerBlock("turret_assembly_station", TurretAssemblyStationBlock::new);
	public final static Block ELECTRIC_FENCE = RegistryHelper.registerBlock("electric_fence", ElectricFenceBlock::new);

	// v1.2.0-beta
	public final static Block WORKSHOP = RegistryHelper.registerBlock("workshop", WorkshopBlock::new);
	public final static Block FABRICATION_MATRIX = RegistryHelper.registerBlock("fabrication_matrix", FabricationMatrixBlock::new);
	public final static Block ANTI_PERSONNEL_MINE_M14 = RegistryHelper.registerBlock("anti_personnel_mine_m14", AntiPersonnelMineM14Block::new);
	public final static Block ANTI_TANK_MINE_HAWKINS = RegistryHelper.registerBlock("anti_tank_mine_hawkins", AntiTankMineHawkinsBlock::new);

	public final static List<Block> LANDMINES;

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING BLOCKS FOR {}...", DefensiveMeasures.MOD_NAME);

		ModBlockEntities.init();
	}

	// /////////////////// //
	// STATIC INITIALIZERS //
	// /////////////////// //

	static {
		LANDMINES = List.of(
			// ANTI-PERSONNEL
			ANTI_PERSONNEL_MINE_M14,

			// ANTI-TAK
			ANTI_TANK_MINE_HAWKINS
		);
	}
}
