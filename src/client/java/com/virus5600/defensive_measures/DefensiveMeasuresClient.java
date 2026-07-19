package com.virus5600.defensive_measures;

import com.virus5600.defensive_measures.color.ModClientColorProviderRegistry;
import net.fabricmc.api.ClientModInitializer;

import com.virus5600.defensive_measures.command.ModClientCommands;
import com.virus5600.defensive_measures.gui.screen.ingame.ModHandledScreens;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.networking.ModClientPackets;
import com.virus5600.defensive_measures.particle.ModClientParticles;
import com.virus5600.defensive_measures.renderer.ModEntityRenderers;

/**
 * The second main entry point of the mod.
 * <br><br>
 * This class holds the client-specific initialization processes
 * along with the needed C2S packet registrations, if there are any.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class DefensiveMeasuresClient implements ClientModInitializer {

	/**
	 * Client side initialization.
	 */
	@Override
	public void onInitializeClient() {
		DefensiveMeasures.LOGGER.info("INITIALIZING CLIENT ENTRY POINT FOR {}...", DefensiveMeasures.MOD_NAME);

		// Renderers
		ModEntityRenderers.registerEntityRenderers();
		ModClientParticles.registerParticles();
		ModClientColorProviderRegistry.init();

		// Models
		ModEntityModels.registerEntityModels();

		// Screens
		ModHandledScreens.registerScreens();

		// Commands
		ModClientCommands.registerCommands();

		// Networking
		ModClientPackets.registerClientPackets();

		DefensiveMeasures.LOGGER.info("{} CLIENT ENTRY POINT INITIALIZED.", DefensiveMeasures.MOD_NAME);
	}
}
