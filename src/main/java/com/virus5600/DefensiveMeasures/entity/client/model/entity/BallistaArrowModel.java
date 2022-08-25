package com.virus5600.DefensiveMeasures.entity.client.model.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.projectile.BallistaArrowEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BallistaArrowModel extends AnimatedGeoModel<BallistaArrowEntity> {
	
	// METHODS //
	// PUBLIC
	@Override
	public Identifier getAnimationResource(BallistaArrowEntity animatable) {
		return new Identifier(DefensiveMeasures.MOD_ID, "animations/ballista_arrow.animation.json");
	}

	@Override
	public Identifier getModelResource(BallistaArrowEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "geo/ballista_arrow.geo.json");
	}

	@Override
	public Identifier getTextureResource(BallistaArrowEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/ballista/ballista_arrow.png");
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setLivingAnimations(BallistaArrowEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}
}