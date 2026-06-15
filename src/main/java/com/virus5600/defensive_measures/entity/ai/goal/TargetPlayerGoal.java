package com.virus5600.defensive_measures.entity.ai.goal;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import org.jspecify.annotations.NonNull;

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
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class TargetPlayerGoal extends NearestAttackableTargetGoal<Player> {
	public TargetPlayerGoal(TurretEntity turret) {
		super(turret, Player.class, true);
	}

	@Override
	public boolean canUse() {
		return this.mob.getTeam() != null && super.canUse();
	}

	@Override
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
