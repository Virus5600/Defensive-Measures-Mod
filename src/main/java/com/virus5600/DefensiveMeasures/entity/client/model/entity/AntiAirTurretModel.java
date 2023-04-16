package com.virus5600.DefensiveMeasures.entity.client.model.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.custom.AntiAirTurretEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class AntiAirTurretModel extends AnimatedGeoModel<AntiAirTurretEntity> {

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getAnimationResource(final AntiAirTurretEntity animatable) {
		return new Identifier(DefensiveMeasures.MOD_ID, "animations/anti_air_turret.animation.json");
	}

	@Override
	public Identifier getModelResource(final AntiAirTurretEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "geo/anti_air_turret.geo.json");
	}

	@Override
	public Identifier getTextureResource(final AntiAirTurretEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/anti_air_turret/anti_air_turret.png");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setLivingAnimations(final AntiAirTurretEntity entity, final Integer uniqueID, final AnimationEvent customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		IBone base = this.getAnimationProcessor().getBone("base");
		IBone neck = this.getAnimationProcessor().getBone("stand");
		IBone head = this.getAnimationProcessor().getBone("head");

		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if (neck != null) {
			float targetYRot = (extraData.netHeadYaw * ((float) Math.PI / 180f));
			float neckRotY =  targetYRot;
			neck.setRotationY(neckRotY);

			if (head != null) {
				float targetXRot = (extraData.headPitch * ((float) Math.PI / 180f));
				float headRotX = (targetXRot > 40f ? 40f : targetXRot < -75f ? -75f : targetXRot) + 0.125f;
				head.setRotationX(headRotX);
			}
		}

		base.setRotationZ(0);
		base.setRotationY(0);
	}
}
