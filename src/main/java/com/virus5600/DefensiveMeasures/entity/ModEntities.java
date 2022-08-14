package com.virus5600.DefensiveMeasures.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.renderer.CannonTurretRenderer;
import com.virus5600.DefensiveMeasures.entity.client.model.renderer.CannonballRenderer;
import com.virus5600.DefensiveMeasures.entity.custom.CannonTurretEntity;
import com.virus5600.DefensiveMeasures.entity.projectile.CannonballEntity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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
			.dimensions(EntityDimensions.fixed(1f, 1f))
			.build()
	);
	
	/// PROJECTILES
	// v1.0.0
	public static final EntityType<CannonballEntity> CANNONBALL = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(DefensiveMeasures.MOD_ID, "cannonball"),
		FabricEntityTypeBuilder
			.<CannonballEntity>create(SpawnGroup.MISC, CannonballEntity::new)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.build()
	);
	
	public static void registerModEntities() {
		DefensiveMeasures.LOGGER.debug("REGISTERING ENTITIES FOR " + DefensiveMeasures.MOD_NAME);
		
		/// TURRETS
		// v1.0.0
		EntityRendererRegistry.register(ModEntities.CANNON_TURRET, CannonTurretRenderer::new);
		FabricDefaultAttributeRegistry.register(ModEntities.CANNON_TURRET, CannonTurretEntity.setAttributes());
		
		/// PROJECTILES
		// v1.0.0
		EntityRendererRegistry.register(ModEntities.CANNONBALL, CannonballRenderer::new);
	}
}