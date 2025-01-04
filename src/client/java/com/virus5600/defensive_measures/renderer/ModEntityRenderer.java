package com.virus5600.defensive_measures.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.renderer.entity.*;
import com.virus5600.defensive_measures.renderer.projectiles.*;

public class ModEntityRenderer {
	public static void registerEntityRenderers() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY RENDERERS FOR {}...", DefensiveMeasures.MOD_NAME);

		/////////////
		// TURRETS //
		/////////////

		// v1.0.0
		EntityRendererRegistry.register(ModEntities.CANNON_TURRET, CannonTurretRenderer::new);
		EntityRendererRegistry.register(ModEntities.BALLISTA_TURRET, BallistaTurretRenderer::new);

		/////////////////
		// PROJECTILES //
		/////////////////

		// v1.0.0
		EntityRendererRegistry.register(ModEntities.CANNONBALL, CannonballRenderer::new);
		EntityRendererRegistry.register(ModEntities.BALLISTA_ARROW, BallistaArrowRenderer::new);
	}
}
