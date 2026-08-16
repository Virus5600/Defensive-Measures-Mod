package com.virus5600.defensive_measures.entity.turrets.interfaces;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;


/**
 * An interface that provides a contract for entities that use block projectiles. This interface can
 * be implemented by any turret entity that shoots blocks as projectiles, allowing for consistent
 * behavior and interaction with other systems in the mod.
 * <br><br>
 * Furthermore, this interface serves as a marker that this entity can be configured through config
 * files and item tags, allowing for a broad range of block projectiles to be used by the
 * turret and thus, customizations and flexibility in integrating modded blocks as projectiles.
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
	 * Checks if the given item stack is a valid ammo item for this turret entity. This method
	 * uses the {@link #getAllowedItems()} method to determine if the item is allowed as ammo.
	 *
	 * @param item The item stack to check if it is a valid ammo item for this turret entity.
	 *
	 * @return True if the item is a valid ammo item for this turret entity, false otherwise.
	 */
	default boolean isValidAmmo(ItemStack item) {
		return RegistryHelper.getHolder(item.getItem())
			.is(this.getAllowedItems());
	}

	/**
	 * Resolves the block that the given ammo item places, so it can be used as the fired
	 * projectile's visual/identity and looked up in {@code BlockProjectileConfigManager}.
	 * Only {@link BlockItem}-backed ammo is supported — anything else resolves to an empty
	 * {@link Optional} rather than throwing, consistent with this system's "skip malformed,
	 * don't crash" handling elsewhere.
	 *
	 * @param ammo The ammo item stack to resolve a block from.
	 *
	 * @return The block this ammo item places, or empty if the item isn't a {@link BlockItem}.
	 */
	default Optional<Block> resolveBlock(ItemStack ammo) {
		if (ammo.getItem() instanceof BlockItem blockItem) {
			return Optional.of(blockItem.getBlock());
		}
		return Optional.empty();
	}

	// ///// //
	// ENUMS //
	// ///// //

	enum AttributeKey {
		DRAG("drag", Double.class),
		GRAVITY("gravity", Double.class),
		DAMAGE("damage", Double.class),
		PIERCING("piercing", Double.class),
		FIRE("fire", Boolean.class),
		EXPLOSIVE("explosive", Boolean.class),
		DETAILED_EXPLOSIVE("explosive", Map.class)
		;

		private final String id;
		private final Class<?> type;

		AttributeKey(String id, Class<?> type) {
			this.id = id;
			this.type = type;
		}

		public Class<?> getType() {
			return type;
		}

		@Override
		public String toString() {
			return id;
		}
	}

	enum ExplosiveAttributeKey {
		DAMAGE("damage", Double.class),
		RADIUS("radius", Double.class)
		;

		private final String id;
		private final Class<?> type;

		ExplosiveAttributeKey(String id, Class<?> type) {
			this.id = id;
			this.type = type;
		}

		public Class<?> getType() {
			return type;
		}

		@Override
		public String toString() {
			return id;
		}
	}

	// ////// //
	// RECORD //
	// ////// //

	record Attributes<T>(AttributeKey key, T value) {
	}
}
