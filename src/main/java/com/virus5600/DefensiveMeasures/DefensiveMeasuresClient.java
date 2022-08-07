package com.virus5600.DefensiveMeasures;

import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.particle.ModParticles;

import net.fabricmc.api.ClientModInitializer;

public class DefensiveMeasuresClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModEntities.registerModEntities();
		ModParticles.registerClientParticles();
	}
}