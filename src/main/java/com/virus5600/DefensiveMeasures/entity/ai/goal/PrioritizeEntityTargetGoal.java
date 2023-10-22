package com.virus5600.DefensiveMeasures.entity.ai.goal;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;

public class PrioritizeEntityTargetGoal extends ProjectileAttackGoal {
	private final MobEntity mob;
    private float maxShootRange;

    protected List<Class<? extends LivingEntity>> targetTypes;

	public PrioritizeEntityTargetGoal(final RangedAttackMob mob, final double mobSpeed, final int intervalTicks, final float maxShootRange, final List<Class<? extends LivingEntity>> targetTypes) {
		this(mob, mobSpeed, intervalTicks, intervalTicks, maxShootRange, targetTypes);
	}

	public PrioritizeEntityTargetGoal(final RangedAttackMob mob, final double mobSpeed, final int minIntervalTicks, final int maxIntervalTicks, final float maxShootRange, final List<Class<? extends LivingEntity>> targetTypes) {
		super(mob, mobSpeed, minIntervalTicks, maxIntervalTicks, maxShootRange);

        this.mob = (MobEntity) ((Object) mob);
        this.maxShootRange = maxShootRange;

        // For filtering all class that aren't LivingEntity that made it through (Just in case)
        targetTypes.forEach(type -> {
        	if (!LivingEntity.class.isAssignableFrom(type)) {
        		targetTypes.remove(type);
        	}
        });

        this.targetTypes = targetTypes;
	}

	@Override
	public boolean canStart() {
		LivingEntity target = this.mob.getTarget();

		double x1 = mob.getX() + this.maxShootRange;
		double x2 = mob.getX() - this.maxShootRange;
		double y1 = mob.getY() + this.maxShootRange;
		double y2 = mob.getY() - this.maxShootRange;
		double z1 = mob.getZ() + this.maxShootRange;
		double z2 = mob.getZ() - this.maxShootRange;
		Box area = new Box(x1, y1, z1, x2, y2, z2);
		List<Entity> list = this.mob.getWorld().getOtherEntities(mob, area);

		for (Entity entity : list) {
			if (!(entity instanceof LivingEntity)) continue;

			if (this.instanceHit((LivingEntity) entity)) {
				mob.setTarget((LivingEntity) entity);
			}
		}

		if (target == null || !target.isAlive()) return  false;
		return true;
	}

	/**
	 * Identifies whether an entity matches one of the provided entity
	 * classes.
	 *
	 * @param entity The entity to test with
	 * @return boolean
	 */
	public boolean instanceHit(LivingEntity entity) {
		Iterator<Class<? extends LivingEntity>> targets = targetTypes.iterator();
		while (true) {
			Class<? extends LivingEntity> clazz = targets.next();

			if (clazz == null) break;

			if (clazz.isAssignableFrom(entity.getClass())) return true;
		}

		return false;
	}
}