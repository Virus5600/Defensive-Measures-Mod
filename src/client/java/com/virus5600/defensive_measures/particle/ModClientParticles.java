package com.virus5600.defensive_measures.particle;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.virus5600.defensive_measures.particle.custom.*;
import com.virus5600.defensive_measures.particle.custom.emitters.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

/**
 * Client-side particle registration.
 * <br><br>
 * All custom particles are registered here, even if they are not used by the client.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public class ModClientParticles {
	public static void registerParticles() {
		DefensiveMeasures.LOGGER.info("REGISTERING PARTICLES FOR {}...", DefensiveMeasures.MOD_NAME);

		ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();

		// PARTICLES
		registry.register(ModParticles.CANNON_FUSE, CannonFuseParticle.Factory::new);
		registry.register(ModParticles.SPARKS, SparksParticle.Factory::new);
		registry.register(ModParticles.SUSPENDED_SPARKS, SparksParticle.SuspendedFactory::new);
		registry.register(ModParticles.FLAK, FlakParticle.Factory::new);
		registry.register(ModParticles.FLAK_CLOUD, FlakCloudParticle.Factory::new);
		registry.register(ModParticles.LIGHTER_FLAME, LighterFlameParticle.Factory::new);

		// EMITTERS
		registry.register(ModParticles.CANNON_FLASH, new CannonFlashEmitter.Factory());
		registry.register(ModParticles.FLAK_EXPLOSION, new FlakExplosionEmitter.Factory());
	}
}
