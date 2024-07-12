package com.virus5600.defensive_measures.entity.turrets;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;

/**
 * An interface that serves as a base for all entities that can be stored as an items.
 *
 * @author Virus5600
 * @version 1.0.0
 */
public interface Itemable {
	/**
	 * Determines whether this entity is from an item.
	 * @return {@link byte}
	 */
	public byte isFromItem();

	/**
	 * Sets the value that identifies whether this entity is from an item.
	 * @param fromItem {@link byte} A `1` or `0` value.
	 */
	public void setFromItem(byte fromItem);

	/**
	 * Copies the data to the given stack.
	 * @param stack {@link ItemStack} The stack to copy the data to.
	 */
	public void copyDataToStack(ItemStack stack);

	/**
	 * Copies the data from the given NBT to this entity.
	 * @param nbt {@link NbtCompound} The NBT to copy the data from.
	 */
	public void copyDataFromNbt(NbtCompound nbt);

	/**
	 * Retrieves the item this turret is from.
	 * @return {@link ItemStack}
	 */
	public ItemStack getEntityItem();

	/**
	 * Retrieves the sound that plays when the turret is removed.
	 * @return {@link SoundEvent}
	 */
	public SoundEvent getEntityRemoveSound();

	/**
	 * Copies the data from this entity to the given stack.
	 *
	 * @param entity {@link MobEntity} The entity to copy the data from.
	 * @param stack {@link ItemStack} The stack to copy the data to.
	 */
	public static void copyDataToStack(MobEntity entity, ItemStack stack) {
		// Sets the name
		stack.set(DataComponentTypes.CUSTOM_NAME, entity.hasCustomName() ? entity.getCustomName() : stack.getName());

		NbtCompound nbtCompound = new NbtCompound();

		// Sets the NBT
		nbtCompound.putBoolean("NoAI", entity.isAiDisabled());
		nbtCompound.putBoolean("Silent", entity.isSilent());
		nbtCompound.putBoolean("NoGravity", entity.hasNoGravity());
		nbtCompound.putBoolean("Glowing", entity.isGlowing());
		nbtCompound.putBoolean("Invulnerable", entity.isInvulnerable());
		nbtCompound.putFloat("Health", entity.getHealth());
		nbtCompound.putUuid("UUID", entity.getUuid());

		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCompound));
	}

	/**
	 * Copies the data from the given NBT to this entity.
	 *
	 * @param entity {@link MobEntity} The entity to copy the data to.
	 * @param nbt {@link NbtCompound} The NBT to copy the data from.
	 */
	public static void copyDataFromNbt(MobEntity entity, NbtCompound nbt) {
		NbtComponent.of(nbt).applyToEntity(entity);
	}
}
