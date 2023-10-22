package com.virus5600.DefensiveMeasures.entity.client.model.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.projectile.AntiAirProjectileEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AntiAirProjectileModel extends AnimatedGeoModel<AntiAirProjectileEntity> {

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getAnimationResource(final AntiAirProjectileEntity animatable) {
		return new Identifier(DefensiveMeasures.MOD_ID, "animations/anti_air_projectile.animation.json");
	}

	@Override
	public Identifier getModelResource(final AntiAirProjectileEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "geo/anti_air_projectile.geo.json");
	}

	@Override
	public Identifier getTextureResource(final AntiAirProjectileEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/anti_air_turret/anti_air_projectile.png");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setLivingAnimations(final AntiAirProjectileEntity entity, final Integer uniqueID, final AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}
}
