package com.virus5600.defensive_measures.block.state.properties;

import net.minecraft.world.level.block.state.properties.EnumProperty;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.block.misc.tier2.WorkshopBlock.WorkshopPart;
import com.virus5600.defensive_measures.block.misc.tier3.FabricationMatrixBlock.FabricationMatrixPart;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModBlockStateProperties {
	// v1.2.0-beta
	public static final EnumProperty<WorkshopPart> WORKSHOP_PART = EnumProperty.create("workshop_part", WorkshopPart.class);
	public static final EnumProperty<FabricationMatrixPart> FABRICATION_MATRIX_PART = EnumProperty.create("fabrication_matrix_part", FabricationMatrixPart.class);

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING BLOCK STATES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
