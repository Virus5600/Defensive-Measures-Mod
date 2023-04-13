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
	public Identifier getAnimationResource(final CannonballEntity animatable) {
		return null;
	}

	@Override
	public Identifier getModelResource(final CannonballEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "geo/cannonball.geo.json");
	}

	@Override
	public Identifier getTextureResource(final CannonballEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannonball.png");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setLivingAnimations(final CannonballEntity entity, final Integer uniqueID, final AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}
}
