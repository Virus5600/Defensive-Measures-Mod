package com.virus5600.defensive_measures.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

/**
 * A goal that targets entities that are not in the same team as the turret.
 * <br><br>
 * This is based originally on {@code TargetOtherTeamGoal} of the {@link net.minecraft.entity.mob.ShulkerEntity ShulkerEntity}
 * class. But since the original was not public, this implementation was created.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class TargetOtherTeamGoal extends ActiveTargetGoal<LivingEntity> {
	public TargetOtherTeamGoal(TurretEntity turret) {
		super(turret, LivingEntity.class, 10, true, false, (entity, _) -> entity instanceof LivingEntity);
	}

	public boolean canStart() {
		return this.mob.getScoreboardTeam() != null && super.canStart();
	}

	protected Box getSearchBox(double distance) {
		Direction dir = Direction.DOWN;

		if (dir.getAxis() == Direction.Axis.X) {
			return this.mob.getBoundingBox().expand(4.0, distance, distance);
		}

		return dir.getAxis() == Direction.Axis.Z
			? this.mob.getBoundingBox().expand(distance, distance, 4.0)
			: this.mob.getBoundingBox().expand(distance, 4.0, distance);

	}
}
