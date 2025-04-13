package com.virus5600.defensive_measures.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * Literally a copy of {@link net.minecraft.entity.ai.goal.ProjectileAttackGoal ProjectileAttackGoal}
 * aside from some additional timing related methods to allow for more control over what the entity
 * is doing during the attack. This goal is primarily designed to work on
 * {@link TurretEntity turret entities} that can shoot projectiles at a target.
 * <br><br>
 * Specifically, the methods that are tailored-fit for the turret entities are:
 *
 * <ul>
 * 	<li>
 * 	    {@link #canStart()} to determine if the turret can start attacking the target.<br>
 * 		This method is modified to also check if the target is within the rotation limits of the turret.
 * 	</li>
 *
 * 	<li>
 *		{@link #tick()} to determine what the turret should be doing every tick.<br>
 * 		This method is modified to also check if the target is within the rotation limits of the turret.
 * 	</li>
 *
 * 	<li><s>
 * 		{@code getShootingPitch(LivingEntity)} to calculate the initial pitch angle in degrees for
 * 		the shooter to aim at the target.<br>
 * 		This is due to the use of {@link TurretEntity.TurretProjectileVelocity} class to calculate
 * 		the projectile velocity based on the target's position and the turret's position.
 * 	</s></li>
 *
 * 	<li><s>
 * 		{@code getShootingYaw(LivingEntity)} to calculate the initial yaw angle in degrees for
 * 		the shooter to aim at the target.<br>
 * 		This is due to the use of {@link TurretEntity.TurretProjectileVelocity} class to calculate
 * 		the projectile velocity based on the target's position and the turret's position.
 * 	</s></li>
 *
 * 	<li>
 * 		{@link #isWithinRotationLimit(LivingEntity)} to determine if the target is within the
 * 		rotation limits of the turret.<br>
 * 		This is due to the use of {@link TurretEntity.TurretProjectileVelocity} class to calculate
 * 		the projectile velocity based on the target's position and the turret's position.
 * 	</li>
 * </ul>
 *
 * <b>NOTE:</b> {@code getShootingPitch(LivingEntity)} and {@code getShootingYaw(LivingEntity)} are
 * 			yet to be implemented as they are not yet needed.
 * @see net.minecraft.entity.ai.goal.ProjectileAttackGoal ProjectileAttackGoal
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ProjectileAttackGoal extends net.minecraft.entity.ai.goal.ProjectileAttackGoal {
	@Nullable
	private LivingEntity target;
	private int updateCountdownTicks = -1;
	private int seenTargetTicks;
	private AttackPhase attackPhase = AttackPhase.IDLE;
	private Consumer<LivingEntity> startCallback;
	private Consumer<LivingEntity> stopCallback;

	private final MobEntity mob;
	private final RangedAttackMob owner;
	private final double mobSpeed;
	private final int minIntervalTicks;
	private final int maxIntervalTicks;
	private final float maxShootRange;
	private final float minShootRange;
	private final float squaredMaxShootRange;
	private final float squaredMinShootRange;

	public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
		this(mob, mobSpeed, intervalTicks, intervalTicks, maxShootRange);
	}

	public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange, float minShootRange) {
		this(mob, mobSpeed, intervalTicks, intervalTicks, maxShootRange, minShootRange);
	}

	public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange) {
		this(mob, mobSpeed, minIntervalTicks, maxIntervalTicks, maxShootRange, 0.1F);
	}

	public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange, float minShootRange) {
		super(mob, mobSpeed, minIntervalTicks, maxIntervalTicks, maxShootRange);

		if (!(mob instanceof LivingEntity)) {
			throw new IllegalArgumentException("ProjectileAttackGoal requires Mob implements RangedAttackMob");
		} else {
			this.owner = mob;
			this.mob = (MobEntity)mob;
			this.mobSpeed = mobSpeed;
			this.minIntervalTicks = minIntervalTicks;
			this.maxIntervalTicks = maxIntervalTicks;
			this.maxShootRange = maxShootRange;
			this.minShootRange = minShootRange;
			this.squaredMaxShootRange = maxShootRange * maxShootRange;
			this.squaredMinShootRange = minShootRange * minShootRange;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}
	}

	/**
	 * Sets the callback to be called when the attack starts. This allows for
	 * custom behavior to be executed when the attack starts, such as playing
	 * a sound or animation.
	 * <br><br>
	 * The callback's parameter is a {@link LivingEntity} that represents the
	 * entity that is starting the attack. This can be used to access its
	 * attributes, such as health, position, and rotation.
	 *
	 * @param startCallback The callback to be called when the attack starts.
	 */
	public void setStartCallback(Consumer<LivingEntity> startCallback) {
		this.startCallback = startCallback;
	}

	/**
	 * Sets the callback to be called when the attack stops. This allows for
	 * custom behavior to be executed when the attack stops, such as playing
	 * a sound or animation.
	 * <br><br>
	 * The callback's parameter is a {@link LivingEntity} that represents the
	 * entity that is stopping the attack. This can be used to access its
	 * attributes, such as health, position, and rotation.
	 *
	 * @param stopCallback The callback to be called when the attack stops.
	 */
	public void setStopCallback(Consumer<LivingEntity> stopCallback) {
		this.stopCallback = stopCallback;
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity != null && livingEntity.isAlive() && this.isWithinRotationLimit(livingEntity)) {
			this.target = livingEntity;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.target != null) {
			// Idle -> Start
			if (this.attackPhase == AttackPhase.IDLE) {
				this.attackPhase = AttackPhase.ATTACK_START;
				this.startCallback.accept(this.mob);
			}
			// Start -> Attacking
			else if (this.attackPhase == AttackPhase.ATTACK_START) {
				this.attackPhase = AttackPhase.ATTACKING;
			}

			return this.canStart() || this.target.isAlive() && !this.mob.getNavigation().isIdle();
		}
		// * -> End | Idle
		else {
			stop();
		}

		return false;
	}

	@Override
	public void stop() {
		this.target = null;
		this.seenTargetTicks = 0;
		this.updateCountdownTicks = -1;

		// !(End && Idle) -> End
		if (this.attackPhase != AttackPhase.ATTACK_END && this.attackPhase != AttackPhase.IDLE) {
			this.attackPhase = AttackPhase.ATTACK_END;
			this.stopCallback.accept(this.mob);
		}
		// End -> Idle
		else if (this.attackPhase == AttackPhase.ATTACK_END) {
			this.attackPhase = AttackPhase.IDLE;
		}
	}

	@Override
	public void tick() {
		if (this.target != null) {
			double squaredDistance = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
			boolean canBeSeen = this.mob.getVisibilityCache().canSee(this.target) && this.isWithinRotationLimit(this.target);
			boolean isPastMaxRange = squaredDistance > (double) this.squaredMaxShootRange,
				isPastMinRange = squaredDistance < (double) this.squaredMinShootRange,
				isInRange = !isPastMaxRange && !isPastMinRange,
				isStillSeen = this.seenTargetTicks >= 5;

			if (canBeSeen) {
				this.seenTargetTicks++;
			} else {
				this.seenTargetTicks = 0;
			}

			if (isInRange && isStillSeen) {
				this.mob.getNavigation().stop();
			} else {
				this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
			}

			this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
			if (--this.updateCountdownTicks == 0) {
				if (!canBeSeen) {
					return;
				}

				float nextShotScaler = (float) Math.sqrt(squaredDistance) / this.maxShootRange;
				float pullProgress = MathHelper.clamp(nextShotScaler, 0.1F, 1.0F);

				this.owner.shootAt(this.target, pullProgress);
				this.updateCountdownTicks = MathHelper.floor(nextShotScaler * (float) (this.maxIntervalTicks - this.minIntervalTicks) + (float) this.minIntervalTicks);
			} else if (this.updateCountdownTicks < 0) {
				this.updateCountdownTicks = MathHelper.floor(
					MathHelper.lerp(Math.sqrt(squaredDistance) / (double) this.maxShootRange, this.minIntervalTicks, this.maxIntervalTicks)
				);
			}
		}
		else {
			if (this.attackPhase != AttackPhase.IDLE) {
				this.stop();
			}
		}
	}

	/**
	 * Determines if the entity is locked into a target but not yet
	 * attacking it.
	 * <br><br>
	 * For more control over the countdown ticks, use {@link #isLockedButNotAttacking(int) isLockedButNotAttacking(int)}.
	 *
	 * @return {@code true} if the entity is locked into a target but
	 * not yet attacking it, {@code false} otherwise.
	 *
	 * @see #isLockedButNotAttacking(int)
	 */
	public boolean isLockedButNotAttacking() {
		return this.isLockedButNotAttacking(0);
	}

	/**
	 * Determines if the entity is locked into a target but not yet
	 * attacking it. The {@code allowance} parameter allows for a lee-way
	 * in the countdown ticks before the entity is considered locked in.
	 *
	 * @param allowance The amount of tick allowance before the countdown reaches 0.
	 *
	 * @return {@code true} if the entity is locked into a target but
	 * not yet attacking it, {@code false} otherwise.
	 */
	public boolean isLockedButNotAttacking(int allowance) {
		return this.updateCountdownTicks > allowance;
	}

	/**
	 * Identifies the current attack phase of the entity.
	 *
	 * @return {@link AttackPhase} The current attack phase of the entity.
	 */
	public AttackPhase getAttackPhase() {
		return this.attackPhase;
	}

	/**
	 * Gets the current countdown ticks for the attack. This
	 * allows for more flexibility in determining what the entity
	 * will do during the attack.
	 *
	 * @return The current countdown ticks for the attack.
	 */
	public int getUpdateCountdownTicks() {
		return this.updateCountdownTicks;
	}

	public float getMaxAttackRange() {
		return this.maxShootRange;
	}

	public float getMinAttackRange() {
		return this.minShootRange;
	}

	/**
	 * Calculates the initial pitch angle in degrees for the shooter to aim at the target. This
	 * allows the shooter to align its pitch angle to the trajectory of the projectile it will
	 * shoot.
	 *
	 * @param target The target on which the shooter will aim at.
	 * @return The pitch angle in degrees.
	 */
	public float getShootingPitch(LivingEntity target, boolean shouldClamp) {
		Vec3d velocity = TurretEntity.TurretProjectileVelocity
			.init((TurretEntity) this.mob)
			.setVelocity(target)
			.getVelocity();

		float maxPitch = this.mob.getMaxLookPitchChange();

		float vx = MathHelper.sqrt((float) (velocity.x * velocity.x + velocity.z * velocity.z));
		float p = (float) -Math.atan2(velocity.y, vx);
		p *= (float) (180.0 / Math.PI);

		if (shouldClamp) {
			p = MathHelper.clamp(p, -maxPitch, maxPitch);
		}

		return p;
	}

	public float getShootingYaw(LivingEntity target, boolean shouldClamp) {
		Vec3d velocity = TurretEntity.TurretProjectileVelocity
			.init((TurretEntity) this.mob)
			.setVelocity(target)
			.getVelocity();

		float maxYaw = this.mob.getMaxHeadRotation();

		float y = (float) Math.atan2(velocity.z, velocity.x);
		y *= (float) (180.0 / Math.PI);

		if (shouldClamp) {
			y = MathHelper.clamp(y, -maxYaw, maxYaw);
		}

		return y;
	}

	/**
	 * Checks whether the target is within the rotation limits of the turret.
	 *
	 * @param target The target to check.
	 * @return {@code true} if the target is within the rotation limits of the turret, {@code false} otherwise.
	 */
	public boolean isWithinRotationLimit(LivingEntity target) {
		float maxPitch = this.mob.getMaxLookPitchChange();
		float minPitch = -maxPitch;
		float maxYaw = this.mob.getMaxHeadRotation();

		if (this.mob instanceof TurretEntity) {
			minPitch = ((TurretEntity) this.mob).getMinLookPitchChange();
		}

		float targetPitch = this.getShootingPitch(target, false);
		float targetYaw = this.getShootingYaw(target, false);

		boolean withinPitch = targetPitch <= maxPitch && targetPitch >= minPitch;
		boolean withinYaw = targetYaw <= maxYaw && targetYaw >= -maxYaw;

		return withinPitch && withinYaw;
	}

	/**
	 * Enum representing the different phases of the attack.
	 */
	public enum AttackPhase {
		IDLE,
		ATTACK_START,
		ATTACKING,
		ATTACK_END,
	}
}
