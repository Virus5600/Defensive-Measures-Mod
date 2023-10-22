package com.virus5600.DefensiveMeasures.entity.ai.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.FlyingEntity;

public class PrioritizeAirTargetGoal extends PrioritizeEntityTargetGoal {

	public PrioritizeAirTargetGoal(final RangedAttackMob mob, final double mobSpeed, final int intervalTicks, final float maxShootRange) {
		this(mob, mobSpeed, intervalTicks, intervalTicks, maxShootRange);
	}

	public PrioritizeAirTargetGoal(final RangedAttackMob mob, final double mobSpeed, final int minIntervalTicks, final int maxIntervalTicks, final float maxShootRange) {
		super(mob, mobSpeed, minIntervalTicks, maxIntervalTicks, maxShootRange, ((List<Class<? extends LivingEntity>>) new ArrayList<Class<? extends LivingEntity>>() {
			private static final long serialVersionUID = -5500668492478757836L;
			{
				add(FlyingEntity.class);
				add(EnderDragonEntity.class);
			}
		}));
	}
}