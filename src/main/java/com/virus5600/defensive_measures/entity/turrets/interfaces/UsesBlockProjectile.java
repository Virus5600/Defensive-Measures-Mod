package com.virus5600.defensive_measures.entity.turrets.interfaces;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager;
import com.virus5600.defensive_measures.entity.projectiles.BlockProjectileEntity;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
	 * a block projectile.
	 * <br><br>
	 * While this method is an instance method and does not use a lot of resources, it is still
	 * recommended to override this method and add a static field that hold the first instance of
	 * the returened value to minimize resource usage.
	 *
	 * @return The item tag representing the allowed (ammo) items for this turret.
	 *
	 * @implNote It is recommended to override this method and add a static field that holds the
	 * first instance of the returned value to minimize resource usage, as this method is called
	 * frequently during gameplay.
	 */
	@Nullable
	default TagKey<Item> getAllowedItems() {
		if (this instanceof TurretEntity turret) {
			Identifier turretId = BuiltInRegistries.ENTITY_TYPE
				.getKey(turret.getType());

			String tagKey = "turret_item_ammo/" + turretId.getPath() + "_ammo";

			return RegistryHelper.createItemTagKey(
				turretId.getNamespace(),
				tagKey
			);
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
		return this.getAllowedItems() != null && RegistryHelper.isOf(item, this.getAllowedItems());
	}

	/**
	 * Resolves the block that the given ammo item places, so it can be used as the fired
	 * projectile's visual/identity and looked up in {@code BlockProjectileConfigManager}.
	 * Only {@link BlockItem}-backed ammo is supported — anything else resolves to an empty
	 * {@link Optional} rather than throwing, consistent with this system's "skip malformed,
	 * don't crash" handling elsewhere.
	 *
	 * @param ammo The ammo item to resolve a block from.
	 *
	 * @return The block this ammo item places, or empty if the item isn't a {@link BlockItem}.
	 */
	default Optional<Block> resolveBlock(Item ammo) {
		if (ammo instanceof BlockItem blockItem) {
			return Optional.of(blockItem.getBlock());
		}
		return Optional.empty();
	}
}
