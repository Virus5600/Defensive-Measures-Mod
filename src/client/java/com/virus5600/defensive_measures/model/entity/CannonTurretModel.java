package com.virus5600.defensive_measures.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;

@Environment(EnvType.CLIENT)
public class CannonTurretModel extends BaseTurretModel<CannonTurretEntity> {
	public CannonTurretModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"geo/cannon_turret.geo.json",
			"textures/entity/cannon_turret/cannon_turret.png",
			"animations/cannon_turret.animation.json",
			"base",
			"stand",
			"head"
		);
	}
}
