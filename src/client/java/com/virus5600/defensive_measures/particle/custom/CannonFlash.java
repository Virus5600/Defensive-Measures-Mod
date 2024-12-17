package com.virus5600.defensive_measures.particle.custom;

import com.virus5600.defensive_measures.particle.ModParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Defines the particles emitted by the {@link com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity Cannon Turret}
 * when it fires.
 * The particle shots out in a cone shape and fades out over time. The direction is
 * determined by the direction the turret is facing.
 */
@Environment(EnvType.CLIENT)
public class CannonFlash extends SpriteBillboardParticle {
	private final Vec3d source;

	/// CONSTRUCTORS ///
	public CannonFlash(ClientWorld level, double x, double y, double z, SpriteProvider spriteSet, double xd, double yd, double zd) {
		super(level, x, y, z, xd, yd, zd);

		this.velocityMultiplier = 1f;
		this.x = x;
		this.y = y;
		this.z = z;
		this.scale *= 0.5f;
		this.maxAge = 20;
		this.gravityStrength = 1f;
		this.collidesWithWorld = true;
		this.setSpriteForAge(spriteSet);
		this.setVelocity(xd, yd, zd);
		this.source = new Vec3d(x, y, z);

		this.red = 1f;
		this.green = 1f;
		this.blue = 1f;

		// TODO: Create a separete emitter for the cannon flash, distinguishing the actual flash from the sparks.
		final int maxParticles = MathHelper.nextInt(this.random, 15, 25);
		for (int i = 10; i <= maxParticles; i++) {
			level.addParticle(
				ModParticles.CANNON_FLASH,
				x, y, z,
				MathHelper.nextDouble(this.random, -0.1, 0.1) + xd,
				MathHelper.nextDouble(this.random, -0.1, 0.1) + yd,
				MathHelper.nextDouble(this.random, -0.1, 0.1) + zd
			);
		}
	}

	/// METHODS ///
	// PRIVATE
	private void fadeOut() {
		// Turns the particle black (Simulates tar color)
		if (this.age < 20 && this.red > 0.1 && this.green > 0.1 && this.blue > 0.1) {
			this.red -= 0.05f;
			this.green -= 0.05f;
			this.blue -= 0.05f;
		}

		// Fades the particle out and make it small overtime
		if (this.red <= 0.1 && this.green <= 0.1 && this.blue <= 0.1 && this.alpha > 0.05) {
			this.alpha -= 0.05F;

			if (this.scale > 0)
				this.scale = Math.min(this.scale - 0.5f, 0);
		}
	}

	// PUBLIC
	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
    public int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        int k = i >> 16 & 0xFF;

        return 0xF0 | k << 16;
    }

	@Override
    public float getSize(float tickDelta) {
        float f = ((float)this.age + tickDelta) / (float)this.maxAge;

        return this.scale * (1.0f - f * f);
    }

	public void setVelocity(double vx, double vy, double vz) {
		super.setVelocity(vx, vy, vz);
	}

	@Override
	public void tick() {
		super.tick();
		this.fadeOut();

		if (this.dead) {
			for (int i = 0; i < MathHelper.nextInt(this.random, 1, 3); i++) {
				this.world.addParticle(
					ParticleTypes.CAMPFIRE_COSY_SMOKE,
					this.source.x,
					this.source.y,
					this.source.z,
					MathHelper.nextDouble(this.random, -0.01, 0.01),
					MathHelper.nextDouble(this.random, 0.01, 0.025),
					MathHelper.nextDouble(this.random, -0.01, 0.01)
				);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider sprites;

		public Factory(SpriteProvider sprites) {
			this.sprites = sprites;
		}

		public Particle createParticle(SimpleParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd) {
			return new CannonFlash(level, x, y, z, this.sprites, xd, yd, zd);
		}
	}
}
