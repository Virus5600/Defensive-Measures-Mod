package com.virus5600.defensive_measures.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.WoodType;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.model.entity.*;
import com.virus5600.defensive_measures.model.projectiles.*;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.stream.Stream;

public class ModEntityModels {

	private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();

	// /////// //
	// TURRETS //
	// /////// //

	// v1.0.0
	public static final EntityModelLayer CANNON_TURRET = registerMain("cannon_turret");
	public static final EntityModelLayer BALLISTA_TURRET = registerMain("ballista");
	public static final EntityModelLayer MG_TURRET = registerMain("mg_turret");

	// /////////// //
	// PROJECTILES //
	// /////////// //

	// v1.0.0
	public static final EntityModelLayer CANNONBALL = registerMain("cannonball");
	public static final EntityModelLayer BALLISTA_BOLT = registerMain("ballista_bolt");
	public static final EntityModelLayer MG_BULLET = registerMain("mg_bullet");

	public static void registerEntityModels() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY MODELS FOR {}...", DefensiveMeasures.MOD_NAME);

		// /////// //
		// TURRETS //
		// /////// //

		// v1.0.0
		EntityModelLayerRegistry.registerModelLayer(CANNON_TURRET, CannonTurretModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(BALLISTA_TURRET, BallistaTurretModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MG_TURRET, MGTurretModel::getTexturedModelData);

		// /////////// //
		// PROJECTILES //
		// /////////// //

		// v1.0.0
		EntityModelLayerRegistry.registerModelLayer(CANNONBALL, CannonballModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(BALLISTA_BOLT, BallistaBoltModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MG_BULLET, MGBulletModel::getTexturedModelData);
	}

	private static EntityModelLayer registerMain(String id) {
		return register(id, "main");
	}

	private static EntityModelLayer register(String id, String layer) {
		EntityModelLayer entityModelLayer = create(id, layer);
		if (!LAYERS.add(entityModelLayer)) {
			throw new IllegalStateException("Duplicate registration for " + String.valueOf(entityModelLayer));
		} else {
			return entityModelLayer;
		}
	}

	private static EntityModelLayer create(String id, String layer) {
		return new EntityModelLayer(Identifier.ofVanilla(id), layer);
	}

	private static EquipmentModelData<EntityModelLayer> registerEquipment(String id) {
		return new EquipmentModelData(register(id, "helmet"), register(id, "chestplate"), register(id, "leggings"), register(id, "boots"));
	}

	public static EntityModelLayer createStandingSign(WoodType type) {
		return create("sign/standing/" + type.name(), "main");
	}

	public static EntityModelLayer createWallSign(WoodType type) {
		return create("sign/wall/" + type.name(), "main");
	}

	public static EntityModelLayer createHangingSign(WoodType type, HangingSignBlockEntityRenderer.AttachmentType attachmentType) {
		return create("hanging_sign/" + type.name() + "/" + attachmentType.asString(), "main");
	}

	public static Stream<EntityModelLayer> getLayers() {
		return LAYERS.stream();
	}
}
