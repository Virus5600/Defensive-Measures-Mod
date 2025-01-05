package com.virus5600.defensive_measures.entity;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.virus5600.defensive_measures.entity.projectiles.*;
import com.virus5600.defensive_measures.entity.turrets.*;
import com.virus5600.defensive_measures.util.RegistryUtil;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.SpawnGroup;

public class ModEntities {
	// TURRETS //
	// v1.0.0
	public static final EntityType<CannonTurretEntity> CANNON_TURRET = RegistryUtil.registerEntity(
		"cannon_turret",
		Builder
			.create(CannonTurretEntity::new, SpawnGroup.MISC)
			.dimensions(1F, 1F)
			.eyeHeight(0.51F)
	);

	public static final EntityType<BallistaTurretEntity> BALLISTA_TURRET = RegistryUtil.registerEntity(
		"ballista",
		Builder
			.create(BallistaTurretEntity::new, SpawnGroup.MISC)
			.dimensions(1F, 1F)
			.eyeHeight(0.75F)
	);
//
//	public static final EntityType<MGTurretEntity> MG_TURRET = Registry.register(
//		Registries.ENTITY_TYPE,
//		Identifier.of(DefensiveMeasures.MOD_ID, "mg_turret"),
//		Builder
//			.create(MGTurretEntity::new, SpawnGroup.MISC)
//			.dimensions(1F, 1F)
//			.build()
//	);

	// PROJECTILES //
	// v1.0.0
	public static final EntityType<CannonballEntity> CANNONBALL = RegistryUtil.registerEntity(
		"cannonball",
		Builder
			.<CannonballEntity>create(CannonballEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.125f, 0.125f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);

	public static final EntityType<BallistaArrowEntity> BALLISTA_ARROW = RegistryUtil.registerEntity(
		"ballista_arrow",
		Builder
			.<BallistaArrowEntity>create(BallistaArrowEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.125f, 0.125f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);

	public static final EntityType<BulletEntity> MG_BULLET = RegistryUtil.registerEntity(
		"mg_bullet",
		Builder
			.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.125f, 0.125f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);

	// REGISTRY //
	public static void registerModEntityAttributes() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY ATTRIBUTES FOR {}...", DefensiveMeasures.MOD_NAME);

		// TURRETS //
		// v1.0.0
		FabricDefaultAttributeRegistry.register(ModEntities.CANNON_TURRET, CannonTurretEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BALLISTA_TURRET, BallistaTurretEntity.setAttributes());
//		FabricDefaultAttributeRegistry.register(ModEntities.MG_TURRET, MGTurretEntity.setAttributes());
	}
}
