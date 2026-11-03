package com.virus5600.defensive_measures.entity.turrets.interfaces;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager;
import com.virus5600.defensive_measures.entity.projectiles.BlockProjectileEntity;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * An interface that provides a contract for entities that use block projectiles. This interface can
 * be implemented by any turret entity that shoots blocks as projectiles, allowing for consistent
 * behavior and interaction with other systems in the mod.
 * <br><br>
 * Furthermore, this interface serves as a marker that this entity can be configured through config
 * files and item tags, allowing for a broad range of block projectiles to be used by the
 * turret and thus, customizations and flexibility in integrating modded blocks as projectiles.
 * <br><br>
 * With the inherent behavior and design that all turrets has a base damage and all by default, the
 * values dictated in the config file will all be implemented by adding them to the base damage,
 * allowing the turret to still scale up as it gets upgraded and/or level up. This solves the
 * issue of turrets that use block projectiles being too weak and not scaling up with upgrades or
 * leveling.
 *
 * @see BlockProjectileConfigManager
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @implNote This interface is only implementable on {@link TurretEntity Turret Entities}.
 */
public interface UsesBlockProjectile {
	/**
	 * A cache that maps turret entity identifiers to their corresponding allowed item tags. This
	 * cache is used to avoid recomputing the same values multiple times during gameplay.
	 */
	Map<Identifier, TagKey<Item>> ALLOWED_ITEMS_CACHE = Maps.newHashMap();

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	/**
	 * Gets the current ammo item that this turret entity is using to fire block projectiles.
	 *
	 * @return The current ammo item that this turret entity is using to fire block projectiles.
	 */
	Item getCurrentAmmo();

	/**
	 * Updates the current ammo of the turret entity based on the allowed items defined in the
	 * {@link #getAllowedItems()} method.
	 *
	 * @param item The item to update the current ammo with.
	 *
	 * @return {@code true} if the current ammo was successfully updated, {@code false} otherwise.
	 *
	 * @implNote This method should be called on the server side to ensure that the current ammo is
	 * properly synchronized with the client. Return {@code false} if called in the client.
	 */
	boolean updateCurrentAmmo(Item item);

	/**
	 * Gets the turret entity that implements this interface. Used by its {@link BlockProjectileEntity}
	 * companion class to let it know which turret entity fired it, allowing the lookup to
	 * {@link BlockProjectileConfigManager} to get the correct attributes for the block projectile.
	 *
	 * @return The turret entity that implements this interface.
	 */
	TurretEntity getTurretEntity();

	// /////////////// //
	// DEFAULT METHODS //
	// /////////////// //

	/**
	 * Gets the item tag representing the allowed (ammo) items that this turret can use to fire
	 * a block projectile. This method caches the allowed items for each turret entity type to
	 * avoid recomputing the same values multiple times during gameplay.
	 * <br><br>
	 * The tag key for the allowed items is generated based on the turret entity's identifier,
	 * following the format: {@code turret_item_ammo/<turret_id>_ammo}. For example, if the turret entity's
	 * identifier is {@code defensive_measures:ballista_turret}, the tag key will be
	 * {@code turret_item_ammo/ballista_turret_ammo}.
	 *
	 * @return The item tag representing the allowed (ammo) items for this turret.
	 *
	 * @implNote When overriding this method, it is recommended to retain the caching behavior to
	 * ensure optimal performance. If the allowed items are dynamic and can change during gameplay,
	 * consider implementing a mechanism to invalidate the cache when necessary.
	 */
	@Nullable
	default TagKey<Item> getAllowedItems() {
		if (this instanceof TurretEntity turret) {
			Identifier turretId = BuiltInRegistries.ENTITY_TYPE
				.getKey(turret.getType());

			if (ALLOWED_ITEMS_CACHE.containsKey(turretId)) {
				return ALLOWED_ITEMS_CACHE.get(turretId);
			}

			String tagKey = "turret_item_ammo/" + turretId.getPath() + "_ammo";
			ALLOWED_ITEMS_CACHE.putIfAbsent(
				turretId,
				RegistryHelper.createItemTagKey(
					turretId.getNamespace(),
					tagKey
				)
			);

			return ALLOWED_ITEMS_CACHE.get(turretId);
		}

		return null;
	}

	/**
	 * Checks if the given item is a valid ammo item for this turret entity. This method
	 * uses the {@link #getAllowedItems()} method to determine if the item is allowed as ammo.
	 *
	 * @param item The item to check if it is a valid ammo item for this turret entity.
	 *
	 * @return True if the item is a valid ammo item for this turret entity, false otherwise.
	 */
	default boolean isValidAmmo(Item item) {
		TagKey<Item> allowedItems = this.getAllowedItems();
		return allowedItems != null && RegistryHelper.isOf(item, allowedItems);
	}
}
