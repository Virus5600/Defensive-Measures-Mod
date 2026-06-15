package com.virus5600.defensive_measures.entity.ai.goal;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import org.jspecify.annotations.NonNull;

/**
 * A goal that targets entities that are not in the same team as the turret.
 * <br><br>
 * This is based originally on {@code TargetOtherTeamGoal} of the {@link net.minecraft.world.entity.monster.Shulker ShulkerEntity}
 * class. But since the original was not public, this implementation was created.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class TargetOtherTeamGoal extends NearestAttackableTargetGoal<LivingEntity> {
	public TargetOtherTeamGoal(TurretEntity turret) {
		super(turret, LivingEntity.class, 10, true, false, (entity, serverWorld) -> entity instanceof LivingEntity);
	}

	public boolean canUse() {
		return this.mob.getTeam() != null && super.canUse();
	}

	protected @NonNull AABB getTargetSearchArea(double distance) {
		Direction dir = Direction.DOWN;

		if (dir.getAxis() == Direction.Axis.X) {
			return this.mob.getBoundingBox().inflate(4.0, distance, distance);
		}

		return dir.getAxis() == Direction.Axis.Z
			? this.mob.getBoundingBox().inflate(distance, distance, 4.0)
			: this.mob.getBoundingBox().inflate(distance, 4.0, distance);

	}
}
