package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

import com.virus5600.defensive_measures.particle.custom.emitters.CannonFlashEmitter;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * Defines the particles emitted by certain turrets that creates sparks when they
 * shoot. This particle is the base particle for some spark-based particles such
 * as the {@link CannonFlashEmitter Cannon Flash Emitter}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public class SparksParticle extends SingleQuadParticle {
	protected boolean isSuspended = false;

	// CONSTRUCTORS //
	public SparksParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd) {
		super(level, x, y, z, xd, yd, zd, spriteSet.first());

		this.friction = 1f;
		this.x = x;
		this.y = y;
		this.z = z;
		this.quadSize *= 0.5f;
		this.lifetime = 20;
		this.gravity = 1f;
		this.hasPhysics = true;
		this.setParticleSpeed(xd, yd, zd);

		this.rCol = 1f;
		this.gCol = 1f;
		this.bCol = 1f;
	}

	// METHODS //
	// PRIVATE
	private void fadeOut() {
		// Turns the particle black (Simulates tar color)
		if (this.age < 20 && this.rCol > 0.1 && this.gCol > 0.1 && this.bCol > 0.1) {
			this.rCol -= 0.05f;
			this.gCol -= 0.05f;
			this.bCol -= 0.05f;
		}

		// Fades the particle out and make it small overtime
		if (this.rCol <= 0.1 && this.gCol <= 0.1 && this.bCol <= 0.1 && this.alpha > 0.05) {
			this.alpha -= 0.05F;

			if (this.quadSize > 0)
				this.quadSize = Math.min(this.quadSize - 0.5f, 0);
		}
	}

	// PUBLIC
	@Override @NotNull
	public Layer getLayer() {
		return Layer.OPAQUE;
	}

	@Override
	public void move(double dx, double dy, double dz) {
		if (!this.isSuspended) {
			super.move(dx, dy, dz);
		}
	}

	@Override
    public int getLightCoords(float tint) {
        int i = super.getLightCoords(tint);
        int k = i >> 16 & 0xFF;

        return 0xF0 | k << 16;
    }

	@Override
    public float getQuadSize(float tickDelta) {
        float f = ((float)this.age + tickDelta) / (float)this.lifetime;

        return this.quadSize * (1.0f - f * f);
    }

	public void setParticleSpeed(double vx, double vy, double vz) {
		super.setParticleSpeed(vx, vy, vz);
	}

	@Override
	public void tick() {
		super.tick();
		this.fadeOut();
	}

	// FACTORIES //

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Factory(SpriteSet sprites) {
			this.sprites = sprites;
		}

		public Particle createParticle(@NonNull SimpleParticleType type, @NonNull ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull RandomSource random) {
			return new SparksParticle(level, x, y, z, this.sprites, xd, yd, zd);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SuspendedFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public SuspendedFactory(SpriteSet sprites) {
			this.sprites = sprites;
		}

		public Particle createParticle(@NonNull SimpleParticleType type, @NonNull ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull RandomSource random) {
			SparksParticle particle = new SparksParticle(level, x, y, z, this.sprites, xd, yd, zd);
			particle.isSuspended = true;
			particle.gravity = 0.0f;
			return particle;
		}
	}
}
