package com.virus5600.DefensiveMeasures.entity.client.model.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.custom.BallistaTurretEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class BallistaTurretModel extends AnimatedGeoModel<BallistaTurretEntity> {

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getAnimationResource(final BallistaTurretEntity animatable) {
		return new Identifier(DefensiveMeasures.MOD_ID, "animations/ballista.animation.json");
	}

	@Override
	public Identifier getModelResource(final BallistaTurretEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "geo/ballista.geo.json");
	}

	@Override
	public Identifier getTextureResource(final BallistaTurretEntity object) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/ballista/ballista.png");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setLivingAnimations(final BallistaTurretEntity entity, final Integer uniqueID, final AnimationEvent customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		IBone base = this.getAnimationProcessor().getBone("base");
		IBone neck = this.getAnimationProcessor().getBone("head");
		IBone head = this.getAnimationProcessor().getBone("bow");

		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if (neck != null) {
			float targetYRot = (extraData.netHeadYaw * ((float) Math.PI / 180f));
			float neckRotY =  targetYRot;
			neck.setRotationY(neckRotY);

			if (head != null) {
				float targetXRot = (extraData.headPitch * ((float) Math.PI / 180f));
				float headRotX = (targetXRot > 25f ? 25f : targetXRot < -25f ? -25f : targetXRot) + 0.125f;
				head.setRotationX(headRotX);
			}
		}

		base.setRotationZ(0);
		base.setRotationY(0);
	}
}
