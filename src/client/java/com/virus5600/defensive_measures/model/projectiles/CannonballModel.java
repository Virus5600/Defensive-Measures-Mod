package com.virus5600.defensive_measures.model.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.projectiles.CannonballEntity;

@Environment(EnvType.CLIENT)
public class CannonballModel extends BaseProjectileModel<CannonballEntity> {
	public CannonballModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"geo/projectiles/cannonball.geo.json",
			"textures/entity/cannon_turret/cannonball.png",
			null
		);
	}
}
