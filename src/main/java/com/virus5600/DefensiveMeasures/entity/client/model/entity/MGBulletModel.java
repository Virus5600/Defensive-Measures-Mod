package com.virus5600.DefensiveMeasures.entity.client.model.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.projectile.MGBulletEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MGBulletModel extends AnimatedGeoModel<MGBulletEntity> {

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getAnimationResource(final MGBulletEntity animatable) {
		return new Identifier(DefensiveMeasures.MOD_ID, "animations/mg_bullet.animation.json");
	}

	@Override
	public Identifier getModelResource(final MGBulletEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "geo/mg_bullet.geo.json");
	}

	@Override
	public Identifier getTextureResource(final MGBulletEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/mg_turret/mg_bullet.png");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setLivingAnimations(final MGBulletEntity entity, final Integer uniqueID, final AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}
}
