package com.virus5600.defensive_measures.screen;

import net.minecraft.world.inventory.MenuType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

public class ModScreenHandlers {
	public static final MenuType<TurretAssemblyStationScreenHandler> TURRET_ASSEMBLY_STATION = RegistryHelper.registerScreenHandlerType("turret_assembly_station", TurretAssemblyStationScreenHandler::new);
	public static final MenuType<WorkshopScreenHandler> WORKSHOP = RegistryHelper.registerScreenHandlerType("workshop", WorkshopScreenHandler::new);

	public static void registerScreenHandlers() {
		DefensiveMeasures.LOGGER.info("REGISTERING SCREEN HANDLERS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
