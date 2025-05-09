package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

import com.virus5600.defensive_measures.particle.custom.emitters.CannonFlash;

/**
 * Defines the particles emitted by certain turrets that creates sparks when they
 * shoot. This particle is the base particle for some spark-based particles such
 * as the {@link CannonFlash Cannon Flash Emitter}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class Sparks extends SpriteBillboardParticle {
	protected boolean isSuspended = false;

	// CONSTRUCTORS //
	public Sparks(ClientWorld level, double x, double y, double z, SpriteProvider spriteSet, double xd, double yd, double zd) {
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
	public void move(double dx, double dy, double dz) {
		if (!this.isSuspended) {
			super.move(dx, dy, dz);
		}
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
	}

	// FACTORIES //

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider sprites;

		public Factory(SpriteProvider sprites) {
			this.sprites = sprites;
		}

		public Particle createParticle(SimpleParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd) {
			return new Sparks(level, x, y, z, this.sprites, xd, yd, zd);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SuspendedFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider sprites;

		public SuspendedFactory(SpriteProvider sprites) {
			this.sprites = sprites;
		}

		public Particle createParticle(SimpleParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd) {
			Sparks particle = new Sparks(level, x, y, z, this.sprites, xd, yd, zd);
			particle.isSuspended = true;
			particle.gravityStrength = 0.0f;
			return particle;
		}
	}
}
