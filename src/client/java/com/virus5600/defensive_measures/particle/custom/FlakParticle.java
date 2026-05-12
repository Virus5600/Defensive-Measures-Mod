package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

/**
 * Defines the particles emitted by the {@code Flak Projectile} when it explodes.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class FlakParticle extends BillboardParticle {
	// CONSTRUCTORS //
	public FlakParticle(ClientWorld level, double x, double y, double z, SpriteProvider spriteSet, double xd, double yd, double zd) {
		super(level, x, y, z, xd, yd, zd, spriteSet.getFirst());

		this.x = x;
		this.y = y;
		this.z = z;

		this.gravityStrength = 0.25f;
		this.collidesWithWorld = true;

		this.velocityMultiplier = 0.5f;
		this.setVelocity(xd, yd, zd);

		float black = this.random.nextFloat() * 0.5F;

		this.red = black;
		this.green = black;
		this.blue = black;

		this.scale = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 5.0F + 2.5F);
		this.maxAge = (int)((double)16.0F / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;

		this.updateSprite(spriteSet);
	}

	// PUBLIC
	@Override
	public RenderType getRenderType() {
		return RenderType.PARTICLE_ATLAS_OPAQUE;
	}

	// FACTORIES //

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd, Random random) {
			return new FlakParticle(level, x, y, z, this.spriteProvider, xd, yd, zd);
		}
	}
}
