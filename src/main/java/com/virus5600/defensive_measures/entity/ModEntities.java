package com.virus5600.defensive_measures.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.entity.projectiles.*;
import com.virus5600.defensive_measures.entity.turrets.tier1.*;
import com.virus5600.defensive_measures.entity.turrets.tier2.*;

/**
 * Register and store all custom entities for the mod.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModEntities {
	// TURRETS //
	// v1.0.0-beta
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

	// v1.1.0-beta
	public static final EntityType<AATurretEntity> AA_TURRET = RegistryHelper.registerEntity(
		"aa_turret",
		Builder.
			create(AATurretEntity::new, SpawnGroup.MISC)
			.dimensions(2F, 2F)
			.eyeHeight(0.96875F)
	);

	public static final EntityType<FlameTurretEntity> FLAME_TURRET = RegistryHelper.registerEntity(
		"flame_turret",
		Builder.
			create(FlameTurretEntity::new, SpawnGroup.MISC)
			.dimensions(2F, 2F)
			.eyeHeight(1.1875F)
	);

	// PROJECTILES //
	// v1.0.0-beta
	public static final EntityType<CannonballEntity> CANNONBALL = RegistryHelper.registerEntity(
		"cannonball",
		Builder
			.create(CannonballEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.5f, 0.5f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
			.eyeHeight(0.25F)
	);

	public static final EntityType<BallistaBoltEntity> BALLISTA_BOLT = RegistryHelper.registerEntity(
		"ballista_bolt",
		Builder
			.<BallistaBoltEntity>create(BallistaBoltEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.5f, 0.5f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
			.eyeHeight(0.1F)
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

	// v1.1.0-beta
	public static final EntityType<FlakProjectileEntity> FLAK_PROJECTILE = RegistryHelper.registerEntity(
		"flak_projectile",
		Builder
			.<FlakProjectileEntity>create(FlakProjectileEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.125f, 0.125f)
			.maxTrackingRange(8)
			.trackingTickInterval(10)
	);

	public static final EntityType<FlammableAerosolEntity> FLAMMABLE_AEROSOL = RegistryHelper.registerEntity(
		"flammable_aerosol",
		Builder
			.create(FlammableAerosolEntity::new, SpawnGroup.MISC)
			.dropsNothing()
			.dimensions(0.5f, 0.5f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
			.makeFireImmune()

	);

	// REGISTRY //
	@SuppressWarnings("ConstantConditions")
	public static void registerModEntityAttributes() {
		DefensiveMeasures.LOGGER.info("REGISTERING ENTITY ATTRIBUTES FOR {}...", DefensiveMeasures.MOD_NAME);

		// TURRETS //
		// v1.0.0-beta
		FabricDefaultAttributeRegistry.register(ModEntities.CANNON_TURRET, CannonTurretEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BALLISTA_TURRET, BallistaTurretEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MG_TURRET, MGTurretEntity.setAttributes());

		// v1.1.0-beta
		FabricDefaultAttributeRegistry.register(ModEntities.AA_TURRET, AATurretEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.FLAME_TURRET, FlameTurretEntity.setAttributes());
	}
}
