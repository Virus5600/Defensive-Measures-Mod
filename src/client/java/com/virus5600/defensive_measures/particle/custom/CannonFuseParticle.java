package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.virus5600.defensive_measures.particle.ModParticles;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

/**
 * Defines the particles emitted by the {@link com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity Cannon Turret}
 * when charging up to fire.
 * The particle is a small flame that fades out over time, emitting from the back
 * of the cannon.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public class CannonFuseParticle extends BillboardParticle {
	private final Vec3d source;
	private final float maxParticleAge = 25;

	// CONSTRUCTORS //
	protected CannonFuseParticle(ClientWorld level, double x, double y, double z, SpriteProvider spriteSet, double xd, double yd, double zd) {
		super(level, x, y, z, xd, yd, zd, spriteSet.getFirst());

		this.velocityMultiplier = 0.75f;
		this.x = x;
		this.y = y;
		this.z = z;
		this.scale *= 0.5f;
		this.maxAge = 2;
		this.gravityStrength = 1f;
		this.collidesWithWorld = true;
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
	public RenderType getRenderType() {
		return RenderType.PARTICLE_ATLAS_OPAQUE;
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

	@Override
	public void tick() {
		super.tick();
		this.fadeOut();

		if (this.age < this.maxAge) {
            if (MathHelper.nextInt(this.random, 0, 100) > 90) {
                this.world.addParticleClient(
					ParticleTypes.SMOKE,
					this.source.x,
					this.source.y,
					this.source.z,
					MathHelper.nextDouble(this.random, -0.01, 0.01),
					MathHelper.clamp(Math.abs(this.velocityY), 0.0625, 0.125),
					MathHelper.nextDouble(this.random, -0.01, 0.01)
				);
            }

			if (MathHelper.nextInt(this.random, 0, 100) > 25) {
				this.world.addParticleClient(
					ModParticles.SPARKS,
					this.source.x,
					this.source.y,
					this.source.z,
					MathHelper.nextDouble(this.random, -0.01, 0.01),
					MathHelper.clamp(Math.abs(this.velocityY), 0.0625, 0.125),
					MathHelper.nextDouble(this.random, -0.01, 0.01)
				);
			}
        }
	}

	// FACTORIES //

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider sprites;

		public Factory(SpriteProvider sprites) {
			this.sprites = sprites;
		}

		public @Nullable Particle createParticle(SimpleParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd, Random random) {
			return new CannonFuseParticle(level, x, y, z, this.sprites, xd, yd, zd);
		}
	}
}
