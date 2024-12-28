package com.virus5600.defensive_measures.particle.custom.emitters;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;

import com.virus5600.defensive_measures.particle.ModParticles;

/**
 * Defines the particles emitted by the {@link com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity Cannon Turret}
 * when it fires.
 * The particle shots out in a cone shape and fades out over time. The direction is
 * determined by the direction the turret is facing.
 */
@Environment(EnvType.CLIENT)
public class CannonFlash extends CustomEmitter {
	/// CONSTRUCTORS ///
	public CannonFlash(ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, ModParticles.SPARKS, x, y, z, 9);
		this.velocityX = vx;
		this.velocityY = vy;
		this.velocityZ = vz;
		this.setCustomEmitterCode();
	}

	public CannonFlash(ClientWorld world, Entity entity, double vx, double vy, double vz) {
		super(world, entity, ModParticles.SPARKS, 9);
		this.velocityX = vx;
		this.velocityY = vy;
		this.velocityZ = vz;
		this.setCustomEmitterCode();
	}

	// CUSTOM EMITTER CODE //
	private void setCustomEmitterCode() {
		this.customEmitterCode = (particle) -> {
			// Flash
			if (this.age < 3) {
				double variance = 0.75;
				double minX = this.velocityX - variance,
					maxX = this.velocityX + variance,
					minY = this.velocityY - variance,
					maxY = this.velocityY + variance,
					minZ = this.velocityZ - variance,
					maxZ = this.velocityZ + variance;

				for (int i = 0; i < MathHelper.nextInt(this.random, 10, 25); i++) {
					this.world.addParticle(
						particle,
						this.getPosSource().x,
						this.getPosSource().y,
						this.getPosSource().z,
						MathHelper.nextDouble(this.random, minX, maxX),
						MathHelper.nextDouble(this.random, minY, maxY),
						MathHelper.nextDouble(this.random, minZ, maxZ)
					);
				}
			}

			// Smoke
			if (this.age >= this.maxAge) {
				for (int i = 0; i < MathHelper.nextInt(this.random, 1, 3); i++) {
					this.world.addParticle(
						ParticleTypes.CAMPFIRE_COSY_SMOKE,
						this.getPosSource().x,
						this.getPosSource().y,
						this.getPosSource().z,
						MathHelper.nextDouble(this.random, -0.01, 0.01),
						MathHelper.nextDouble(this.random, 0.01, 0.025),
						MathHelper.nextDouble(this.random, -0.01, 0.01)
					);
				}
			}

			return null;
		};
	}

	///  FACTORY ///
	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
			return new CannonFlash(world, x, y, z, vx, vy, vz);
		}
	}
}
