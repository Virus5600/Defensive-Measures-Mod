package com.virus5600.defensive_measures.item.turrets;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import org.jetbrains.annotations.Nullable;

/**
 * {@code TurretItem} is an abstract class that acts nearly akin
 * to {@link SpawnEggItem} but for turrets.
 * <br><br>
 * The purpose of this class is to provide a common base for all
 * placeable turret items, which are used to spawn turrets in the
 * world.
 * <br><br>
 * This class is abstract and should be extended by all turret items.
 * <br><br>
 * For creating a regular item, use {@link Item} instead.
 *
 * @see Item
 * @see SpawnEggItem
 * @see com.virus5600.defensive_measures.entity.ModEntities ModEntities
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public abstract class TurretItem extends Item {
	protected final EntityType<?> type;
	private static final Map<EntityType<? extends MobEntity>, TurretItem> TURRETS = new HashMap<>();


	public TurretItem(EntityType<? extends MobEntity> type, net.minecraft.item.Item.Settings settings) {
		super(
			settings.translationKey(type.getTranslationKey())
		);

		this.type = type;
		TURRETS.put(type, this);
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

			NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
			NbtCompound nbt = nbtComponent != null ? nbtComponent.copyNbt() : NbtComponent.DEFAULT.copyNbt();
			EntityType<?> entityType2 = this.getEntityType(nbt);
			Entity entity = entityType2.spawnFromItemStack(
				(ServerWorld) world,
				itemStack,
				context.getPlayer(),
				blockPos2,
				SpawnReason.SPAWN_ITEM_USE,
				true,
				!Objects.equals(blockPos, blockPos2) && direction == Direction.UP
			);

			if (entity != null) {
				this.applyNbt((MobEntity) entity, nbt);

				itemStack.decrement(1);
				world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
			}

			return ActionResult.CONSUME;
		}
	}

	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = SpawnEggItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return ActionResult.PASS;
        }
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }

        BlockPos blockPos = hitResult.getBlockPos();

        if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return ActionResult.PASS;
        }
        if (!world.canEntityModifyAt(user, blockPos) || !user.canPlaceOn(blockPos, hitResult.getSide(), itemStack)) {
            return ActionResult.FAIL;
        }

		NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
		NbtCompound nbt = nbtComponent != null ? nbtComponent.copyNbt() : NbtComponent.DEFAULT.copyNbt();
		EntityType<?> entityType = this.getEntityType(nbt);
        Entity entity = entityType.spawnFromItemStack((ServerWorld)world, itemStack, user, blockPos, SpawnReason.SPAWN_ITEM_USE, false, false);
        if (entity == null) {
            return ActionResult.PASS;
        }
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getTrackedPosition().getPos());

        return ActionResult.CONSUME;
	}

	public boolean isOfSameEntityType(@Nullable NbtCompound nbt, EntityType<?> type) {
		return Objects.equals(this.getEntityType(nbt), type);
	}

	@Nullable
	public static TurretItem forEntity(@Nullable EntityType<?> type) {
		return TURRETS.get(type);
	}

	public static Iterable<TurretItem> getAll() {
		return TURRETS.values();
	}

	public EntityType<?> getEntityType(@Nullable NbtCompound nbt) {
		if (nbt != null && nbt.contains("EntityTag")) {
			NbtCompound nbtCompound = nbt.getCompound("EntityTag")
				.orElse(null);

			if (nbtCompound != null && nbtCompound.contains("id")) {
				String id = nbtCompound.getString("id")
					.orElse(null);

				return id != null ? EntityType.get(id).orElse(this.type) : this.type;
			}
		}

		return this.type;
	}

	@Override
	@Deprecated
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		int maxHealth = (int) this.getTurretMaxHealth();
		int currentHealth = maxHealth;

		NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
		NbtCompound nbt;
		if (nbtComponent != null) {
			nbt = nbtComponent.copyNbt();

			if (nbt.contains("Health")) {
				currentHealth = (int) nbt.getFloat("Health", 0.0F);
			}

			if (nbt.contains("MaxHealth")) {
				maxHealth = (int) nbt.getFloat("MaxHealth", 0.0F);
			}
		}

		if (maxHealth != 0) {
			textConsumer.accept(
				Text.translatable(
					"itemTooltip.dm.generic.health",
					currentHealth, maxHealth)
					.formatted(Formatting.RED)
			);
		}
	}

	/**
	 * Sets custom data from the NBT Compound to the entity, allowing the old turret data
	 * to carry over to the new one this item will spawn.
	 * When overriding this method, {@link TurretItem#applyNbt(MobEntity, NbtCompound)} must
	 * be called within the override method so that the following data could be set properly:
	 * <ul>
	 *     <li>{@code NoAI}</li>
	 *     <li>{@code Silent}</li>
	 *     <li>{@code NoGravity}</li>
	 *     <li>{@code Glowing}</li>
	 *     <li>{@code Invulnerable}</li>
	 *     <li>{@code Health}</li>
	 * </ul>
	 *
	 * @param entity {@link MobEntity} The entity to apply the NBT data to.
	 * @param nbt {@link NbtCompound} The NBT data to apply to the entity.
	 */
	protected void applyNbt(MobEntity entity, NbtCompound nbt) {
		if (nbt.contains("NoAI")) {
			entity.setAiDisabled(nbt.getBoolean("NoAI", false));
		}
		if (nbt.contains("Silent")) {
			entity.setSilent(nbt.getBoolean("Silent", false));
		}
		if (nbt.contains("NoGravity")) {
			entity.setNoGravity(nbt.getBoolean("NoGravity", false));
		}
		if (nbt.contains("Glowing")) {
			entity.setGlowing(nbt.getBoolean("Glowing", false));
		}
		if (nbt.contains("Invulnerable")) {
			entity.setInvulnerable(nbt.getBoolean("Invulnerable", false));
		}
		if (nbt.contains("MaxHealth")) {
			float maxHp = nbt.getFloat("MaxHealth", entity.getMaxHealth());
			EntityAttributeInstance maxHealthAttr = entity.getAttributeInstance(EntityAttributes.MAX_HEALTH);

			if (maxHealthAttr != null) {
				maxHealthAttr.setBaseValue(maxHp);
			}
		}
		if (nbt.contains("Health")) {
			float max = entity.getMaxHealth();
			float hp = Math.min(nbt.getFloat("Health", max), max);
			entity.setHealth(hp);
		}
		if (nbt.contains("ActiveEffects")) {
			NbtList effectList = nbt.getList("ActiveEffects")
				.orElse(new NbtList());

			effectList.forEach(effect -> {
				NbtCompound effectNbt = (NbtCompound) effect;
				Identifier effId = Identifier.of(
					effectNbt.getString("id").orElse("")
				);

				RegistryEntry<StatusEffect> entry = entity.getEntityWorld()
					.getRegistryManager()
					.getOrThrow(RegistryKeys.STATUS_EFFECT)
					.getEntry(effId)
					.orElse(null);

				if (entry != null) {
					NbtCompound nbtCompound = effectNbt.getCompound("effect")
						.orElse(null);

					StatusEffectInstance.CODEC
						.parse(NbtOps.INSTANCE, nbtCompound)
						.ifSuccess(entity::addStatusEffect);
				}
			});
		}
	}

	protected abstract float getTurretMaxHealth();
}
