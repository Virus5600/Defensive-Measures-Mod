package com.virus5600.defensive_measures.model.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.projectiles.CannonballEntity;

@Environment(EnvType.CLIENT)
public class CannonballModel extends GeoModel<CannonballEntity> {
	/////////////////////////
	/// INTERFACE METHODS ///
	/////////////////////////

	@Override
	public Identifier getModelResource(CannonballEntity cannonballEntity, @Nullable GeoRenderer<CannonballEntity> geoRenderer) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "geo/projectiles/cannonball.geo.json");
	}

	@Override
	public Identifier getTextureResource(CannonballEntity cannonballEntity, @Nullable GeoRenderer<CannonballEntity> geoRenderer) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannonball.png");
	}

	@Override
	public Identifier getAnimationResource(CannonballEntity animatable) {
		return null;
	}
}
