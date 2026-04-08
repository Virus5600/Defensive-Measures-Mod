package com.virus5600.defensive_measures.entity;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.virus5600.defensive_measures.entity.projectiles.*;
import com.virus5600.defensive_measures.entity.turrets.*;
import com.virus5600.defensive_measures._helper.RegistryHelper;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.SpawnGroup;

/**
 * Register and store all custom entities for the mod.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModEntities {
	// TURRETS //
	// v1.0.0
	public static final EntityType<CannonTurretEntity> CANNON_TURRET = RegistryHelper.registerEntity(
		"cannon_turret",
		Builder
			.create(CannonTurretEntity::new, SpawnGroup.MISC)
			.dimensions(1F, 1F)
			.eyeHeight(0.51F)
	);

	public static final EntityType<BallistaTurretEntity> BALLISTA_TURRET = RegistryHelper.registerEntity(
		"ballista",
		Builder
			.create(BallistaTurretEntity::new, SpawnGroup.MISC)
			.dimensions(1F, 1F)
			.eyeHeight(0.71F)
	);

	public static final EntityType<MGTurretEntity> MG_TURRET = RegistryHelper.registerEntity(
		"mg_turret",
		Builder
			.create(MGTurretEntity::new, SpawnGroup.MISC)
			.dimensions(1F, 0.625F)
			.eyeHeight(0.4275F)
	);

	// PROJECTILES //
	// v1.0.0
	public static final EntityType<CannonballEntity> CANNONBALL = RegistryHelper.registerEntity(
		"cannonball",
		Builder
			.<CannonballEntity>create(CannonballEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.5f, 0.5f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
			.eyeHeight(0.25F)
	);

	public static final EntityType<BallistaArrowEntity> BALLISTA_ARROW = RegistryHelper.registerEntity(
		"ballista_arrow",
		Builder
			.<BallistaArrowEntity>create(BallistaArrowEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.125f, 0.125f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);

	public static final EntityType<MGBulletEntity> MG_BULLET = RegistryHelper.registerEntity(
		"mg_bullet",
		Builder
			.<MGBulletEntity>create(MGBulletEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.125f, 0.125f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);

	// REGISTRY //
	@SuppressWarnings("ConstantConditions")
	public static void registerModEntityAttributes() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY ATTRIBUTES FOR {}...", DefensiveMeasures.MOD_NAME);

		// TURRETS //
		// v1.0.0
		FabricDefaultAttributeRegistry.register(ModEntities.CANNON_TURRET, CannonTurretEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BALLISTA_TURRET, BallistaTurretEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MG_TURRET, MGTurretEntity.setAttributes());
	}
}
