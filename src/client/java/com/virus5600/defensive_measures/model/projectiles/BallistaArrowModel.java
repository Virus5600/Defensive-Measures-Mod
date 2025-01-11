package com.virus5600.defensive_measures.model.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.projectiles.BallistaArrowEntity;

@Environment(EnvType.CLIENT)
public class BallistaArrowModel extends BaseProjectileModel<BallistaArrowEntity> {
	public BallistaArrowModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"geo/projectiles/ballista_arrow.geo.json",
			"textures/entity/ballista/ballista_arrow.png",
			"animations/projectiles/ballista_arrow.animation.json"
		);
	}
}
