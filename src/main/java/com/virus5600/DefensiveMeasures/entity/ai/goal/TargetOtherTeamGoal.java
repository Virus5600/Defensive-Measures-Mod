package com.virus5600.DefensiveMeasures.entity.ai.goal;

import com.virus5600.DefensiveMeasures.entity.custom.TurretEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.Monster;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class TargetOtherTeamGoal extends ActiveTargetGoal<LivingEntity> {	
	public TargetOtherTeamGoal(TurretEntity turret) {
		super(turret, LivingEntity.class, 10, true, false, (entity) -> {
			return entity instanceof Monster;
		});
	}

	public boolean canStart() {
		return this.mob.getScoreboardTeam() == null ? false : super.canStart();
	}

	protected Box getSearchBox(double distance) {
		Direction direction = Direction.DOWN;
		if (direction.getAxis() == Direction.Axis.X) {
			return this.mob.getBoundingBox().expand(4.0, distance, distance);
		} else {
			return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().expand(distance, distance, 4.0) : this.mob.getBoundingBox().expand(distance, 4.0, distance);
		}
	}
}
