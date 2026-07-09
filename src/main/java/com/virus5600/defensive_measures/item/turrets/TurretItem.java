package com.virus5600.defensive_measures.item.turrets;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

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
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class TurretItem extends Item {
	protected final EntityType<?> type;
	private static final Map<EntityType<? extends Mob>, TurretItem> TURRETS = new HashMap<>();


	public TurretItem(EntityType<? extends Mob> type, Properties settings) {
		super(
			settings
				.spawnEgg(type)
		);

		this.type = type;
		TURRETS.put(type, this);
	}

	@NonNull
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();

		// If the world is not the Server world
		if (!(world instanceof ServerLevel)) {
			return InteractionResult.SUCCESS;
		} else {
			ItemStack itemStack = context.getItemInHand();
			BlockPos blockPos = context.getClickedPos();
			Direction direction = context.getClickedFace();
			BlockState blockState = world.getBlockState(blockPos);
			BlockPos blockPos2;

			if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
				blockPos2 = blockPos;
			} else {
				blockPos2 = blockPos.relative(direction);
			}

			CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
			CompoundTag nbt = nbtComponent != null ? nbtComponent.copyTag() : CustomData.EMPTY.copyTag();
			EntityType<?> entityType2 = this.getEntityType(nbt);
			Entity entity = entityType2.spawn(
				(ServerLevel) world,
				itemStack,
				context.getPlayer(),
				blockPos2,
				EntitySpawnReason.SPAWN_ITEM_USE,
				true,
				!Objects.equals(blockPos, blockPos2) && direction == Direction.UP
			);

			if (entity != null) {
				this.applyNbt((Mob) entity, nbt);

				itemStack.shrink(1);
				world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
			}

			return InteractionResult.CONSUME;
		}
	}

	@NonNull
	public InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
        BlockHitResult hitResult = SpawnEggItem.getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos blockPos = hitResult.getBlockPos();

        if (!(world.getBlockState(blockPos).getBlock() instanceof LiquidBlock)) {
            return InteractionResult.PASS;
        }
        if (!world.mayInteract(user, blockPos) || !user.mayUseItemAt(blockPos, hitResult.getDirection(), itemStack)) {
            return InteractionResult.FAIL;
        }

		CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
		CompoundTag nbt = nbtComponent != null ? nbtComponent.copyTag() : CustomData.EMPTY.copyTag();
		EntityType<?> entityType = this.getEntityType(nbt);
        Entity entity = entityType.spawn((ServerLevel)world, itemStack, user, blockPos, EntitySpawnReason.SPAWN_ITEM_USE, false, false);
        if (entity == null) {
            return InteractionResult.PASS;
        }
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        world.gameEvent(user, GameEvent.ENTITY_PLACE, entity.getPositionCodec().getBase());

        return InteractionResult.CONSUME;
	}

	public boolean isOfSameEntityType(@Nullable CompoundTag nbt, EntityType<?> type) {
		return Objects.equals(this.getEntityType(nbt), type);
	}

	@Nullable
	public static TurretItem forEntity(@Nullable EntityType<?> type) {
		return TURRETS.get(type);
	}

	public static Iterable<TurretItem> getAll() {
		return TURRETS.values();
	}

	public EntityType<?> getEntityType(@Nullable CompoundTag nbt) {
		if (nbt != null && nbt.contains("EntityTag")) {
			CompoundTag nbtCompound = nbt.getCompound("EntityTag")
				.orElse(null);

			if (nbtCompound != null && nbtCompound.contains("id")) {
				String id = nbtCompound.getString("id")
					.orElse("");

				Optional<EntityType<?>> entityType = (
					id.isEmpty() ?
						Optional.of(this.type)
						: Optional.of(
							BuiltInRegistries.ENTITY_TYPE
							.getValue(Identifier.parse(id))
					)
				);

				return entityType.orElse(this.type);
			}
		}

		return this.type;
	}

	@Deprecated @Override
	public void appendHoverText(ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, @NonNull Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
		int maxHealth = (int) this.getTurretMaxHealth();
		int currentHealth = maxHealth;

		CustomData nbtComponent = stack.get(DataComponents.CUSTOM_DATA);
		CompoundTag nbt;
		if (nbtComponent != null) {
			nbt = nbtComponent.copyTag();

			if (nbt.contains("Health")) {
				currentHealth = (int) nbt.getFloatOr("Health", 0.0F);
			}

			if (nbt.contains("MaxHealth")) {
				maxHealth = (int) nbt.getFloatOr("MaxHealth", 0.0F);
			}
		}

		double healthPercent = ((double) currentHealth / (double) maxHealth) * 100;

		if (maxHealth != 0) {
			textConsumer.accept(
				Component.translatable(
					"itemTooltip.dm.generic.health",
					currentHealth, maxHealth, healthPercent
				).withStyle(ChatFormatting.RED)
			);
		}
	}

	/**
	 * Sets custom data from the NBT Compound to the entity, allowing the old turret data
	 * to carry over to the new one this item will spawn.
	 * When overriding this method, this method must be called within the override method
	 * so that the following data could be set properly:
	 * <ul>
	 *     <li>{@code TurretLevel}</li>
	 *     <li>{@code NoAI}</li>
	 *     <li>{@code Silent}</li>
	 *     <li>{@code NoGravity}</li>
	 *     <li>{@code Glowing}</li>
	 *     <li>{@code Invulnerable}</li>
	 *     <li>{@code Health}</li>
	 * </ul>
	 *
	 * @param entity {@link Mob} The entity to apply the NBT data to.
	 * @param nbt {@link CompoundTag} The NBT data to apply to the entity.
	 */
	protected void applyNbt(Mob entity, CompoundTag nbt) {
		if (nbt.contains("NoAI")) {
			entity.setNoAi(nbt.getBooleanOr("NoAI", false));
		}
		if (nbt.contains("Silent")) {
			entity.setSilent(nbt.getBooleanOr("Silent", false));
		}
		if (nbt.contains("NoGravity")) {
			entity.setNoGravity(nbt.getBooleanOr("NoGravity", false));
		}
		if (nbt.contains("Glowing")) {
			entity.setGlowingTag(nbt.getBooleanOr("Glowing", false));
		}
		if (nbt.contains("Invulnerable")) {
			entity.setInvulnerable(nbt.getBooleanOr("Invulnerable", false));
		}
		if (nbt.contains("MaxHealth")) {
			float maxHp = nbt.getFloatOr("MaxHealth", entity.getMaxHealth());
			AttributeInstance maxHealthAttr = entity.getAttribute(Attributes.MAX_HEALTH);

			if (maxHealthAttr != null) {
				maxHealthAttr.setBaseValue(maxHp);
			}
		}
		if (nbt.contains("Health")) {
			float max = entity.getMaxHealth();
			float hp = Math.min(nbt.getFloatOr("Health", max), max);
			entity.setHealth(hp);
		}
		if (nbt.contains("ActiveEffects")) {
			ListTag effectList = nbt.getList("ActiveEffects")
				.orElse(new ListTag());

			effectList.forEach(effect -> {
				// Guard against non-compound list elements
				if (!(effect instanceof CompoundTag effectNbt)) {
					return;
				}

				// Validate and parse the effect id
				String idString = effectNbt.getString("id").orElse(null);
				if (idString == null || idString.isEmpty()) {
					return;
				}

				Identifier effId;
				try {
					effId = Identifier.parse(idString);
				}
				// Skip invalid identifiers
				catch (IllegalArgumentException e) {
					return;
				}

				Holder<MobEffect> entry = entity.level()
					.registryAccess()
					.lookupOrThrow(Registries.MOB_EFFECT)
					.get(effId)
					.orElse(null);

				if (entry != null) {
					CompoundTag nbtCompound = effectNbt.getCompound("effect")
						.orElse(null);

					MobEffectInstance.CODEC
						.parse(NbtOps.INSTANCE, nbtCompound)
						.ifSuccess(entity::addEffect);
				}
			});
		}

		// Handling turret related stuff
		if (entity instanceof TurretEntity turretEntity) {
			if (nbt.contains("TurretLevel")) {
				turretEntity.setTrackedLevel(nbt.getIntOr("TurretLevel", 1));
			}
		}
	}

	protected abstract float getTurretMaxHealth();
}
