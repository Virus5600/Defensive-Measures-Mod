package com.virus5600.defensive_measures.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.screen.ModScreenHandlers;

import com.google.common.collect.Maps;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModHandledScreens {
	private static final Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> PROVIDERS = Maps.newHashMap();

	public static void registerScreens() {
		DefensiveMeasures.LOGGER.info("REGISTERING CLIENT SCREENS FOR {}...", DefensiveMeasures.MOD_NAME);

		MenuScreens.register(ModScreenHandlers.TURRET_ASSEMBLY_STATION, TurretAssemblyStationScreen::new);
		MenuScreens.register(ModScreenHandlers.WORKSHOP, WorkshopScreen::new);
	}
}
