package com.virus5600.defensive_measures.model.entity;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.MGTurretEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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
public class MGTurretModel extends BaseTurretModel<MGTurretEntity> {
	public MGTurretModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"machine_gun_turret",
			"mg_turret/machine_gun_turret",
			"machine_gun_turret",
			"base",
			"body",
			"head"
		);
	}
}
