package com.virus5600.defensive_measures.model.entity;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.AATurretEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Anti Air Turret Model.
 *
 * @see BaseTurretModel
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class AATurretModel extends BaseTurretModel<AATurretEntity> {
	public AATurretModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"geo/anti_air_turret.geo.json",
			"textures/entity/aa_turret/anti_air_turret.png",
			"animations/anti_air_turret.animation.json",
			"base",
			"stand",
			"head"
		);
	}
}
