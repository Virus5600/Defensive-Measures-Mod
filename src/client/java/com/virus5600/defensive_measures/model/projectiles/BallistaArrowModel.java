package com.virus5600.defensive_measures.model.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.projectiles.BallistaArrowEntity;

@Environment(EnvType.CLIENT)
public class BallistaArrowModel extends GeoModel<BallistaArrowEntity> {
	/////////////////////////
	/// INTERFACE METHODS ///
	/////////////////////////

	@Override
	public Identifier getModelResource(BallistaArrowEntity ballistaArrowEntity, @Nullable GeoRenderer<BallistaArrowEntity> geoRenderer) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "geo/projectiles/ballista_arrow.geo.json");
	}

	@Override
	public Identifier getTextureResource(BallistaArrowEntity ballistaArrowEntity, @Nullable GeoRenderer<BallistaArrowEntity> geoRenderer) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "textures/entity/ballista/ballista_arrow.png");
	}

	@Override
	public Identifier getAnimationResource(BallistaArrowEntity animatable) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "animations/projectiles/ballista_arrow.animation.json");
	}
}
