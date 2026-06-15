package com.virus5600.defensive_measures.particle.custom.emitters;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import com.virus5600.defensive_measures.entity.turrets.tier1.CannonTurretEntity;
import com.virus5600.defensive_measures.particle.ModParticles;
import org.jspecify.annotations.NonNull;

/**
 * Defines the particles emitted by the {@link CannonTurretEntity Cannon Turret}
 * when it fires.
 * The particle shots out in a cone shape and fades out over time. The direction is
 * determined by the direction the turret is facing.
 *
 * @see CustomEmitter
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public class CannonFlashEmitter extends CustomEmitter {
	protected double flashDuration = 3;
	protected double variance = 0.75;

	// CONSTRUCTORS //

	public CannonFlashEmitter(ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, ModParticles.SPARKS, x, y, z, 9);
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		this.setCustomEmitterCode();
	}

	// CUSTOM EMITTER CODE //
	private void setCustomEmitterCode() {
		this.customEmitterCode = (particle) -> {
			// Flash
			if (this.age < this.flashDuration) {
				double variance = this.variance;
				double minX = this.xd - variance,
					maxX = this.xd + variance,
					minY = this.yd - variance,
					maxY = this.yd + variance,
					minZ = this.zd - variance,
					maxZ = this.zd + variance;

				for (int i = 0; i < Mth.nextInt(this.random, 10, 25); i++) {
					this.level.addParticle(
						particle,
						this.getPosSource().x,
						this.getPosSource().y,
						this.getPosSource().z,
						Mth.nextDouble(this.random, minX, maxX),
						Mth.nextDouble(this.random, minY, maxY),
						Mth.nextDouble(this.random, minZ, maxZ)
					);
				}
			}

			// Smoke
			if (this.age >= this.lifetime) {
				for (int i = 0; i < Mth.nextInt(this.random, 1, 3); i++) {
					this.level.addParticle(
						ParticleTypes.CAMPFIRE_COSY_SMOKE,
						this.getPosSource().x,
						this.getPosSource().y,
						this.getPosSource().z,
						Mth.nextDouble(this.random, -0.01, 0.01),
						Mth.nextDouble(this.random, 0.01, 0.025),
						Mth.nextDouble(this.random, -0.01, 0.01)
					);
				}
			}

			return null;
		};
	}

	//  FACTORY //
	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		public Particle createParticle(@NonNull SimpleParticleType simpleParticleType, @NonNull ClientLevel world, double x, double y, double z, double vx, double vy, double vz, @NonNull RandomSource random) {
			return new CannonFlashEmitter(world, x, y, z, vx, vy, vz);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ThrusterFactory implements ParticleProvider<SimpleParticleType> {
		public Particle createParticle(@NonNull SimpleParticleType simpleParticleType, @NonNull ClientLevel world, double x, double y, double z, double vx, double vy, double vz, @NonNull RandomSource random) {
			CannonFlashEmitter particle = new CannonFlashEmitter(world, x, y, z, vx, vy, vz);

			particle.setLifetime(1);
			particle.flashDuration = 1;
			particle.variance = 0.125;

			return particle;
		}
	}
}
