package com.virus5600.defensive_measures.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoRenderer;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class BallistaTurretModel extends GeoModel<BallistaTurretEntity> {
	/////////////////////////
	/// INTERFACE METHODS ///
	/////////////////////////

	@Override
	public Identifier getModelResource(BallistaTurretEntity ballistaTurretEntity, @Nullable GeoRenderer<BallistaTurretEntity> geoRenderer) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "geo/ballista.geo.json");
	}

	@Override
	public Identifier getTextureResource(BallistaTurretEntity ballistaTurretEntity, @Nullable GeoRenderer<BallistaTurretEntity> geoRenderer) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "textures/entity/ballista/ballista.png");
	}

	@Override
	public Identifier getAnimationResource(BallistaTurretEntity animatable) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "animations/ballista.animation.json");
	}

	@Override
	public void setCustomAnimations(BallistaTurretEntity animatable, long instanceId, AnimationState<BallistaTurretEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

		Optional<GeoBone> base = this.getBone("base");
		Optional<GeoBone> neck = this.getBone("head");
		Optional<GeoBone> head = this.getBone("bow");

		EntityModelData extraData = (EntityModelData) animationState.getExtraData()
			.get(DataTickets.ENTITY_MODEL_DATA);

		if (neck.isPresent()) {
			float targetYRot = (extraData.netHeadYaw() * ((float) Math.PI / 180F));
			targetYRot = Math.min(targetYRot, animatable.getMaxHeadRotation());
			neck.get().updateRotation(0, targetYRot, 0);

			if (head.isPresent()) {
				float targetXRot = (extraData.headPitch() * ((float) Math.PI / 180F));
				targetXRot = Math.min(targetXRot, animatable.getMaxLookPitchChange());
				head.get().updateRotation(targetXRot, 0, 0);
			}
		}

		animatable.setBodyYaw(0);
		base.ifPresent(geoBone -> geoBone.setRotY(0));
	}
}
