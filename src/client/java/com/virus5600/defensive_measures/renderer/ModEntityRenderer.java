package com.virus5600.defensive_measures.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.renderer.entity.*;
import com.virus5600.defensive_measures.renderer.projectiles.*;

/**
 * Registers entity renderers for all entities in the mod.
 * <br><br>
 * This is used so that the client knows how to render the
 * entities on the screen; and since this is client side,
 * texture packs can be used to modify how the entities look.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModEntityRenderer {
	public static void registerEntityRenderers() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY RENDERERS FOR {}...", DefensiveMeasures.MOD_NAME);

		// /////// //
		// TURRETS //
		// /////// //

		// v1.0.0
		EntityRendererRegistry.register(ModEntities.CANNON_TURRET, CannonTurretRenderer::new);
		EntityRendererRegistry.register(ModEntities.BALLISTA_TURRET, BallistaTurretRenderer::new);
		EntityRendererRegistry.register(ModEntities.MG_TURRET, MGTurretRenderer::new);

		// v1.1.0
//		EntityRendererRegistry.register(ModEntities.AA_TURRET, AATurretRenderer::new);

		// /////////// //
		// PROJECTILES //
		// /////////// //

		// v1.0.0
		EntityRendererRegistry.register(ModEntities.CANNONBALL, CannonballRenderer::new);
		EntityRendererRegistry.register(ModEntities.BALLISTA_ARROW, BallistaArrowRenderer::new);
		EntityRendererRegistry.register(ModEntities.MG_BULLET, MGBulletRenderer::new);
	}
}
