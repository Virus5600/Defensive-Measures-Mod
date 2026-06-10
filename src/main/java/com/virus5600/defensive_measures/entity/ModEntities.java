package com.virus5600.defensive_measures.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

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
			.of(CannonTurretEntity::new, MobCategory.MISC)
			.sized(1F, 1F)
			.clientTrackingRange(32)
			.eyeHeight(0.51F)
	);

	public static final EntityType<BallistaTurretEntity> BALLISTA_TURRET = RegistryHelper.registerEntity(
		"ballista",
		Builder
			.of(BallistaTurretEntity::new, MobCategory.MISC)
			.sized(1F, 1F)
			.clientTrackingRange(16)
			.eyeHeight(0.71F)
	);

	public static final EntityType<MGTurretEntity> MG_TURRET = RegistryHelper.registerEntity(
		"mg_turret",
		Builder
			.of(MGTurretEntity::new, MobCategory.MISC)
			.sized(1F, 0.625F)
			.clientTrackingRange(32)
			.eyeHeight(0.4275F)
	);

	// v1.1.0-beta
	public static final EntityType<AATurretEntity> AA_TURRET = RegistryHelper.registerEntity(
		"aa_turret",
		Builder.
			of(AATurretEntity::new, MobCategory.MISC)
			.sized(2F, 2F)
			.clientTrackingRange(96)
			.eyeHeight(0.96875F)
	);

	public static final EntityType<FlameTurretEntity> FLAME_TURRET = RegistryHelper.registerEntity(
		"flame_turret",
		Builder.
			of(FlameTurretEntity::new, MobCategory.MISC)
			.sized(2F, 2F)
			.clientTrackingRange(16)
			.eyeHeight(1.1875F)
	);

	public static final EntityType<MissileTurretEntity> MISSILE_TURRET = RegistryHelper.registerEntity(
		"missile_turret",
		Builder.
                of(MissileTurretEntity::new, MobCategory.MISC)
			.sized(2F, 2F)
			.clientTrackingRange(64)
			.eyeHeight(1.5625F)
	);

	// PROJECTILES //
	// v1.0.0-beta
	public static final EntityType<CannonballEntity> CANNONBALL = RegistryHelper.registerEntity(
		"cannonball",
		Builder
			.of(CannonballEntity::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.5f, 0.5f)
			.clientTrackingRange(4)
			.updateInterval(10)
			.eyeHeight(0.25F)
	);

	public static final EntityType<BallistaBoltEntity> BALLISTA_BOLT = RegistryHelper.registerEntity(
		"ballista_bolt",
		Builder
			.<BallistaBoltEntity>of(BallistaBoltEntity::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.5f, 0.5f)
			.clientTrackingRange(4)
			.updateInterval(10)
			.eyeHeight(0.1F)
	);

	public static final EntityType<MGBulletEntity> MG_BULLET = RegistryHelper.registerEntity(
		"mg_bullet",
		Builder
			.<MGBulletEntity>of(MGBulletEntity::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.125f, 0.125f)
			.clientTrackingRange(4)
			.updateInterval(10)
	);

	// v1.1.0-beta
	public static final EntityType<FlakProjectileEntity> FLAK_PROJECTILE = RegistryHelper.registerEntity(
		"flak_projectile",
		Builder
			.of(FlakProjectileEntity::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.125f, 0.125f)
			.clientTrackingRange(8)
			.updateInterval(10)
	);

	public static final EntityType<FlammableAerosolEntity> FLAMMABLE_AEROSOL = RegistryHelper.registerEntity(
		"flammable_aerosol",
		Builder
			.of(FlammableAerosolEntity::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.5f, 0.5f)
			.clientTrackingRange(4)
			.updateInterval(10)
			.fireImmune()
	);

	public static final EntityType<MicroMissileEntity> MICRO_MISSILE = RegistryHelper.registerEntity(
		"micro_missile",
		Builder
			.of(MicroMissileEntity::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.125f, 0.125f)
			.clientTrackingRange(8)
			.updateInterval(10)
			.eyeHeight(0.0625f)
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
		FabricDefaultAttributeRegistry.register(ModEntities.MISSILE_TURRET, MissileTurretEntity.setAttributes());
	}
}
