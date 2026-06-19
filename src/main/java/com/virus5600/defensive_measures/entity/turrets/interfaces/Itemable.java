package com.virus5600.defensive_measures.entity.turrets.interfaces;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures.advancement.criterion.ModCriterion;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import java.util.Optional;
import java.util.Random;

/**
 * An interface that serves as a base for all entities that can be stored as an items.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface Itemable {
	/**
	 * Determines whether this entity is from an item.
	 * @return {@code byte}
	 */
	byte isFromItem();

	/**
	 * Sets the value that identifies whether this entity is from an item.
	 * @param fromItem {@code byte} A `1` or `0` value.
	 */
	void setFromItem(byte fromItem);

	/**
	 * Copies the data to the given stack.
	 * @param stack {@link ItemStack} The stack to copy the data to.
	 */
	void copyDataToStack(ItemStack stack);

	/**
	 * Copies the data from the given NBT to this entity.
	 * @param nbt {@link CompoundTag} The NBT to copy the data from.
	 */
	void copyDataFromNbt(CompoundTag nbt);

	/**
	 * Retrieves the item this turret is from.
	 * @return {@link ItemStack}
	 */
	ItemStack getEntityItem();

	/**
	 * Retrieves the sound that plays when the turret is removed.
	 * @return {@link SoundEvent}
	 */
	SoundEvent getEntityRemoveSound();

	/**
	 * Copies the data from this entity to the given stack.
	 *
	 * @param entity {@link Mob} The entity to copy the data from.
	 * @param stack {@link ItemStack} The stack to copy the data to.
	 */
	static void copyDataToStack(Mob entity, ItemStack stack) {
		// Sets the name
		stack.set(DataComponents.CUSTOM_NAME, entity.hasCustomName() ? entity.getCustomName() : stack.getHoverName());

		// Finalized NBT
		CompoundTag nbtCompound = new CompoundTag();
		// For storing active entity status effects
		ListTag effectList = new ListTag();

		entity.getActiveEffectsMap().forEach((entry, effectInstance) -> {
			CompoundTag effectNbt = new CompoundTag();

			// Store the effect key
			entry.unwrapKey().ifPresent(
				key -> effectNbt.putString("id", key.identifier().toString())
			);

			// Then store the effect instance
			effectNbt.put(
				"effect",
				MobEffectInstance.CODEC
					.encodeStart(NbtOps.INSTANCE, effectInstance)
					.getOrThrow()
			);

			// Only include effect nbt when not empty
			if (!effectNbt.isEmpty()) {
				effectList.add(effectNbt);
			}
		});

		// Sets the NBT
		nbtCompound.putBoolean("NoAI", entity.isNoAi());
		nbtCompound.putBoolean("Silent", entity.isSilent());
		nbtCompound.putBoolean("NoGravity", entity.isNoGravity());
		nbtCompound.putBoolean("Glowing", entity.isCurrentlyGlowing());
		nbtCompound.putBoolean("Invulnerable", entity.isInvulnerable());
		nbtCompound.putFloat("Health", entity.getHealth());
		nbtCompound.putFloat("MaxHealth", entity.getMaxHealth());
		nbtCompound.put("ActiveEffects",  effectList);

		// Handling turret related stuff
		if (entity instanceof TurretEntity turretEntity) {
			nbtCompound.putInt("TurretLevel", turretEntity.getTrackedLevel());
		}

		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtCompound));
	}

	/**
	 * Copies the data from the given NBT to this entity.
	 *
	 * @param entity {@link Mob} The entity to copy the data to.
	 * @param nbt {@link CompoundTag} The NBT to copy the data from.
	 */
	static void copyDataFromNbt(Mob entity, CompoundTag nbt) {
		entity.setComponent(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
	}

	/**
	 * Attempts to retrieve the {@link Itemable Itemable Entity} as an item. This will also
	 * automatically discard said entity if the {@code discard} parameter is set to {@code true}.
	 * However, if the passed value is {@code false}, the entity must be manually discarded.
	 *
	 * @param player The player that is attempting to retrieve the item.
	 * @param hand The hand the player is using to retrieve the item.
	 * @param entity The entity to retrieve the item from.
	 * @param tool The tool the player is using to retrieve the item.
	 * @param dropItem The item to retrieve.
	 * @param spawnItem Whether to spawn the item after retrieving the entity.
	 * @param discard Whether to discard the entity after retrieving the item.
	 *
	 * @return {@link Optional<InteractionResult>} The result of the action.
	 * @param <T> The type of entity.
	 */
	static <T extends TurretEntity> Optional<InteractionResult> tryItem(Player player, InteractionHand hand, T entity, Item tool, Item dropItem, boolean spawnItem, boolean discard) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (itemStack.getItem() == tool && entity.isAlive()) {
			Level world = entity.level();

			if (!world.isClientSide()) {
				entity.playSound(entity.getEntityRemoveSound(), 1.0f, new Random().nextFloat(0.75f, 1.25f));

				if (player.isCreative() && !player.isShiftKeyDown()) {
					if (discard) {
						entity.discard();
					}

					return Optional.of(InteractionResult.SUCCESS);
				}

				ItemStack stack = new ItemStack(dropItem);
				if (spawnItem) {
					ItemEntity itemStackEntity = tryItem(entity, dropItem, world);
					world.addFreshEntity(itemStackEntity);
				}

				if (discard) {
					entity.discard();
				}

				ModCriterion.TURRET_ITEM_RETRIEVED_CRITERION.trigger((ServerPlayer) player, stack);
			}

			return Optional.of(InteractionResult.SUCCESS);
		}
		return Optional.empty();
	}

	static <T extends TurretEntity> ItemEntity tryItem(T entity, Item dropItem, Level world) {
		ItemStack stack = new ItemStack(dropItem);
		entity.copyDataToStack(stack);

		Vec3 entityPos = entity.position();

		float x = (float) entityPos.x() + 0.5f;
		float y = (float) entityPos.y() + 0.5f;
		float z = (float) entityPos.z() + 0.5f;
		double vx = Mth.nextDouble(world.getRandom(), -0.1, 0.1);
		double vy = Mth.nextDouble(world.getRandom(), 0.0, 0.1);
		double vz = Mth.nextDouble(world.getRandom(), -0.1, 0.1);

		return new ItemEntity(world, x, y, z, stack, vx, vy, vz);
	}
}
