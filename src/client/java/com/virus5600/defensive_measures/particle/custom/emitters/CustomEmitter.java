package com.virus5600.defensive_measures.particle.custom.emitters;

import net.minecraft.client.particle.NoRenderParticle;
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
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class CustomEmitter extends NoRenderParticle {
	// PRIVATE FIELDS
	/**
	 * The particle that will be emitted by the emitter.
	 */
	private final ParticleEffect particle;
	/**
	 * The entity that is the source of the particle.
	 */
	private final Entity entity;
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

	// PROTECTED FIELDS
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

	/// CONSTRUCTORS - VEC3D ///

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
	}

	/// CONSTRUCTORS - ENTITY ///

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
		this.posSource = entity.getPos();
		this.entity = entity;
	}

	/// METHODS ///

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
			this.posSource = this.useEyePos ? this.entity.getEyePos() : this.entity.getPos();
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
					this.world.addParticle(this.particle, xPos, yPos, zPos, xVel, yVel + 0.2, zVel);
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
}
