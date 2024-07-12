package com.virus5600.defensive_measures.entity.ai.goal;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.player.PlayerEntity;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class TargetPlayerGoal extends ActiveTargetGoal<PlayerEntity> {
	public TargetPlayerGoal(TurretEntity turret) {
		super(turret, PlayerEntity.class, true);
	}

	@Override
	public boolean canStart() {
		return this.mob.getScoreboardTeam() != null && super.canStart();
	}

	@Override
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
