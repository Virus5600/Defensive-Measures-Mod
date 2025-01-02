package com.virus5600.defensive_measures.entity.turrets;

import com.virus5600.defensive_measures.advancement.criterion.ModCriterion;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

/**
 * An interface that serves as a base for all entities that can be stored as an items.
 *
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
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

	/**
	 * Attempts to retrieve the {@link Itemable Itemable Entity} as an item.
	 * @param player The player that is attempting to retrieve the item.
	 * @param hand The hand the player is using to retrieve the item.
	 * @param entity The entity to retrieve the item from.
	 * @param tool The tool the player is using to retrieve the item.
	 * @param modItem The item to retrieve.
	 * @return {@link Optional<ActionResult>} The result of the action.
	 * @param <T> The type of entity.
	 */
	public static <T extends LivingEntity> Optional<ActionResult> tryItem(PlayerEntity player, Hand hand, T entity, Item tool, Item modItem) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == tool && entity.isAlive()) {
			World world = entity.getWorld();

			if (!world.isClient) {
				if (((TurretEntity) entity).getTurretMaterial() == TurretMaterial.METAL) {
					entity.playSound(ModSoundEvents.TURRET_REMOVED_METAL, 1.0f, new Random().nextFloat(0.75f, 1.25f));
				}
				else if (((TurretEntity) entity).getTurretMaterial() == TurretMaterial.WOOD) {
					entity.playSound(ModSoundEvents.TURRET_REMOVED_WOOD, 1.0f, new Random().nextFloat(0.75f, 1.25f));
				}
			}

			if (player.isCreative() && !player.isSneaking()) {
				entity.discard();
				return Optional.of(ActionResult.SUCCESS);
			}

			ItemStack stack = new ItemStack(modItem);
			((Itemable) entity).copyDataToStack(stack);

			float x = (float) entity.getPos().x + 0.5f;
			float y = (float) entity.getPos().y + 0.5f;
			float z = (float) entity.getPos().z + 0.5f;
			double vx = MathHelper.nextDouble(world.random, -0.1, 0.1);
			double vy = MathHelper.nextDouble(world.random, 0.0, 0.1);
			double vz = MathHelper.nextDouble(world.random, -0.1, 0.1);

			entity.discard();
			ItemEntity itemStackEntity = new ItemEntity(world, x, y, z, stack, vx, vy, vz);
			world.spawnEntity(itemStackEntity);

			if (!world.isClient) {
				ModCriterion.TURRET_ITEM_RETRIEVED_CRITERION.trigger((ServerPlayerEntity) player, stack);
			}

			entity.discard();
			return Optional.of(ActionResult.SUCCESS);
		}
		return Optional.empty();
	}
}
