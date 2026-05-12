package com.virus5600.defensive_measures.particle.custom.emitters;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.particle.custom.FlakCloudParticle;
import com.virus5600.defensive_measures.particle.custom.FlakParticle;

/**
 * Defines the particles emitted by a larger caliber of {@link com.virus5600.defensive_measures.entity.projectiles.FlakProjectileEntity Flak Projectile}
 * when it explodes. The particles emitted by this emitter contains two types of particles: The {@link FlakParticle}
 * and the {@link FlakCloudParticle}, with the latter serving as
 * the accumulated flak cloud that lingers a bit longer than the usual flak particles. The flak
 * particles are emitted in a spherical shape, while the smoke particles are emitted in a more random
 * pattern, creating a more natural and chaotic explosion effect.
 *
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 * @see CustomEmitter
 * @since 1.0.0-beta
 */
@Environment(EnvType.CLIENT)
public class FlakExplosionEmitter extends CustomEmitter {
	// CONSTRUCTORS //
	public FlakExplosionEmitter(ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, ModParticles.FLAK, x, y, z, 9);

		this.velocityX = vx;
		this.velocityY = vy;
		this.velocityZ = vz;

		this.forceShow = true;

		this.setCustomEmitterCode();
	}

	// CUSTOM EMITTER CODE //
	private void setCustomEmitterCode() {
		this.customEmitterCode = (particle) -> {
			// Flak
			this.explodeBall(particle, 5, 1, 0.5);

			// Flak Cloud
			this.explodeBall(ModParticles.FLAK_CLOUD, 5, 1, 0.5);

			return null;
		};
	}

	//  FACTORY //
	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld world, double x, double y, double z, double vx, double vy, double vz, Random random) {
			return new FlakExplosionEmitter(world, x, y, z, vx, vy, vz);
		}
	}
}
