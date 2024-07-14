package com.virus5600.defensive_measures.entity;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
	// TURRETS //
	// v1.0.0
	public static final EntityType<CannonTurretEntity> CANNON_TURRET = Registry.register(
		Registries.ENTITY_TYPE,
		Identifier.of(DefensiveMeasures.MOD_ID, "cannon_turret"),
		Builder
			.create(CannonTurretEntity::new, SpawnGroup.MISC)
			.dimensions(1F, 1F)
			.build()
	);

//	public static final EntityType<BallistaTurretEntity> BALLISTA = Registry.register(
//		Registries.ENTITY_TYPE,
//		Identifier.of(DefensiveMeasures.MOD_ID, "ballista"),
//		Builder
//			.create(BallistaTurretEntity::new, SpawnGroup.MISC)
//			.dimensions(1F, 1F)
//			.build()
//	);
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
//	public static final EntityType<CannonballEntity> CANNONBALL = Registry.register(
//		Registries.ENTITY_TYPE,
//		Identifier.of(DefensiveMeasures.MOD_ID, "cannonball"),
//		Builder
//			.create(CannonballEntity::new, SpawnGroup.MISC)
//			.dimensions(EntityDimensions.fixed(0.125f, 0.125f))
//			.build()
//	);

//	public static final EntityType<BallistaArrowEntity> BALLISTA_ARROW = Registry.register(
//		Registries.ENTITY_TYPE,
//		Identifier.of(DefensiveMeasures.MOD_ID, "ballista_arrow"),
//		Builder
//			.create(BallistaArrowEntity::new, SpawnGroup.MISC)
//			.dimensions(EntityDimensions.fixed(0.125f, 0.125f))
//			.build()
//	);

//	public static final EntityType<MGBulletEntity> MG_BULLET = Registry.register(
//		Registries.ENTITY_TYPE,
//		Identifier.of(DefensiveMeasures.MOD_ID, "mg_bullet"),
//		Builder
//			.create(MGBulletEntity::new, SpawnGroup.MISC)
//			.dimensions(EntityDimensions.fixed(0.125f, 0.125f))
//			.build()
//	);

	// REGISTRY //
	public static void registerModEntityAttributes() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY ATTRIBUTES FOR {}...", DefensiveMeasures.MOD_NAME);

		// TURRETS //
		// v1.0.0
		FabricDefaultAttributeRegistry.register(ModEntities.CANNON_TURRET, CannonTurretEntity.setAttributes());
//		FabricDefaultAttributeRegistry.register(ModEntities.BALLISTA, BallistaTurretEntity.setAttributes());
//		FabricDefaultAttributeRegistry.register(ModEntities.MG_TURRET, MGTurretEntity.setAttributes());
	}
}
