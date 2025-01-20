package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ExplosiveProjectileEntity extends TurretProjectileEntity {
	protected ParticleEffect particleTrail;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	protected ExplosiveProjectileEntity(EntityType<? extends TurretProjectileEntity> entityType, World world) {
		super(entityType, world);
		this.setDamage(5);
		this.setPierceLevel(this.getMaxPierceLevel());
		this.setSpeedAffectsDamage(false);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //
	// PROTECTED
	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);
	}

	protected boolean isBurning() {
		return true;
	}

	@Nullable
	protected ParticleEffect getParticleType() {
		return this.particleTrail;
	}

	@Override
	protected float getDrag() {
		return 0.95F;
	}

	@Override
	protected float getDragInWater() {
		return 0.8F;
	}

	@Override
	protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
		super.onDeflected(deflector, fromAttack);

		if (fromAttack) {
			this.doDamage();
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult hitResult) {
		super.onBlockHit(hitResult);

		if (!this.getWorld().isClient()) {
			this.doDamage();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult hitResult) {
		super.onEntityHit(hitResult);

		if (!this.getWorld().isClient()) {
			this.doDamage();
		}
	}

	// PUBLIC
	private double radius = 0;
	public void doDamage() {
		// Create the damage source
		DamageSource dmgSrc = this.getDamageSources().explosion(
			this,
			this.getOwner() == null ? this : this.getOwner()
		);

		// Create the explosion
		this.getWorld()
			.createExplosion(
				this,
				dmgSrc,
				new ExplosionBehavior(),
				this.getX(),
				this.getBodyY(0.0625),
				this.getZ(),
				1.25F,
				false,
				World.ExplosionSourceType.NONE
			);

		// Damaging entities within a radius.
		double effectiveRadius = this.getEffectiveRadius();
		double maxDmgRadius = this.getMaxDamageRadius();
		double dmgReduction = this.getDamageReduction();
		double baseDmg = this.getDamage();

		// Damaging entities within Effective Radius
		Box fullDmgReceiver = new Box(
			this.getX() - effectiveRadius,
			this.getY() - effectiveRadius,
			this.getZ() - effectiveRadius,
			this.getX() + effectiveRadius,
			this.getY() + effectiveRadius,
			this.getZ() + effectiveRadius
		);

		List<LivingEntity> damagedEntities = new ArrayList<>();
		this.getWorld()
			.getOtherEntities(this, fullDmgReceiver)
			.forEach(entity -> {
				if (entity instanceof LivingEntity) {
					entity.damage(
						(ServerWorld) this.getWorld(),
						dmgSrc,
						(float) baseDmg
					);
					damagedEntities.add((LivingEntity) entity);
				}
			});

		// Damaging entities outside Effective Radius but within Max Damage Radius
		for (radius = effectiveRadius; radius < maxDmgRadius; radius += 0.5) {
			double ext = radius + 1;
			Box partialDmgReceiver = new Box(
				this.getX() - ext,
				this.getY() - ext,
				this.getZ() - ext,
				this.getX() + ext,
				this.getY() + ext,
				this.getZ() + ext
			);

			this.getWorld()
				.getOtherEntities(this, partialDmgReceiver)
				.forEach(entity -> {
					if (entity instanceof LivingEntity && !damagedEntities.contains(entity)) {
						float dmg = (float) (baseDmg - (dmgReduction * (radius - effectiveRadius)));
						entity.damage(
							(ServerWorld) this.getWorld(),
							dmgSrc,
							dmg
						);

						damagedEntities.add((LivingEntity) entity);
					}
				});
		}

		this.discard();
	}

	// PUBLIC

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return false;
	}

	@Override
	public void tick() {
		Entity owner = this.getOwner();

		this.applyDrag();
		// TODO: Implement `tick()` method from ExplosiveProjectileEntity Minecraft class to this.
	}

	// /////////////////////////////// //
	// GETTERS, SETTERS, AND QUESTIONS //
	// /////////////////////////////// //

	// PROTECTED

	protected RaycastContext.ShapeType getRaycastShape() {
		return RaycastContext.ShapeType.COLLIDER;
	}

	@Override
	protected boolean canHit(Entity entity) {
		return super.canHit(entity) && !entity.noClip;
	}

	// PUBLIC

	/**
	 * Defines the maximum effective radius of the explosion. Within this
	 * radius, all entities will receive the full {@link #getDamage() base damage}.
	 * Entities outside this radius will receive reduced damage based on the
	 * {@link #getDamageReduction() damage reduction} multiplier.
	 *
	 * @return The maximum effective radius of the explosion.
	 */
	public abstract double getEffectiveRadius();

	/**
	 * Defines the maximum radius this explosion can reach. Entities within this
	 * radius will receive damage, but the damage will be reduced based on the
	 * distance from the {@link #getEffectiveRadius() effective radius} of the
	 * explosion.
	 *
	 * @return The maximum radius of the explosion.
	 */
	public abstract double getMaxDamageRadius();

	/**
	 * Defines the damage reduction multiplier for entities outside the
	 * {@link #getEffectiveRadius() effective radius} of the explosion.
	 * <br><br>
	 * The reduced damage is calculated as:
	 * <pre>{@code baseDamage - (reduction * (radius - effectiveRadius))}</pre>
	 * <br>
	 * This means that if the <b>base damage is 10</b>, the <b>reduction is 3.75</b>,
	 * and the <b>radius is 5</b> and with an <b>effective range of 3</b>, the <b>
	 * reduced damage will be 7.5</b>. Plugging the values will result to this:
	 * <pre>{@code 10 - (3.75 * (5 - 3)) = 7.5}</pre>
	 *
	 * @return The damage reduction multiplier for entities outside the effective radius.
	 */
	public abstract double getDamageReduction();
}
