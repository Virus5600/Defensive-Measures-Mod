package com.virus5600.DefensiveMeasures;

import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.entity.client.model.renderer.CannonTurretRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DefensiveMeasuresClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModEntities.registerModEntities();
	}
}