package com.virus5600.defensive_measures.block.state.properties;

import net.minecraft.world.level.block.state.properties.EnumProperty;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.block.misc.tier2.WorkshopBlock.WorkshopPart;

public class ModBlockStateProperties {
	// v1.2.0-beta
	public static final EnumProperty<WorkshopPart> WORKSHOP_PART = EnumProperty.create("workshop_part", WorkshopPart.class);

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING BLOCK STATES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
