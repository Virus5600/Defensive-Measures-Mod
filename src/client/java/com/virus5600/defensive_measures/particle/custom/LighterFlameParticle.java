package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class LighterFlameParticle extends BillboardParticle {
	// CONSTRUCTORS //
	public LighterFlameParticle(ClientWorld level, double x, double y, double z, SpriteProvider spriteSet) {
		super(level, x, y, z, spriteSet.getFirst());

		this.x = x;
		this.y = y;
		this.z = z;

		this.gravityStrength = 0f;
		this.collidesWithWorld = true;

		this.velocityMultiplier = 0f;
		this.setVelocity(0, 0, 0);

		this.scale = 0.1f;
		this.maxAge = 1;

		this.updateSprite(spriteSet);
	}

	// PUBLIC
	@Override
	public RenderType getRenderType() {
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	@Override
	public void move(double dx, double dy, double dz) {
		this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
		this.repositionFromBoundingBox();
	}

	@Override
	public float getSize(float tickProgress) {
		float f = ((float)this.age + tickProgress) / (float)this.maxAge;
		return this.scale * (1.0F - f * f * 0.5F);
	}

	@Override
	public int getBrightness(float tint) {
		float f = ((float)this.age + tint) / (float)this.maxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getBrightness(tint);
		int j = i & 255;
		int k = i >> 16 & 255;
		j += (int)(f * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	// FACTORIES //

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd, Random random) {
			return new LighterFlameParticle(level, x, y, z, this.spriteProvider);
		}
	}
}
