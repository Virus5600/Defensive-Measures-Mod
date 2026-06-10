package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures.entity.turrets.tier1.CannonTurretEntity;
import com.virus5600.defensive_measures.particle.ModParticles;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Defines the particles emitted by the {@link CannonTurretEntity Cannon Turret}
 * when charging up to fire.
 * The particle is a small flame that fades out over time, emitting from the back
 * of the cannon.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public class CannonFuseParticle extends SingleQuadParticle {
	private final Vec3 source;

	// CONSTRUCTORS //
	protected CannonFuseParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd) {
		super(level, x, y, z, xd, yd, zd, spriteSet.first());

		this.friction = 0.75f;
		this.x = x;
		this.y = y;
		this.z = z;
		this.quadSize *= 0.5f;
		this.lifetime = 2;
		this.gravity = 1f;
		this.hasPhysics = true;
		this.source = new Vec3(x, y, z);

		this.rCol = 1f;
		this.gCol = 1f;
		this.bCol = 1f;
	}

	// /////// //
	// METHODS //
	// /////// //

	// PRIVATE
	private void fadeOut() {
		float maxParticleAge = 10;

		// Turns the particle black (Simulates tar color)
		if (this.age < maxParticleAge && this.rCol > 0.1 && this.gCol > 0.1 && this.bCol > 0.1) {
			this.rCol -= 0.05f;
			this.gCol -= 0.05f;
			this.bCol -= 0.05f;
		}

		// Fades the particle out
		if (this.rCol <= 0.1 && this.gCol <= 0.1 && this.bCol <= 0.1 && this.alpha > 0.05) {
			this.alpha -= 0.05F;
		}
	}

	// PUBLIC
	@Override @NonNull
	public Layer getLayer() {
		return Layer.OPAQUE;
	}

	@Override
    public int getLightColor(float tint) {
        int i = super.getLightColor(tint);
        int k = i >> 16 & 0xFF;

        return 0xF0 | k << 16;
    }

	@Override
    public float getQuadSize(float tickDelta) {
        float f = ((float)this.age + tickDelta) / (float)this.lifetime;

        return this.quadSize * (1.0f - f * f);
    }

	@Override
	public void tick() {
		super.tick();
		this.fadeOut();

		if (this.age < this.lifetime) {
            if (Mth.nextInt(this.random, 0, 100) > 90) {
                this.level.addParticle(
					ParticleTypes.SMOKE,
					this.source.x,
					this.source.y,
					this.source.z,
					Mth.nextDouble(this.random, -0.01, 0.01),
					Mth.clamp(Math.abs(this.yd), 0.0625, 0.125),
					Mth.nextDouble(this.random, -0.01, 0.01)
				);
            }

			if (Mth.nextInt(this.random, 0, 100) > 25) {
				this.level.addParticle(
					ModParticles.SPARKS,
					this.source.x,
					this.source.y,
					this.source.z,
					Mth.nextDouble(this.random, -0.01, 0.01),
					Mth.clamp(Math.abs(this.yd), 0.0625, 0.125),
					Mth.nextDouble(this.random, -0.01, 0.01)
				);
			}
        }
	}

	// FACTORIES //

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Factory(SpriteSet sprites) {
			this.sprites = sprites;
		}

		public @Nullable Particle createParticle(@NonNull SimpleParticleType type, @NonNull ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull RandomSource random) {
			return new CannonFuseParticle(level, x, y, z, this.sprites, xd, yd, zd);
		}
	}
}
