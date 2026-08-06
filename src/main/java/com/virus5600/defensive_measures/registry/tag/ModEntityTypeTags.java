package com.virus5600.defensive_measures.registry.tag;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * A class containing all the entity type tags used in the Defensive Measures mod.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModEntityTypeTags {
	// v1.0.0-beta
	public static final TagKey<EntityType<?>> TIER_0_TURRETS = RegistryHelper.createEntityTypeTagKey("tier_0_turrets");
	public static final TagKey<EntityType<?>> TIER_1_TURRETS = RegistryHelper.createEntityTypeTagKey("tier_1_turrets");
	public static final TagKey<EntityType<?>> TIER_2_TURRETS = RegistryHelper.createEntityTypeTagKey("tier_2_turrets");
	public static final TagKey<EntityType<?>> TURRETS = RegistryHelper.createEntityTypeTagKey("turrets");

	// v1.1.0-beta
	public static final TagKey<EntityType<?>> FLYING_HOSTILES = RegistryHelper.createEntityTypeTagKey("flying_hostiles");

	// v1.2.0-beta
	public static final TagKey<EntityType<?>> PROJECTILES = RegistryHelper.createEntityTypeTagKey("projectiles");
	public static final TagKey<EntityType<?>> HEAVY_ENTITIES = RegistryHelper.createEntityTypeTagKey("heavy_entities");
}
