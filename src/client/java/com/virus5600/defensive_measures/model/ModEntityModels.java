package com.virus5600.defensive_measures.model;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.model.entity.*;
import com.virus5600.defensive_measures.model.projectiles.*;

import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

public class ModEntityModels {

	private static final Set<ModelLayerLocation> LAYERS = Sets.newHashSet();

	// /////// //
	// TURRETS //
	// /////// //

	// v1.0.0-beta
	public static final ModelLayerLocation CANNON_TURRET = registerMain("cannon_turret");
	public static final ModelLayerLocation BALLISTA_TURRET = registerMain("ballista");
	public static final ModelLayerLocation MG_TURRET = registerMain("mg_turret");

	// v1.1.0-beta
	public static final ModelLayerLocation AA_TURRET = registerMain("aa_turret");
	public static final ModelLayerLocation FLAME_TURRET = registerMain("flame_turret");
	public static final ModelLayerLocation MISSILE_TURRET = registerMain("missile_turret");

	// /////////// //
	// PROJECTILES //
	// /////////// //

	// v1.0.0-beta
	public static final ModelLayerLocation CANNONBALL = registerMain("cannonball");
	public static final ModelLayerLocation BALLISTA_BOLT = registerMain("ballista_bolt");
	public static final ModelLayerLocation MG_BULLET = registerMain("mg_bullet");

	// v1.1.0-beta
	public static final ModelLayerLocation FLAK_PROJECTILE = registerMain("flak_projectile");
	public static final ModelLayerLocation FLAMMABLE_AEROSOL = registerMain("flammable_aerosol");
	public static final ModelLayerLocation MICRO_MISSILE = registerMain("micro_missile");

	public static void registerEntityModels() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY MODELS FOR {}...", DefensiveMeasures.MOD_NAME);

		// /////// //
		// TURRETS //
		// /////// //

		// v1.0.0-beta
		ModelLayerRegistry.registerModelLayer(CANNON_TURRET, CannonTurretModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(BALLISTA_TURRET, BallistaTurretModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(MG_TURRET, MGTurretModel::getTexturedModelData);

		// v1.1.0-beta
		ModelLayerRegistry.registerModelLayer(AA_TURRET, AATurretModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(FLAME_TURRET, FlameTurretModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(MISSILE_TURRET, MissileTurretModel::getTexturedModelData);

		// /////////// //
		// PROJECTILES //
		// /////////// //

		// v1.0.0-beta
		ModelLayerRegistry.registerModelLayer(CANNONBALL, CannonballModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(BALLISTA_BOLT, BallistaBoltModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(MG_BULLET, MGBulletModel::getTexturedModelData);

		// v1.1.0-beta
		ModelLayerRegistry.registerModelLayer(FLAK_PROJECTILE, FlakProjectileModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(FLAMMABLE_AEROSOL, FlammableAerosolModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(MICRO_MISSILE, MicroMissileModel::getTexturedModelData);
	}

	private static ModelLayerLocation registerMain(String id) {
		return register(id, "main");
	}

	private static ModelLayerLocation register(String id, String layer) {
		ModelLayerLocation entityModelLayer = create(id, layer);
		if (!LAYERS.add(entityModelLayer)) {
			throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
		} else {
			return entityModelLayer;
		}
	}

	private static ModelLayerLocation create(String id, String layer) {
		return new ModelLayerLocation(Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, id), layer);
	}

	private static ArmorModelSet<ModelLayerLocation> registerEquipment(String id) {
		return new ArmorModelSet<>(register(id, "helmet"), register(id, "chestplate"), register(id, "leggings"), register(id, "boots"));
	}

	public static ModelLayerLocation createStandingSign(WoodType type) {
		return create("sign/standing/" + type.name(), "main");
	}

	public static ModelLayerLocation createWallSign(WoodType type) {
		return create("sign/wall/" + type.name(), "main");
	}

	public static ModelLayerLocation createHangingSign(WoodType type, HangingSignBlock.Attachment attachmentType) {
		return create("hanging_sign/" + type.name() + "/" + attachmentType.getSerializedName(), "main");
	}

	public static Stream<ModelLayerLocation> getLayers() {
		return LAYERS.stream();
	}
}
