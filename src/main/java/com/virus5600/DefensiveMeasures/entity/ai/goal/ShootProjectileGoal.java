package com.virus5600.DefensiveMeasures.entity.ai.goal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.custom.TurretEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvents;

public class ShootProjectileGoal extends Goal {
	private int counter;
	/**
	 * Owner of the said projectile.
	 */
	private TurretEntity turret;
	/**
	 * The class type of the projectile.
	 */
	private Class<?> projectile;
	/**
	 * Parameter values for the first constructor of the class provided on {@link #projectile}.
	 */
	private Object[] args;

	/**
	 * Similar to the {@link ShulkerEntity}'s {@code ShootBulletGoal} which allows the Shulker to attack using their Shulker Bullet Projectile.
	 *
	 * @param entity Owner of the said projectile, defined as a {@link TurretEntity}
	 * @param projectile The class type of the projectile this {@code TurretEntity}
	 * @param projectileArgs Parameter values for the first constructor of the class provided on {@code projectile}.
	 */
	public ShootProjectileGoal(final TurretEntity entity, final Class<?> projectile, final Object... projectileArgs) {
		this.turret = entity;
		this.projectile = projectile;
		this.args = projectileArgs;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	public boolean canStart() {
		LivingEntity livingEntity = turret.getTarget();

		return (livingEntity != null && livingEntity.isAlive());
	}

	@Override
    public boolean shouldContinue() {
        return (this.canStart() || !((MobEntity) this.turret).getNavigation().isIdle());
    }

	public void start() {
		super.start();
		((MobEntity) this.turret).setAttacking(true);
	}

	public void stop() {
		super.stop();
        ((MobEntity) this.turret).setAttacking(false);
//        this.targetSeeingTicker = 0;
//        this.cooldown = -1;
        ((LivingEntity) this.turret).clearActiveItem();
	}

	public boolean shouldRunEveryTick() {
		return true;
	}

	public void tick() {
		LivingEntity livingEntity = turret.getTarget();
		if (livingEntity != null) {
			turret.getLookControl().lookAt(livingEntity, 180.0F, 180.0F);
			double d = turret.squaredDistanceTo(livingEntity);
			if (d < 400.0) {
				if (this.counter <= 0) {
					this.counter = 20 + turret.getRandom().nextInt(10) * 20 / 2;
					turret.world.spawnEntity(summonProjectile(projectile, args));
					turret.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (turret.getRandom().nextFloat() - turret.getRandom().nextFloat()) * 0.2F + 1.0F);
				}
			}
			else {
				turret.setTarget((LivingEntity) null);
			}

			super.tick();
		}
	}

	/**
	 * Summons a projectile for this entity.
	 * @param projectile The {@code Class} of the projectile that will be used. Stored at {@link #projectile}
	 * @param args All the arguments for the 1st constructor of the class
	 * @return ProjectileEntity
	 *
	 * @see Class
	 */
	private ProjectileEntity summonProjectile(final Class<?> projectile, final Object... args) {
		Class<?> clazz;
		Constructor<?>[] ctor;
		Object obj = null;

		try {
			clazz = Class.forName(projectile.getName());
			ctor = clazz.getConstructors();
			obj = ctor[0].newInstance(args);
		}
		catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			DefensiveMeasures.LOGGER.error(e.getMessage());
		}

		return (ProjectileEntity) obj;
	}
}
