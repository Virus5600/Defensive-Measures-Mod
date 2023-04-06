package com.virus5600.DefensiveMeasures.entity.client.model.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.projectile.CannonballEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CannonballModel extends AnimatedGeoModel<CannonballEntity> {

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getAnimationResource(CannonballEntity animatable) {
		return null;
	}

	@Override
	public Identifier getModelResource(CannonballEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "geo/cannonball.geo.json");
	}

	@Override
	public Identifier getTextureResource(CannonballEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannonball.png");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setLivingAnimations(CannonballEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}
}
