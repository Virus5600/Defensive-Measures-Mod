package com.virus5600.defensive_measures.model.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.projectiles.CannonballEntity;

/**
 * Cannonball Model
 *
 * @see BaseProjectileModel
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class CannonballModel extends BaseProjectileModel<CannonballEntity> {
	public CannonballModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"geckolib/models/projectiles/cannonball.geo.json",
			"textures/entity/cannon_turret/cannonball.png",
			null
		);
	}
}
