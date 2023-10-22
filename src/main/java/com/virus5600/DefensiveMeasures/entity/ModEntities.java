package com.virus5600.DefensiveMeasures.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.custom.AntiAirTurretEntity;
import com.virus5600.DefensiveMeasures.entity.custom.BallistaTurretEntity;
import com.virus5600.DefensiveMeasures.entity.custom.CannonTurretEntity;
import com.virus5600.DefensiveMeasures.entity.custom.MGTurretEntity;
import com.virus5600.DefensiveMeasures.entity.projectile.AntiAirProjectileEntity;
import com.virus5600.DefensiveMeasures.entity.projectile.BallistaArrowEntity;
import com.virus5600.DefensiveMeasures.entity.projectile.CannonballEntity;
import com.virus5600.DefensiveMeasures.entity.projectile.MGBulletEntity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
	/// TURRETS
	// v1.0.0
	public static final EntityType<CannonTurretEntity> CANNON_TURRET = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "cannon_turret"),
		FabricEntityTypeBuilder
			.create(SpawnGroup.MISC, CannonTurretEntity::new)
			.dimensions(EntityDimensions.changing(0.875f, 0.875f))
			.build()
	);
	public static final EntityType<BallistaTurretEntity> BALLISTA = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "ballista"),
		FabricEntityTypeBuilder
			.create(SpawnGroup.MISC, BallistaTurretEntity::new)
			.dimensions(EntityDimensions.changing(0.875f, 0.875f))
			.build()
	);
	public static final EntityType<MGTurretEntity> MG_TURRET = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "mg_turret"),
		FabricEntityTypeBuilder
			.create(SpawnGroup.MISC, MGTurretEntity::new)
			.dimensions(EntityDimensions.changing(0.875f, 0.875f))
			.build()
	);
	// v1.1.0-beta
	public static final EntityType<AntiAirTurretEntity> ANTI_AIR_TURRET = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "anti_air_turret"),
		FabricEntityTypeBuilder
			.create(SpawnGroup.MISC, AntiAirTurretEntity::new)
			.dimensions(EntityDimensions.changing(0.875f, 0.875f))
			.build()
	);

	/// PROJECTILES
	// v1.0.0
	public static final EntityType<CannonballEntity> CANNONBALL = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "cannonball"),
		FabricEntityTypeBuilder
			.<CannonballEntity>create(SpawnGroup.MISC, CannonballEntity::new)
			.dimensions(EntityDimensions.fixed(0.125f, 0.125f))
			.build()
	);
	public static final EntityType<BallistaArrowEntity> BALLISTA_ARROW = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "ballista_arrow"),
		FabricEntityTypeBuilder
			.<BallistaArrowEntity>create(SpawnGroup.MISC, BallistaArrowEntity::new)
			.dimensions(EntityDimensions.fixed(0.125f, 0.125f))
			.build()
	);
	public static final EntityType<MGBulletEntity> MG_BULLET = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "mg_bullet"),
		FabricEntityTypeBuilder
			.<MGBulletEntity>create(SpawnGroup.MISC, MGBulletEntity::new)
			.dimensions(EntityDimensions.fixed(0.125f, 0.125f))
			.build()
	);

	// v1.1.0-beta
	public static final EntityType<AntiAirProjectileEntity> ANTI_AIR_PROJECTILE = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "anti_air_projectile"),
		FabricEntityTypeBuilder
			.<AntiAirProjectileEntity>create(SpawnGroup.MISC, AntiAirProjectileEntity::new)
			.dimensions(EntityDimensions.fixed(0.125f, 0.125f))
			.build()
	);

	public static void registerModEntityAttributes() {
		DefensiveMeasures.LOGGER.debug("REGISTERING ENTITY ATTRIBUTES FOR " + DefensiveMeasures.MOD_NAME);

		/// TURRETS
		// v1.0.0
		FabricDefaultAttributeRegistry.register(ModEntities.CANNON_TURRET, CannonTurretEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BALLISTA, BallistaTurretEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MG_TURRET, MGTurretEntity.setAttributes());

		// v1.1.0
		FabricDefaultAttributeRegistry.register(ModEntities.ANTI_AIR_TURRET, AntiAirTurretEntity.setAttributes());
	}
}