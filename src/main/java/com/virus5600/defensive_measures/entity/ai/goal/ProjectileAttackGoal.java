package com.virus5600.defensive_measures.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Literally a copy of {@link net.minecraft.entity.ai.goal.ProjectileAttackGoal ProjectileAttackGoal} aside
 * from some additional timing related methods to allow for more control over what the entity is doing
 * during the attack.
 *
 * @see net.minecraft.entity.ai.goal.ProjectileAttackGoal ProjectileAttackGoal
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ProjectileAttackGoal extends net.minecraft.entity.ai.goal.ProjectileAttackGoal {
	private final MobEntity mob;
	private final RangedAttackMob owner;
	@Nullable
	private LivingEntity target;
	private int updateCountdownTicks = -1;
	private final double mobSpeed;
	private int seenTargetTicks;
	private final int minIntervalTicks;
	private final int maxIntervalTicks;
	private final float maxShootRange;
	private final float squaredMaxShootRange;

	public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
		this(mob, mobSpeed, intervalTicks, intervalTicks, maxShootRange);
	}

	public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange) {
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
			this.squaredMaxShootRange = maxShootRange * maxShootRange;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity != null && livingEntity.isAlive()) {
			this.target = livingEntity;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.target != null) {
			return this.canStart() || this.target.isAlive() && !this.mob.getNavigation().isIdle();
		}
		return false;
	}

	@Override
	public void stop() {
		this.target = null;
		this.seenTargetTicks = 0;
		this.updateCountdownTicks = -1;
	}

	@Override
	public void tick() {
		if (this.target != null) {
			double d = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
			boolean bl = this.mob.getVisibilityCache().canSee(this.target);
			if (bl) {
				this.seenTargetTicks++;
			} else {
				this.seenTargetTicks = 0;
			}

			if (!(d > (double) this.squaredMaxShootRange) && this.seenTargetTicks >= 5) {
				this.mob.getNavigation().stop();
			} else {
				this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
			}

			this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
			if (--this.updateCountdownTicks == 0) {
				if (!bl) {
					return;
				}

				float f = (float) Math.sqrt(d) / this.maxShootRange;
				float g = MathHelper.clamp(f, 0.1F, 1.0F);
				this.owner.shootAt(this.target, g);
				this.updateCountdownTicks = MathHelper.floor(f * (float) (this.maxIntervalTicks - this.minIntervalTicks) + (float) this.minIntervalTicks);
			} else if (this.updateCountdownTicks < 0) {
				this.updateCountdownTicks = MathHelper.floor(
					MathHelper.lerp(Math.sqrt(d) / (double) this.maxShootRange, this.minIntervalTicks, this.maxIntervalTicks)
				);
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
	 * Gets the current countdown ticks for the attack. This
	 * allows for more flexibility in determining what the entity
	 * will do during the attack.
	 *
	 * @return The current countdown ticks for the attack.
	 */
	public int getUpdateCountdownTicks() {
		return this.updateCountdownTicks;
	}
}
