package com.virus5600.DefensiveMeasures.entity.client.model.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.custom.CannonTurretEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class CannonTurretModel extends AnimatedGeoModel<CannonTurretEntity> {

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getAnimationResource(CannonTurretEntity animatable) {
		return new Identifier(DefensiveMeasures.MOD_ID, "animations/cannon_turret.animation.json");
	}

	@Override
	public Identifier getModelResource(CannonTurretEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "geo/cannon_turret.geo.json");
	}

	@Override
	public Identifier getTextureResource(CannonTurretEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannon_turret.png");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setLivingAnimations(CannonTurretEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
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
				float headRotX = (targetXRot > 22.5f ? 30f : targetXRot < -22.5f ? -30f : targetXRot) + 0.125f;
				head.setRotationX(headRotX);
			}
		}

		base.setRotationZ(0);
		base.setRotationY(0);
	}
}
