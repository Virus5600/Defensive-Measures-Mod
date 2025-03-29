package com.virus5600.defensive_measures.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity;

/**
 * Ballista Turret Model.
 *
 * @see BaseTurretModel
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class BallistaTurretModel extends BaseTurretModel<BallistaTurretEntity> {
	public BallistaTurretModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"geo/ballista.geo.json",
			"textures/entity/ballista/ballista.png",
			"animations/ballista.animation.json",
			"base",
			"head",
			"bow"
		);
	}
}
