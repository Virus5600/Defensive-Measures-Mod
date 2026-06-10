package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.entity.turrets.tier2.AATurretEntity;
import com.virus5600.defensive_measures.particle.ModParticles;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * The projectile used by {@link AATurretEntity}.
 * <br><br>
 * Represents an explosive projectile that shoots out fragments (in reality) upon exploding to
 * create a blanket of smaller, deadly projectiles around its area, providing a great airspace
 * denial. However, in this game, the flak projectile is represented as a projectile that explodes
 * upon hitting something or when it is near its target (if it missed), damaging entities around
 * a large radius.
 * <br><br>
 * With its turret's rate of fire, this projectile has a maximum radius of 10 blocks with 3.5
 * blocks being the effective radius where entities gets the full damage, which gets reduced the
 * farther the entity is from the effective radius.
 *
 * @see AATurretEntity
 * @see CannonballEntity
 * @see ExplosiveProjectileEntity
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class FlakProjectileEntity extends CannonballEntity {
	protected double targetDistance = -1;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public FlakProjectileEntity(
            EntityType<? extends CannonballEntity> entityType,
            Level world
	) {
		super(entityType, world);
	}

	// /////// //
	// METHODS //
	// /////// //

	// PROTECTED

	/**
	 * {@inheritDoc}
	 * @return {@code null} as the flak projectile does not have a trail particle effect.
	 */
	@Override @Nullable
	protected ParticleOptions getTrailParticleType() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @return {@link ModParticles#FLAK_EXPLOSION} as the small explosion particle effect for the flak projectile.
	 */
	@Override @NotNull
	protected ParticleOptions getSmallExplosionParticleType() {
		return ModParticles.FLAK_EXPLOSION;
	}

	/**
	 * {@inheritDoc}
	 * @return {@link ModParticles#FLAK_EXPLOSION} as the large explosion particle effect for the flak projectile.
	 */
	@Override @NotNull
	protected ParticleOptions getLargeExplosionParticleType() {
		return ModParticles.FLAK_EXPLOSION;
	}

	@Override
	protected double getDefaultGravity() {
		return this.isInWater() ? 0.0625 : 0;
	}

	@Override
	protected float getDrag() {
		return 1F;
	}

	// PUBLIC

	@Override
	public boolean hurtServer(@NonNull ServerLevel world, @NonNull DamageSource source, float amount) {
		if (source.is(DamageTypes.EXPLOSION)) {
			this.doDamage();
			return true;
		}
		return false;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level() instanceof ServerLevel world) {
			// Update the distance traveled by this projectile
			this.moveDist = (float) this.getSpawnPos()
				.distanceTo(this.position());

			// If there's an owner, identify its target and get its distance from the owner.
			if (this.getOwner() instanceof Mob entity) {
				LivingEntity target = entity.getTarget();

				if (target != null) {
					this.targetDistance = target.position()
						.distanceTo(this.getSpawnPos());
				}
			}

			// Creates a variable fuse trigger near the target
			double varTargetDistance = this.targetDistance +
				(this.targetDistance *
					(world.getRandom().nextIntBetweenInclusive(-2, 2) / 100.0));

			// Calculate the age threshold based on the projectile's speed, with a minimum of 20
			// ticks (1 second). This makes the projectile explode faster if it's moving faster,
			// and slower if it's moving slower, but it will always explode after at least 20 ticks.
			double variableVelLen = this.getDeltaMovement().length() +
				Mth.nextDouble(
					world.getRandom(),
					-this.getDeltaMovement().length() * 0.1,
					this.getDeltaMovement().length() * 0.1
				);
			double ageThreshold = Mth.clamp((variableVelLen / 5) * 20, 20, Double.MAX_VALUE);

			// Explodes this projectile if it hits the minimum threshold (only if the target's
			// distance is 0 or more) or if it's age is more than the age threshold (which is
			// determined by the projectile's speed, with a minimum of 20 ticks or 1 second).
			if ((this.targetDistance > -1 && this.moveDist >= varTargetDistance) ||
				((this.targetDistance == -1 && this.tickCount >= ageThreshold))) {
				this.doDamage();
			}
		}
	}

	@Override
	public void doDamage() {
		super.doDamage();

		if (this.level() instanceof ServerLevel world) {
			world.sendParticles(
				ModParticles.FLAK_EXPLOSION,
				true, true,
				this.getX(), this.getEyeY(), this.getZ(),
				1,
				0, 0, 0,
				0
			);
		}
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	// EplosiveProjectileEntity //
	@Override
	public double getEffectiveRadius() {
		return 3.75;
	}

	@Override
	public double getMaxDamageRadius() {
		return 10;
	}

	@Override
	public double getDamageReduction() {
		return 0.125;
	}

	// ExplosiveEntity //
	@Override
	public boolean canDestroyBlocks() {
		return false;
	}
}
