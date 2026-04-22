package com.virus5600.defensive_measures.screen;

import net.minecraft.screen.ScreenHandlerType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

public class ModScreenHandlers {
	public static final ScreenHandlerType<TurretAssemblyStationScreenHandler> TURRET_ASSEMBLY_STATION = RegistryHelper.registerScreenHandlerType("turret_assembly_station", TurretAssemblyStationScreenHandler::new);

	public static void registerScreenHandlers() {
		DefensiveMeasures.LOGGER.info("REGISTERING SCREEN HANDLERS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
