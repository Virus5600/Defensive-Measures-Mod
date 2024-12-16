package com.virus5600.defensive_measures.model.entity;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;
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

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CannonTurretModel extends GeoModel<CannonTurretEntity> {
	///////////////////////
	// INTERFACE METHODS //
	///////////////////////

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

		EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);

		if (neck.isPresent()) {
			float targetYRot = (extraData.netHeadYaw() * ((float) Math.PI / 180F));
			neck.get().setRotY(targetYRot);

			if (head.isPresent()) {
				float targetXRot = (extraData.headPitch() * ((float) Math.PI / 180F));
				head.get().setRotX(targetXRot);
			}
		}

		if (base.isPresent()) {
			base.get().setRotX(0);
			base.get().setRotY(0);
			base.get().setRotZ(0);
		}
	}

	/*
	 * TODO: Consult Discord Servers for potential issue why this is thrown specifically when placed on Lightning Rods:
	 *  [09:11:45] [Netty Server IO #1/ERROR] (Minecraft) Error sending packet clientbound/minecraft:set_entity_data
	 */
}
