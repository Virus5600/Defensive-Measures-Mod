package com.virus5600.DefensiveMeasures;

import com.virus5600.DefensiveMeasures.entity.client.ModEntityRenderers;
import com.virus5600.DefensiveMeasures.networking.ModPackets;
import com.virus5600.DefensiveMeasures.particle.ModParticles;

import net.fabricmc.api.ClientModInitializer;

public class DefensiveMeasuresClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Client Side Initialization
		ModEntityRenderers.registerModEntityRenderers();
		ModParticles.registerClientParticles();

		// Networking part
		ModPackets.registerS2CPackets();
	}
}