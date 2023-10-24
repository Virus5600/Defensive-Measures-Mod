package com.virus5600.DefensiveMeasures.entity.client;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.entity.client.renderer.entity.AntiAirProjectileRenderer;
import com.virus5600.DefensiveMeasures.entity.client.renderer.entity.AntiAirTurretRenderer;
import com.virus5600.DefensiveMeasures.entity.client.renderer.entity.BallistaArrowRenderer;
import com.virus5600.DefensiveMeasures.entity.client.renderer.entity.BallistaTurretRenderer;
import com.virus5600.DefensiveMeasures.entity.client.renderer.entity.CannonTurretRenderer;
import com.virus5600.DefensiveMeasures.entity.client.renderer.entity.CannonballRenderer;
import com.virus5600.DefensiveMeasures.entity.client.renderer.entity.MGBulletRenderer;
import com.virus5600.DefensiveMeasures.entity.client.renderer.entity.MGTurretRenderer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ModEntityRenderers {
	public static void registerModEntityRenderers() {
		DefensiveMeasures.LOGGER.debug("REGISTERING ENTITIES FOR " + DefensiveMeasures.MOD_NAME);

		/// TURRETS
		// v1.0.0
		EntityRendererRegistry.register(ModEntities.CANNON_TURRET, CannonTurretRenderer::new);
		EntityRendererRegistry.register(ModEntities.BALLISTA, BallistaTurretRenderer::new);
		EntityRendererRegistry.register(ModEntities.MG_TURRET, MGTurretRenderer::new);

		// v1.1.0-beta
		EntityRendererRegistry.register(ModEntities.ANTI_AIR_TURRET, AntiAirTurretRenderer::new);

		/// PROJECTILES
		// v1.0.0
		EntityRendererRegistry.register(ModEntities.CANNONBALL, CannonballRenderer::new);
		EntityRendererRegistry.register(ModEntities.BALLISTA_ARROW, BallistaArrowRenderer::new);
		EntityRendererRegistry.register(ModEntities.MG_BULLET, MGBulletRenderer::new);

		// v1.1.0-beta
		EntityRendererRegistry.register(ModEntities.ANTI_AIR_PROJECTILE, AntiAirProjectileRenderer::new);
	}
}
