package com.virus5600.defensive_measures.renderer;

import com.virus5600.defensive_measures.renderer.entity.tier0.PelletTurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.tier1.BallistaTurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.tier1.CannonTurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.tier1.MGTurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.tier2.AATurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.tier2.FlameTurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.tier2.MissileTurretRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.renderer.projectiles.*;

/**
 * Registers entity renderers for all entities in the mod.
 * <br><br>
 * This is used so that the client knows how to render the
 * entities on the screen; and since this is client side,
 * texture packs can be used to modify how the entities look.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModEntityRenderers {
	public static void registerEntityRenderers() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY RENDERERS FOR {}...", DefensiveMeasures.MOD_NAME);

		// /////// //
		// TURRETS //
		// /////// //

		// v1.0.0-beta
		EntityRenderers.register(ModEntities.CANNON_TURRET, CannonTurretRenderer::new);
		EntityRenderers.register(ModEntities.BALLISTA_TURRET, BallistaTurretRenderer::new);
		EntityRenderers.register(ModEntities.MG_TURRET, MGTurretRenderer::new);

		// v1.1.0-beta
		EntityRenderers.register(ModEntities.AA_TURRET, AATurretRenderer::new);
		EntityRenderers.register(ModEntities.FLAME_TURRET, FlameTurretRenderer::new);
		EntityRenderers.register(ModEntities.MISSILE_TURRET, MissileTurretRenderer::new);

		// v1.2.0-beta
		EntityRenderers.register(ModEntities.PELLET_TURRET, PelletTurretRenderer::new);

		// /////////// //
		// PROJECTILES //
		// /////////// //

		// v1.0.0-beta
		EntityRenderers.register(ModEntities.CANNONBALL, CannonballRenderer::new);
		EntityRenderers.register(ModEntities.BALLISTA_BOLT, BallistaBoltRenderer::new);
		EntityRenderers.register(ModEntities.MG_BULLET, MGBulletRenderer::new);

		// v1.1.0-beta
		EntityRenderers.register(ModEntities.FLAK_PROJECTILE, FlakProjectileRenderer::new);
		EntityRenderers.register(ModEntities.FLAMMABLE_AEROSOL, FlammableAerosolRenderer::new);
		EntityRenderers.register(ModEntities.MICRO_MISSILE, MicroMissileRenderer::new);

		// v1.2.0
		EntityRenderers.register(ModEntities.FLINT_PELLET, FlintPelletRenderer::new);
	}
}
