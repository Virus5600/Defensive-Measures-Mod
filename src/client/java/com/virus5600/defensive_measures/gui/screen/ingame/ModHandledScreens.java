package com.virus5600.defensive_measures.gui.screen.ingame;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.screen.ModScreenHandlers;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModHandledScreens {
	private static final Map<ScreenHandlerType<?>, HandledScreens.Provider<?, ?>> PROVIDERS = Maps.newHashMap();

	public static void registerScreens() {
		DefensiveMeasures.LOGGER.info("REGISTERING CLIENT SCREENS FOR {}...", DefensiveMeasures.MOD_NAME);

		HandledScreens.register(ModScreenHandlers.TURRET_ASSEMBLY_STATION, TurretAssemblyStationScreen::new);
	}
}
