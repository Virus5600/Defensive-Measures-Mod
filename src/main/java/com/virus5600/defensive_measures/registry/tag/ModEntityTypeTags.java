package com.virus5600.defensive_measures.registry.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.tag.TagKey;

import com.virus5600.defensive_measures._helper.RegistryHelper;

public class ModEntityTypeTags {
	public static final TagKey<EntityType<?>> FLYING_HOSTILES = RegistryHelper.createEntityTypeTagKey("flying_hostiles");
	public static final TagKey<EntityType<?>> PROJECTILES = RegistryHelper.createEntityTypeTagKey("projectiles");
	public static final TagKey<EntityType<?>> TURRETS = RegistryHelper.createEntityTypeTagKey("turrets");
	public static final TagKey<EntityType<?>> TIER_1_TURRETS = RegistryHelper.createEntityTypeTagKey("tier_1_turrets");
	public static final TagKey<EntityType<?>> TIER_2_TURRETS = RegistryHelper.createEntityTypeTagKey("tier_2_turrets");
}
