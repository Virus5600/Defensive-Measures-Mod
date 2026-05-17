package com.virus5600.defensive_measures.particle.custom.emitters;

import com.virus5600.defensive_measures.entity.projectiles.FlakProjectileEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Defines a new emitter that's tailored fit for emitter particles of this mod.
 * This class is a subclass of {@code NoRenderParticle} and is used to create custom
 * emitters, which doesn't need to be rendered, but instead emits other particles.
 * <br><br>
 * This emitter allows the particle to be emitted from a static position or an entity,
 * allowing a flexible way to create emitter particles.
 *
 * @see net.minecraft.client.particle.NoRenderParticle NoRenderParticle
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class CustomEmitter extends NoRenderParticle {
	/**
	 * The particle that will be emitted by the emitter.
	 */
	private final ParticleEffect particle;
	/**
	 * The entity that is the source of the particle.
	 */
	private final Entity entity;
	/**
	 * Defines the particle manager used for this emitter.
	 */
	protected final ParticleManager particleManager;

	/**
	 * The vector position of the source of the particle. This could be a
	 * static position or the position of the source entity.
	 */
	private Vec3d posSource;
	/**
	 * Determines whether the source of the particle is an entity. This
	 * identifies whether this instance used the entity constructor or
	 * the Vec3d constructor.
	 */
	private boolean isEntitySource = true;
	/**
	 * Determines whether to update the {@link #posSource} field.
	 * When set to {@code true}, the {@link #posSource} field will be updated
	 * depending on the source of the particle.
	 */
	protected boolean updatePosSource = true;
	/**
	 * Determines whether to use the eye position of the entity as the source
	 * of the particle. When set to {@code true}, the {@link #posSource} field
	 * will be set to the eye position of the entity.
	 */
	protected boolean useEyePos = false;
	/**
	 * Determines whether to force render the particle or not. When set to {@code true}, the particle
	 * will be rendered even when it's far away from the player and will not respect the players'
	 * particle settings. This is useful for particles that are important to be seen by the player,
	 * such as the explosion particles of the {@link FlakProjectileEntity}, which are important for
	 * the player to see when the projectile explodes.
	 * <br><br>
	 * By default, this is set to {@code false}.
	 */
	protected boolean forceShow = false;
	/**
	 * Defines a custom emitter code that will be executed.
	 * Useful for overriding the {@link #tick()} method's emitter
	 * code without having to override the entire method.
	 * <br><br>
	 * An example of a custom emitter code is shown below:
	 * <pre><code>
	 *     this.customEmitterCode = () -> {
	 *     	// Custom emitter code here...
	 *     };
	 * </code></pre>
	 *
	 * @see #tick()
	 */
	protected Function<ParticleEffect, Void> customEmitterCode;

	// CONSTRUCTORS - VEC3D //

	/**
	 * Creates a new custom emitter particle with default values.
	 * The particle will last for 8 ticks and will not move.
	 * This constructor is used when the source of the particle is a static position.
	 *
	 * @param world The world the particle is in.
	 * @param particle The particle that will be added.
	 * @param x The x-coordinate of the particle.
	 * @param y The y-coordinate of the particle.
	 * @param z The z-coordinate of the particle.
	 */
	CustomEmitter(ClientWorld world, @NotNull ParticleEffect particle, double x, double y, double z) {
		this(world, particle, x, y, z, 8);
	}

	/**
	 * Creates a new custom emitter particle with a specified maximum age,
	 * allowing it to last longer or shorter than the default 8 ticks.
	 * This constructor is used when the source of the particle is a static position.
	 *
	 * @param world The world the particle is in.
	 * @param particle The particle that will be added.
	 * @param x The x-coordinate of the particle.
	 * @param y The y-coordinate of the particle.
	 * @param z The z-coordinate of the particle.
	 * @param maxAge The maximum age of the particle.
	 */
	CustomEmitter(ClientWorld world, @NotNull ParticleEffect particle, double x, double y, double z, int maxAge) {
		this(world, particle, x, y, z, maxAge, 0, 0, 0);
	}

	/**
	 * Creates a new custom emitter particle with a specified maximum age
	 * and velocity. This allows the particle to move in a specific direction
	 * and last longer or shorter than the default 8 ticks.
	 * This constructor is used when the source of the particle is a static position.
	 *
	 * @param world The world the particle is in.
	 * @param particle The particle that will be added.
	 * @param x The x-coordinate of the particle.
	 * @param y The y-coordinate of the particle.
	 * @param z The z-coordinate of the particle.
	 * @param maxAge The maximum age of the particle.
	 * @param vx The x-velocity of the particle.
	 * @param vy The y-velocity of the particle.
	 * @param vz The z-velocity of the particle.
	 */
	CustomEmitter(ClientWorld world, @NotNull ParticleEffect particle, double x, double y, double z, int maxAge, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);

		this.maxAge = maxAge;
		this.particle = particle;
		this.entity = null;
		this.posSource = new Vec3d(x, y, z);
		this.isEntitySource = false;

		this.particleManager = MinecraftClient.getInstance().particleManager;
	}

	// CONSTRUCTORS - ENTITY //

	/**
	 * Creates a new custom emitter particle with default values.
	 * The particle will last for 8 ticks and will not move.
	 * This constructor is used when the source of the particle is an entity.
	 *
	 * @param world The world the particle is in.
	 * @param entity The entity that will be the source of the particle.
	 * @param particle The particle that will be added.
	 */
	CustomEmitter(ClientWorld world, @NotNull Entity entity, @NotNull ParticleEffect particle) {
		this(world, entity, particle, 8);
	}

	/**
	 * Creates a new custom emitter particle with a specified maximum age,
	 * allowing it to last longer or shorter than the default 8 ticks.
	 * This constructor is used when the source of the particle is an entity.
	 *
	 * @param world The world the particle is in.
	 * @param entity The entity that will be the source of the particle.
	 * @param particle The particle that will be added.
	 * @param maxAge The maximum age of the particle.
	 */
	CustomEmitter(ClientWorld world, @NotNull Entity entity, @NotNull ParticleEffect particle, int maxAge) {
		this(world, entity, particle, maxAge, 0, 0, 0);
	}

	/**
	 * Creates a new custom emitter particle with a specified maximum age
	 * and velocity. This allows the particle to move in a specific direction
	 * and last longer or shorter than the default 8 ticks.
	 * This constructor is used when the source of the particle is an entity.
	 *
	 * @param world The world the particle is in.
	 * @param entity The entity that will be the source of the particle.
	 * @param particle The particle that will be added.
	 * @param maxAge The maximum age of the particle.
	 * @param vx The x-velocity of the particle.
	 * @param vy The y-velocity of the particle.
	 * @param vz The z-velocity of the particle.
	 */
	CustomEmitter(ClientWorld world, @NotNull Entity entity, @NotNull ParticleEffect particle, int maxAge, double vx, double vy, double vz) {
		super(world, entity.getX(), entity.getY(), entity.getZ(), vx, vy, vz);

		this.maxAge = maxAge;
		this.particle = particle;
		this.posSource = entity.getTrackedPosition().getPos();
		this.entity = entity;

		this.particleManager = MinecraftClient.getInstance().particleManager;
	}

	// METHODS //

	/**
	 * {@inheritDoc}
	 * <br><br>
	 * In this overridden method, the age of the particle is incremented by 1.
	 * That's the only thing that happens in this method, allowing the emitter
	 * to automatically die after a certain amount of time when {@code super.tick()}
	 * is called.
	 * <br><br>
	 * This method comes with a default emitter code that moves the particle in a
	 * specific direction and applies gravity to it. This code can be overridden
	 * by setting a custom emitter code providing a lambda expression in the {@link #customEmitterCode}
	 * field.
	 *
	 * @see NoRenderParticle#tick() NoRenderParticle.tick()
	 * @see #customEmitterCode
	 */
	@Override
	public void tick() {
		if (this.updatePosSource && this.isEntitySource && this.entity != null) {
			this.posSource = this.useEyePos ?
				this.entity.getEyePos() :
				this.entity.getTrackedPosition().getPos();
		}

		// Custom emitter code
		if (this.customEmitterCode != null) {
			this.customEmitterCode.apply(this.particle);
		}
		// Default emitter code
		else {
			for (int i = 0; i < 16; i++) {
				double xVel = this.random.nextFloat() * 2.0F - 1.0F;
				double yVel = this.random.nextFloat() * 2.0F - 1.0F;
				double zVel = this.random.nextFloat() * 2.0F - 1.0F;
				if (!(xVel * xVel + yVel * yVel + zVel * zVel > 1.0) && this.entity != null) {
					double xPos = this.entity.getBodyX(xVel / 4.0);
					double yPos = this.entity.getBodyY(0.5 + yVel / 4.0);
					double zPos = this.entity.getBodyZ(zVel / 4.0);
					this.world.addParticleClient(
						this.particle,
						this.forceShow, this.forceShow,
						xPos, yPos, zPos,
						xVel, yVel + 0.2, zVel
					);
				}
			}
		}

		this.age++;
		if (this.age > this.maxAge) {
			this.markDead();
		}
	}

	/**
	 * Gets the Vec3d position of the source of the particle.
	 * @return The position of the source of the particle.
	 */
	protected Vec3d getPosSource() {
		return this.posSource;
	}

	// PROTECTED
	protected void explodeBall(ParticleEffect particle, double size, int amount, double variance) {
		size += this.random.nextGaussian() * variance;

		for (int i = -amount; i <= amount; ++i) {
			for (int j = -amount; j <= amount; ++j) {
				for (int k = -amount; k <= amount; ++k) {
					double g = (double) j + (this.random.nextDouble() - this.random.nextDouble()) * (double) 0.5F;
					double h = (double) i + (this.random.nextDouble() - this.random.nextDouble()) * (double) 0.5F;
					double l = (double) k + (this.random.nextDouble() - this.random.nextDouble()) * (double) 0.5F;
					double m = Math.sqrt(g * g + h * h + l * l) / size + this.random.nextGaussian() * 0.05;

//					this.particleManager.addParticle(
//						particle,
//						this.x, this.y, this.z,
//						g / m, h / m, l / m
//					);

					this.world.addParticleClient(
						particle,
						this.forceShow, this.forceShow,
						this.x, this.y, this.z,
						g / m, h / m, l / m
					);

					if (i != -amount && i != amount && j != -amount && j != amount) {
						k += amount * 2 - 1;
					}
				}
			}
		}
	}
}
