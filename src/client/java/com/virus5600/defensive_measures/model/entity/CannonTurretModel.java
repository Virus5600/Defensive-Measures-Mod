package com.virus5600.defensive_measures.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoRenderer;

import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CannonTurretModel extends GeoModel<CannonTurretEntity> {
	/////////////////////////
	/// INTERFACE METHODS ///
	/////////////////////////

	@Override
	public Identifier getModelResource(CannonTurretEntity cannonTurretEntity, @Nullable GeoRenderer<CannonTurretEntity> geoRenderer) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "geo/cannon_turret.geo.json");
	}

	@Override
	public Identifier getTextureResource(CannonTurretEntity cannonTurretEntity, @Nullable GeoRenderer<CannonTurretEntity> geoRenderer) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannon_turret.png");
	}

	@Override
	public Identifier getAnimationResource(CannonTurretEntity animatable) {
		return Identifier.of(DefensiveMeasures.MOD_ID, "animations/cannon_turret.animation.json");
	}

	@Override
	public void setCustomAnimations(CannonTurretEntity animatable, long instanceId, AnimationState<CannonTurretEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

		Optional<GeoBone> base = this.getBone("base");
		Optional<GeoBone> neck = this.getBone("stand");
		Optional<GeoBone> head = this.getBone("head");

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
