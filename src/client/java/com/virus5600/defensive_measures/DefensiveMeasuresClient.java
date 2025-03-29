package com.virus5600.defensive_measures;

import com.virus5600.defensive_measures.particle.ModClientParticles;
import com.virus5600.defensive_measures.renderer.ModEntityRenderer;
import net.fabricmc.api.ClientModInitializer;

/**
 * The second main entry point of the mod.
 * <br><br>
 * This class holds the client-specific initialization processes
 * along with the needed C2S packet registrations, if there are any.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class DefensiveMeasuresClient implements ClientModInitializer {

	/**
	 * Client side initialization.
	 */
	@Override
	public void onInitializeClient() {
		DefensiveMeasures.LOGGER.info("INITIALIZING CLIENT ENTRY POINT FOR {}...", DefensiveMeasures.MOD_NAME);

		// Renderers
		ModEntityRenderer.registerEntityRenderers();
		ModClientParticles.registerParticles();

		// Networking

		DefensiveMeasures.LOGGER.info("{} CLIENT ENTRY POINT INITIALIZED.", DefensiveMeasures.MOD_NAME);
	}
}
