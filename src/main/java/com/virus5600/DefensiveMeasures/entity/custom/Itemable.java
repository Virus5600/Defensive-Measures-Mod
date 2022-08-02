package com.virus5600.DefensiveMeasures.entity.custom;

import java.util.Optional;
import java.util.Random;

import com.virus5600.DefensiveMeasures.advancement.criterion.ModCriterion;
import com.virus5600.DefensiveMeasures.entity.TurretMaterial;
import com.virus5600.DefensiveMeasures.sound.ModSoundEvents;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public interface Itemable {
	public byte isFromItem();

    public void setFromItem(byte fromItem);

    public void copyDataToStack(ItemStack stack);

    public void copyDataFromNbt(NbtCompound nbt);

    /**
     * Retrieves the item this turret is from.
     * @return {@link ItemStack}
     */
    public ItemStack getEntityItem();

    public SoundEvent getTurretRemoveSound();
    
    @Deprecated
    public static void copyDataToStack(MobEntity entity, ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (entity.hasCustomName()) {
            stack.setCustomName(entity.getCustomName());
        }
        if (entity.isAiDisabled()) {
            nbtCompound.putBoolean("NoAI", entity.isAiDisabled());
        }
        if (entity.isSilent()) {
            nbtCompound.putBoolean("Silent", entity.isSilent());
        }
        if (entity.hasNoGravity()) {
            nbtCompound.putBoolean("NoGravity", entity.hasNoGravity());
        }
        if (entity.isGlowingLocal()) {
            nbtCompound.putBoolean("Glowing", entity.isGlowingLocal());
        }
        if (entity.isInvulnerable()) {
            nbtCompound.putBoolean("Invulnerable", entity.isInvulnerable());
        }
        nbtCompound.putFloat("Health", entity.getHealth());
    }

    @Deprecated
    public static void copyDataFromNbt(MobEntity entity, NbtCompound nbt) {
        if (nbt.contains("NoAI")) {
            entity.setAiDisabled(nbt.getBoolean("NoAI"));
        }
        if (nbt.contains("Silent")) {
            entity.setSilent(nbt.getBoolean("Silent"));
        }
        if (nbt.contains("NoGravity")) {
            entity.setNoGravity(nbt.getBoolean("NoGravity"));
        }
        if (nbt.contains("Glowing")) {
            entity.setGlowing(nbt.getBoolean("Glowing"));
        }
        if (nbt.contains("Invulnerable")) {
            entity.setInvulnerable(nbt.getBoolean("Invulnerable"));
        }
        if (nbt.contains("Health", NbtElement.NUMBER_TYPE)) {
            entity.setHealth(nbt.getFloat("Health"));
        }
    }
    
    public static <T extends LivingEntity> Optional<ActionResult> tryItem(PlayerEntity player, Hand hand, T entity, Item tool, Item modItem) {
    	ItemStack itemStack = player.getStackInHand(hand);
    	if (itemStack.getItem() == tool && entity.isAlive()) {
        	World world = entity.world;
        	
        	if (!world.isClient) {
	        	if (((TurretEntity) entity).getTurretMaterial() == TurretMaterial.METAL) {
	        		entity.playSound(ModSoundEvents.TURRET_REMOVED_METAL, 1.0f, new Random().nextFloat(0.75f, 1.25f));
	        	}
	        	else if (((TurretEntity) entity).getTurretMaterial() == TurretMaterial.WOOD) {
	        		entity.playSound(ModSoundEvents.TURRET_REMOVED_WOOD, 1.0f, new Random().nextFloat(0.75f, 1.25f));
	        	}
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
                ModCriterion.TURRET_ITEM_RETRIEVED_CRITERION.trigger((ServerPlayerEntity)player, stack);
            }
            
            entity.discard();
            return Optional.of(ActionResult.success(world.isClient));
        }
        return Optional.empty();
    }
}