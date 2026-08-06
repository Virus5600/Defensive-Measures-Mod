package com.virus5600.defensive_measures.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.screen.ModScreenHandlers;

/**
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public class ModHandledScreens {
	public static void registerScreens() {
		DefensiveMeasures.LOGGER.info("REGISTERING CLIENT SCREENS FOR {}...", DefensiveMeasures.MOD_NAME);

		MenuScreens.register(ModScreenHandlers.TURRET_ASSEMBLY_STATION, TurretAssemblyStationScreen::new);
		MenuScreens.register(ModScreenHandlers.WORKSHOP, WorkshopScreen::new);
		MenuScreens.register(ModScreenHandlers.FABRICATION_MATRIX, FabricationMatrixScreen::new);
	}
}
