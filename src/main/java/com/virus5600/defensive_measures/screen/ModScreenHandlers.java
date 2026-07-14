package com.virus5600.defensive_measures.screen;

import net.minecraft.world.inventory.MenuType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * A class containing all the registered menus for the mod.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModScreenHandlers {
	// v1.1.0-beta
	public static final MenuType<TurretAssemblyStationScreenHandler> TURRET_ASSEMBLY_STATION = RegistryHelper.registerScreenHandlerType("turret_assembly_station", TurretAssemblyStationScreenHandler::new);
	// v1.2.0-beta
	public static final MenuType<WorkshopScreenHandler> WORKSHOP = RegistryHelper.registerScreenHandlerType("workshop", WorkshopScreenHandler::new);
	public static final MenuType<FabricationMatrixScreenHandler> FABRICATION_MATRIX = RegistryHelper.registerScreenHandlerType("fabrication_matrix", FabricationMatrixScreenHandler::new);

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING SCREEN HANDLERS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
