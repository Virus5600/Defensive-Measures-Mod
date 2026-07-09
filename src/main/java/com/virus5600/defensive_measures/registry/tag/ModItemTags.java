package com.virus5600.defensive_measures.registry.tag;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * A class containing all the item tags used in the Defensive Measures mod.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public final class ModItemTags {
	public static final TagKey<Item> TIER_0_TURRETS = RegistryHelper.createItemTagKey("tier_0_turrets");
	public static final TagKey<Item> TIER_1_TURRETS = RegistryHelper.createItemTagKey("tier_1_turrets");
	public static final TagKey<Item> TIER_2_TURRETS = RegistryHelper.createItemTagKey("tier_2_turrets");
	public static final TagKey<Item> TURRETS = RegistryHelper.createItemTagKey("turrets");
	public static final TagKey<Item> TURRET_REMOVERS = RegistryHelper.createItemTagKey("turret_removers");
	public static final TagKey<Item> TURRET_REMOVER_REPAIRABLE = RegistryHelper.createItemTagKey("turret_remover_repairable");
}
