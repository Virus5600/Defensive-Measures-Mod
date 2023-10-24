package com.virus5600.DefensiveMeasures.item.turrets;

import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.virus5600.DefensiveMeasures.entity.custom.TurretEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class TurretItem extends Item {
	private static final Map<EntityType<? extends TurretEntity>, TurretItem> TURRETS = Maps.newIdentityHashMap();
	private final EntityType<?> type;

	public TurretItem(EntityType<? extends MobEntity> type, Settings settings) {
		super(settings);
		this.type = type;
	}

	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();

		// If the world is not the Server world
		if (!(world instanceof ServerWorld)) {
			return ActionResult.SUCCESS;
		} else {
			ItemStack itemStack = context.getStack();
			BlockPos blockPos = context.getBlockPos();
			Direction direction = context.getSide();
			BlockState blockState = world.getBlockState(blockPos);
			BlockPos blockPos2;

			if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
				blockPos2 = blockPos;
			} else {
				blockPos2 = blockPos.offset(direction);
			}

			EntityType<?> entityType2 = this.getEntityType(itemStack.getNbt());
			Entity entity = entityType2.spawnFromItemStack(
				(ServerWorld)world,
				itemStack,
				context.getPlayer(),
				blockPos2,
				SpawnReason.SPAWN_EGG,
				true,
				!Objects.equals(blockPos, blockPos2) && direction == Direction.UP
			);

			if (entity != null) {
				itemStack.decrement(1);
				world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
			}

			return ActionResult.CONSUME;
		}
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = SpawnEggItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);

        if (((HitResult) hitResult).getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        }
        if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(itemStack);
        }

        BlockHitResult blockHitResult = hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();

        if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return TypedActionResult.pass(itemStack);
        }
        if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
            return TypedActionResult.fail(itemStack);
        }

        EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
        Entity entity = entityType.spawnFromItemStack((ServerWorld)world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false);
        if (entity == null) {
            return TypedActionResult.pass(itemStack);
        }
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        world.emitGameEvent((Entity)user, GameEvent.ENTITY_PLACE, entity.getPos());

        return TypedActionResult.consume(itemStack);
	}

	public boolean isOfSameEntityType(@Nullable NbtCompound nbt, EntityType<?> type) {
		return Objects.equals(this.getEntityType(nbt), type);
	}

	@Nullable
	public static TurretItem forEntity(@Nullable EntityType<?> type) {
		return (TurretItem)TURRETS.get(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Iterable<TurretItem> getAll() {
		return Iterables.unmodifiableIterable((Iterable)TURRETS.values());
	}

	public EntityType<?> getEntityType(@Nullable NbtCompound nbt) {
		if (nbt != null && nbt.contains("EntityTag", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound = nbt.getCompound("EntityTag");
			if (nbtCompound.contains("id", NbtElement.STRING_TYPE)) {
				return EntityType.get(nbtCompound.getString("id")).orElse(this.type);
			}
		}

		return this.type;
	}
}