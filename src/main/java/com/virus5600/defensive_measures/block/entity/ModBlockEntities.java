package com.virus5600.defensive_measures.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.block.entity.traps.BaseLandmineBlockEntity;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModBlockEntities {
	// v1.2.0-beta
	public static final BlockEntityType<BaseLandmineBlockEntity> LAND_MINE;

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING BLOCKS FOR {}...", DefensiveMeasures.MOD_NAME);
	}

	// /////////////////// //
	// STATIC INITIALIZERS //
	// /////////////////// //

	static {
		LAND_MINE = RegistryHelper.registerBlockEntity(
			"land_mine", BaseLandmineBlockEntity::new,
			ModBlocks.LANDMINES
		);
	}
}
