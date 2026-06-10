package com.virus5600.defensive_measures.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import com.virus5600.defensive_measures.entity.turrets.tier2.FlameTurretEntity;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * The small flame emitted at the mouth of the {@link FlameTurretEntity Flame Turret}'s barrel.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public class LighterFlameParticle extends SingleQuadParticle {
	// CONSTRUCTORS //
	public LighterFlameParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
		super(level, x, y, z, spriteSet.first());

		this.x = x;
		this.y = y;
		this.z = z;

		this.gravity = 0f;
		this.hasPhysics = true;

		this.friction = 0f;
		this.setParticleSpeed(0, 0, 0);

		this.quadSize = 0.1f;
		this.lifetime = 1;

		this.setSpriteFromAge(spriteSet);
	}

	// PUBLIC
	@Override @NotNull
	public Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	@Override
	public void move(double dx, double dy, double dz) {
		this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
		this.setLocationFromBoundingbox();
	}

	@Override
	public float getQuadSize(float tickProgress) {
		float f = ((float)this.age + tickProgress) / (float)this.lifetime;
		return this.quadSize * (1.0F - f * f * 0.5F);
	}

	@Override
	public int getLightColor(float tint) {
		float f = ((float)this.age + tint) / (float)this.lifetime;
		f = Mth.clamp(f, 0.0F, 1.0F);
		int i = super.getLightColor(tint);
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
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(@NonNull SimpleParticleType type, @NonNull ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull RandomSource random) {
			return new LighterFlameParticle(level, x, y, z, this.spriteProvider);
		}
	}
}
