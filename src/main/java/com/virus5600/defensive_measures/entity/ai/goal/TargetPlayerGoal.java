package com.virus5600.defensive_measures.entity.ai.goal;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

/**
 * A goal that targets players.
 * <br><br>
 * This goal allows the turret to target players within its range. And if
 * the turret has a team, the turret will include players from the opposing team
 * as a valid target. And if the turret has an owner, the turret will not target
 * its owner. Furthermore, whitelisted players will be ignored by the turret.
 * <br><br>
 * <b>Implementation of this goal is incomplete.</b>
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
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
