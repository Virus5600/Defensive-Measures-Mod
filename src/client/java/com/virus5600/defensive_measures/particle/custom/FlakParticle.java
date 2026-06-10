package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * Defines the particles emitted by the {@code Flak Projectile} when it explodes.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public class FlakParticle extends SingleQuadParticle {
	// CONSTRUCTORS //
	public FlakParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd) {
		super(level, x, y, z, xd, yd, zd, spriteSet.first());

		this.x = x;
		this.y = y;
		this.z = z;

		this.gravity = 0.25f;
		this.hasPhysics = true;

		this.friction = 0.5f;
		this.setParticleSpeed(xd, yd, zd);

		float black = this.random.nextFloat() * 0.5F;

		this.rCol = black;
		this.gCol = black;
		this.bCol = black;

		this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 5.0F + 2.5F);
		this.lifetime = (int)((double)16.0F / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;

		this.setSpriteFromAge(spriteSet);
	}

	// PUBLIC
	@Override @NotNull
	public Layer getLayer() {
		return Layer.OPAQUE;
	}

	// FACTORIES //

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(@NonNull SimpleParticleType type, @NonNull ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull RandomSource random) {
			return new FlakParticle(level, x, y, z, this.spriteProvider, xd, yd, zd);
		}
	}
}
