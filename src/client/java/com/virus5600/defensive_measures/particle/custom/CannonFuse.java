package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Defines the particles emitted by the {@link com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity Cannon Turret}
 * when charging up to fire.
 * The particle is a small flame that fades out over time, emitting from the back
 * of the cannon.
 */
@Environment(EnvType.CLIENT)
public class CannonFuse extends SpriteBillboardParticle {
	private final Vec3d source;

	/// CONSTRUCTORS ///
	protected CannonFuse(ClientWorld level, double x, double y, double z, SpriteProvider spriteSet, double xd, double yd, double zd) {
		super(level, x, y, z, xd, yd, zd);

		this.velocityMultiplier = 0.75f;
		this.x = x;
		this.y = y;
		this.z = z;
		this.scale *= 0.5f;
		this.maxAge = 25;
		this.gravityStrength = 1f;
		this.collidesWithWorld = true;
		this.setSpriteForAge(spriteSet);
		this.source = new Vec3d(x, y, z);

		this.red = 1f;
		this.green= 1f;
		this.blue = 1f;
	}

	/// METHODS ///
	// PRIVATE
	private void fadeOut() {
		// Turns the particle black (Simulates tar color)
		if (this.age < 10 && this.red > 0.1 && this.green > 0.1 && this.blue > 0.1) {
			this.red -= 0.05f;
			this.green -= 0.05f;
			this.blue -= 0.05f;
		}

		// Fades the particle out
		if (this.red <= 0.1 && this.green <= 0.1 && this.blue <= 0.1 && this.alpha > 0.05) {
			this.alpha -= 0.05F;
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
        @SuppressWarnings("unused") int j = 240;
        int k = i >> 16 & 0xFF;

        return 0xF0 | k << 16;
    }

	@Override
    public float getSize(float tickDelta) {
        float f = ((float)this.age + tickDelta) / (float)this.maxAge;

        return this.scale * (1.0f - f * f);
    }

	@Override
	public void tick() {
		super.tick();
		this.fadeOut();

		if (this.age < 10) {
            if (MathHelper.nextInt(this.random, 0, 100) > 90) {
                this.world.addParticle(ParticleTypes.SMOKE, this.source.x, this.source.y, this.source.z, MathHelper.nextDouble(this.random, -0.01, 0.01), MathHelper.clamp(Math.abs(this.velocityY), 0.0625, 0.125), MathHelper.nextDouble(this.random, -0.01, 0.01));
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
			return new CannonFuse(level, x, y, z, this.sprites, xd, yd, zd);
		}

	}
}
