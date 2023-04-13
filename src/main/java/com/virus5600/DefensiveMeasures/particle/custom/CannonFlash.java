package com.virus5600.DefensiveMeasures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CannonFlash extends SpriteBillboardParticle {
	private Vec3d source;

	/// CONSTRUCTORS ///
	protected CannonFlash(final ClientWorld level, final double x, final double y, final double z, final SpriteProvider spriteSet, final double xd, final double yd, final double zd) {
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
			this.alpha -= 0.05;

			if (this.scale > 0) this.scale = Math.min(this.scale - 0.5f, 0);
		}
	}

	// PUBLIC
		@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
    public int getBrightness(final float tint) {
        int i = super.getBrightness(tint);
        @SuppressWarnings("unused") int j = 240;
        int k = i >> 16 & 0xFF;

        return 0xF0 | k << 16;
    }

	@Override
    public float getSize(final float tickDelta) {
        float f = ((float) this.age + tickDelta) / (float) this.maxAge;

        return this.scale * (1.0f - f * f);
    }

	public void setVelocity(final double vx, final double vy, final double vz) {
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
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider sprites;

		public Factory(final SpriteProvider sprites) {
			this.sprites = sprites;
		}

		public Particle createParticle(final DefaultParticleType type, final ClientWorld level, final double x, final double y, final double z, final double xd, final double yd, final double zd) {
			return new CannonFlash(level, x, y, z, this.sprites, xd, yd, zd);
		}

	}
}
