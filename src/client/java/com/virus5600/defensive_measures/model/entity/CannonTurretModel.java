package com.virus5600.defensive_measures.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;

/**
 * Cannon Turret Model
 *
 * @see BaseTurretModel
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class CannonTurretModel extends BaseTurretModel<CannonTurretEntity> {
	public CannonTurretModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"geckolib/models/cannon_turret.geo.json",
			"textures/entity/cannon_turret/cannon_turret.png",
			"geckolib/animations/cannon_turret.animation.json",
			"base",
			"stand",
			"head"
		);
	}
}
