package com.virus5600.defensive_measures.model.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.projectiles.MGBulletEntity;

/**
 * MG Bullet Model
 *
 * @see BaseProjectileModel
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class MGBulletModel extends BaseProjectileModel<MGBulletEntity> {
	public MGBulletModel() {
		super(
			DefensiveMeasures.MOD_ID,
			"geo/projectiles/mg_bullet.geo.json",
			"textures/entity/mg_turret/mg_bullet.png",
			"animations/projectiles/mg_bullet.animation.json"
		);
	}
}
