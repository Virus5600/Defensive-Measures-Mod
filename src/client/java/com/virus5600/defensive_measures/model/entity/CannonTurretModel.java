package com.virus5600.defensive_measures.model.entity;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class CannonTurretModel extends GeoModel<CannonTurretEntity> {
	///////////////////////
	// INTERFACE METHODS //
	///////////////////////

	@Override
	public Identifier getModelResource(CannonTurretEntity animatable) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "geo/cannon_turret.geo.json");
	}

	@Override
	public Identifier getTextureResource(CannonTurretEntity animatable) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannon_turret.png");
	}

	@Override
	public Identifier getAnimationResource(CannonTurretEntity animatable) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "animations/cannon_turret.animation.json");
	}

	// TODO: Fix the animation not working.
}
