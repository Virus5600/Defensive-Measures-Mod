package com.virus5600.DefensiveMeasures.entity.custom;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.TurretMaterial;
import com.virus5600.DefensiveMeasures.item.ModItems;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity.PassiveData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * The mob base for all the <b>Turrets</b> that will be added into this mod. The
 * {@code TurretEntity} extends to the base code of {@code MobEntity}, the same
 * superclass used by various mobs that has the same trait as this. The custom
 * interface {@code Itemable} and {@code RangedAttackMob} interfaces are also
 * implemented to define this entity as something that can do range attacks
 * and can be converted to an item.<br>
 * <br>
 * New custom {@code NBT}s are also added such as {@code LEVEL}
 * and {@code FROM_ITEM} to identify their level variation and whether
 * this entity is spawned from an instance of already spawned turret entity.
 *
 * @author Virus5600
 * @since 1.0.0
 *
 * @see MobEntity
 * @see Itemable
 * @see RangedAttackMob
 *
 */
public class TurretEntity extends MobEntity implements Itemable, RangedAttackMob {
	private static final TrackedData<Integer> LEVEL;
	private static final TrackedData<Byte> FROM_ITEM;
	private static final TrackedData<Boolean> SHOOTING;
	private static final TrackedData<Boolean> SHOOTING_FX_DONE;
	private static final TrackedData<Boolean> HAS_TARGET;
	/**
	 * The sound that will play when this turret is healed.
	 */
	private SoundEvent healSound = SoundEvents.ENTITY_IRON_GOLEM_REPAIR;
	/**
	 * Contains all the items that can heal this entity.
	 */
	@Nullable
	private Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity, and must be stored in a 2D array. An item can have multiple effects and is structured as this:<br>
	 * {@code [}<br>
	 * {@code   [StatusEffect, duration, amplifier],}<br>
	 * {@code   [StatusEffect, duration, amplifier],}<br>
	 * {@code   [StatusEffect, duration, amplifier]}<br>
	 * {@code ]}
	 */
	@Nullable
	private Map<Item, List<Object[]>> effectSource;
	@SuppressWarnings("unused")
	private BlockPos prevAttachedBlock;
	private TurretMaterial material;

	protected static final TrackedData<Float> X;
	protected static final TrackedData<Float> Y;
	protected static final TrackedData<Float> Z;
	protected static final TrackedData<Float> YAW;
	protected static final TrackedData<Float> PITCH;
	protected static final TrackedData<Float> TARGET_POS_X;
	protected static final TrackedData<Float> TARGET_POS_Y;
	protected static final TrackedData<Float> TARGET_POS_Z;
	protected static final TrackedData<Direction> ATTACHED_FACE;
	protected SoundEvent shootSound = SoundEvents.ENTITY_ARROW_SHOOT;
	protected Class<?> projectile = null;
	protected int level;
	protected final Random random;

	static final Vec3f SOUTH_VECTOR;

	/**
	 * List of plank items in the game. This allows easy insertion of all the items when needed by iterating through the List.
	 */
	public static final List<Item> PLANKS = Arrays.asList(new Item[]{
		Items.ACACIA_PLANKS,
		Items.BIRCH_PLANKS,
		Items.CRIMSON_PLANKS,
		Items.DARK_OAK_PLANKS,
		Items.JUNGLE_PLANKS,
		Items.MANGROVE_PLANKS,
		Items.OAK_PLANKS,
		Items.SPRUCE_PLANKS,
		Items.WARPED_PLANKS
	});
	/**
	 * List of log items in the game. This allows easy insertion of all the items when needed by iterating through the List.
	 */
	public static final List<Item> LOGS = Arrays.asList(new Item[]{
			Items.ACACIA_LOG,
			Items.BIRCH_LOG,
			Items.CRIMSON_STEM,
			Items.DARK_OAK_LOG,
			Items.JUNGLE_LOG,
			Items.MANGROVE_LOG,
			Items.OAK_LOG,
			Items.SPRUCE_LOG,
			Items.WARPED_STEM
		});

	// CONSTRUCTORS //
	public TurretEntity(EntityType<? extends MobEntity> entityType, World world, TurretMaterial material, Class<?> projectile) {
		this(entityType, world, material);
		this.projectile = projectile;
	}

	public TurretEntity(EntityType<? extends MobEntity> entityType, World world, TurretMaterial material) {
		super(entityType, world);
		this.material = material;
		this.random = Random.create();
		this.level = 0;
		if (this.projectile == null)
			this.projectile = ArrowEntity.class;
	}

	// METHODS //
	// PROTECTED
	protected void setLevel(int level) {
		this.dataTracker.set(LEVEL, level);
	}

	@Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LEVEL, this.level);
        this.dataTracker.startTracking(FROM_ITEM, (byte) 1);
        this.dataTracker.startTracking(SHOOTING, false);
        this.dataTracker.startTracking(SHOOTING_FX_DONE, true);
        this.dataTracker.startTracking(HAS_TARGET, false);

        this.dataTracker.startTracking(X, 0f);
        this.dataTracker.startTracking(Y, 0f);
        this.dataTracker.startTracking(Z, 0f);
        this.dataTracker.startTracking(YAW, 0f);
        this.dataTracker.startTracking(PITCH, 0f);
        this.dataTracker.startTracking(TARGET_POS_X, 0f);
        this.dataTracker.startTracking(TARGET_POS_Y, 0f);
        this.dataTracker.startTracking(TARGET_POS_Z, 0f);
        this.dataTracker.startTracking(ATTACHED_FACE, Direction.DOWN);
    }

	@Override
    protected BodyControl createBodyControl() {
        return new TurretBodyControl(this);
    }

	@Override
    protected MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

	protected void tryAttachOrFall() {
        Direction direction = this.findAttachSide(this.getBlockPos());
        if (direction == null) {
            this.setAttachedFace(direction);
        } else if (direction != Direction.DOWN) {
            this.tryFall();
        }
    }

	protected boolean tryFall() {
		if (this.isAiDisabled() || !this.isAlive())
            return false;

		BlockPos blockPos = this.getBlockPos().add(new Vec3i(0, -1, 0));
		if (this.isValidFallingPosition(blockPos)) {
			this.setVelocity(super.getVelocity().multiply(0.98));
			return true;
		}

		return false;
	}

	protected void setAttachedFace(Direction face) {
		this.dataTracker.set(ATTACHED_FACE, face);
	}

	protected Direction getAttachedFace() {
		return this.dataTracker.get(ATTACHED_FACE);
	}

	@Nullable
	protected Direction findAttachSide(BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (!this.canStay(pos, direction))
				continue;

			return direction;
		}

		return null;
	}

	protected boolean canStay(BlockPos pos, Direction direction) {
		if (!this.isValidFallingPosition(pos))
			return false;

		direction = direction == null ? Direction.DOWN : direction;
		Direction oppositeDir = direction.getOpposite();
		if (!this.world.isDirectionSolid(pos.offset(direction), this, oppositeDir))
			return false;

		Box box = this.calculateBoundingBox().offset(pos).contract(1.0E-6);
		return this.world.isSpaceEmpty(this, box);
	}

	protected boolean isValidFallingPosition(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);

        if (blockState.isAir())
            return true;

        if ((blockState.isOf(Blocks.BUBBLE_COLUMN) && pos.equals(this.getBlockPos())) || (blockState.isOf(Blocks.WATER) && pos.equals(this.getBlockPos())))
        	return true;

        boolean bl = blockState.isOf(Blocks.MOVING_PISTON) && pos.equals(this.getBlockPos());
        return !bl;
    }

	// PUBLIC
	public Vec3d getRelativePos(double offset) {
		return this.getRelativePos(offset, offset, offset);
	}

	/**
	 * Identifies the position of a point relative to this turret rotation and position.
	 *
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param xOffset The offset of the point at the local X-Axis of this turret
	 * @param yOffset The offset of the point at the local Y-Axis of this turret
	 * @param zOffset The offset of the point at the local Z-Axis of this turret
	 *
	 * @return Vec3d the relative position of this point, assuming that the origin is at <b>[0, 0, 0]<b>
	 */
	public Vec3d getRelativePos(double xOffset, double yOffset, double zOffset) {
		double yaw = ((this.getTrackedYaw() + 90 + xOffset) * Math.PI) / 180;
		double pitch = ((this.getTrackedPitch() + yOffset) * Math.PI) / 180;

		double x = zOffset * Math.cos(yaw) * Math.cos(pitch);
		double y = zOffset * Math.sin(pitch);
		double z = zOffset * Math.sin(yaw) * Math.cos(pitch);

		return new Vec3d(x, -y, z);
	}

	@Override
	public int getMaxLookPitchChange() {
		return 30;
	}

	@Override
	public int getMaxHeadRotation() {
		return 360;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}

	public int getLevel() {
		return this.dataTracker.get(LEVEL);
	}

	public TurretMaterial getTurretMaterial() {
		return this.material;
	}

	public void setShootSound(SoundEvent sound) {
		this.shootSound = sound;
	}

	public SoundEvent getShootSound() {
		return this.shootSound;
	}


	public void setHealSound(SoundEvent sound) {
		this.healSound = sound;
	}

	public SoundEvent getHealSound() {
		return this.healSound;
	}

	/**
	 * Adds an item to the map of items that can heal this turret.
	 * <br><br><b>NOTE:</b> If the item is already added, the old heal amount would be updated to the specified value
	 *
	 * @param item Item to be added
	 * @param amount Amount of health that will be healed when used
	 * @return TurretEntity
	 *
	 * @see Item
	 * @see Items
	 * @see TurretEntity
	 */
	public TurretEntity addHealable(Item item, float amount) {
		if (this.healables == null)
			this.healables = new HashMap<>();
		this.healables.put(item, amount);
		return this;
	}

	/**
	 * Adds the list of items to the map of items that can heal this turret.
	 * <br><br><b>NOTE:</b> If the item is already added, the old heal amount would be updated to the specified value
	 *
	 * @param group A {@link List list} of items that will be added to the {@code healable} map
	 * @param amount Amount of health that will be healed when used
	 * @return TurretEntity
	 *
	 * @see Item
	 * @see Items
	 * @see TurretEntity
	 */
	public TurretEntity addHealable(List<Item> group, float amount) {
		if (this.healables == null)
			this.healables = new HashMap<>();

		for (Item item : group)
			this.healables.put(item, amount);

		return this;
	}

	/**
	 * Adds all the items to the list of items that can heal this turret.
	 * @param healables A key-value pair made from a {@code Map}, The map uses the {@code Item} as the key, and the {@code Float} as the amount of health healed
	 * @return TurretEntity
	 *
	 * @see Item
	 * @see Items
	 * @see Map
	 * @see TurretEntity
	 */
	public TurretEntity addHealables(Map<Item, Float> healables) {
		if (this.healables == null)
			this.healables = healables;
		else
			this.healables.putAll(healables);
		return this;
	}

	/**
	 * Identifies whether the submitted item is part of the healable map.
	 * @param item The item in question
	 * @return boolean
	 */
	public boolean isHealableItem(Item item) {
		return this.healables.containsKey(item);
	}

	/**
	 * Retrieves the item and identifies the heal amount of this item, otherwise, return {@code null}.
	 * @param item
	 * @return float Amount of healing this item will give to the entity; otherwise, {@code null}
	 */
	@Nullable
	public float getHealAmt(Item item) {
		return this.isHealableItem(item) ? this.healables.get(item) : null;
	}

	/**
	 * Adds an item to the map of items that can give mob effect to this turret.
	 * <br><br><b>NOTE:</b> If the item is already added, the old values will be updated
	 *
	 * @param item Item to be added
	 * @param effect The mob effect that will be added
	 * @param duration How long this effect will last (in seconds)
	 * @param amplifier Level of severity (or just basically level) with 0 being the lowest.
	 * @return TurretEntity
	 *
	 * @see Item
	 * @see Items
	 * @see StatusEffect
	 * @see TurretEntity
	 */
	@SuppressWarnings("serial")
	public TurretEntity addEffectSource(Item item, StatusEffect effect, float duration, int amplifier) {
		if (this.effectSource == null)
			this.effectSource = new HashMap<>();
		this.effectSource.put(item, new ArrayList<Object[]>() {{add(new Object[] {effect, duration, amplifier});}});

		return this;
	}

	/**
	 * Adds the list of items to the map of items that can give this turret a mob effect.
	 * <br><br><b>NOTE:</b> If the item is already added, the old heal amount would be updated to the specified value
	 *
	 * @param group A {@link List list} of items that will be added to the {@code healable} map
	 * @param effect The mob effect that will be added
	 * @param duration How long this effect will last (in seconds)
	 * @param amplifier Level of severity (or just basically level) with 0 being the lowest.
	 * @return TurretEntity
	 *
	 * @see Item
	 * @see Items
	 * @see StatusEffect
	 * @see TurretEntity
	 */
	public TurretEntity addEffectSource(List<Item> group, StatusEffect effect, float duration, int amplifier) {
		if (this.effectSource == null)
			this.effectSource = new HashMap<>();

		@SuppressWarnings("serial")
		List<Object[]> args = new ArrayList<>() {{add(new Object[] {effect, duration, amplifier});}};
		for (Item item : group)
			this.effectSource.put(item, args);

		return this;
	}

	/**
	 * Adds all the items to the list of items that can give this turret a mob effect.
	 * @param effectSource A key-value pair made from a {@code Map}, The map uses the {@code Item} as the key, and a {@code List} of {@code Object} array for the effect. The array should only have three items in order: {@code StatusEffect}, {@code duration}, and {@code amplifier}.
	 * @return TurretEntity
	 *
	 * @see #effectSource
	 * @see Item
	 * @see Items
	 * @see Map
	 * @see StatusEffect
	 * @see TurretEntity
	 */
	public TurretEntity addEffectSource(Map<Item, List<Object[]>> effectSource) {
		if (this.effectSource == null)
			this.effectSource = effectSource;
		else
			this.effectSource.putAll(effectSource);
		return this;
	}

	/**
	 * Updates a single entry in the list of effect the item has. If the effect isn't in the list yet, it will be added with default values of 10 seconds duration and amplifier of 0.
	 * Many effects can be updated at the same time.
	 *
	 * @param item The item in question
	 * @param args An Object array that consists of {@code StatusEffect,} {@code duration} (in seconds), and {@code amplifier} level
	 *
	 * @return TuretEntity
	 *
	 * @see #effectSource
	 * @see Item
	 * @see Items
	 * @see StatusEffect
	 * @see TurretEntity
	 */
	public TurretEntity updateEffectSource(Item item, Object[]... args) {
		List<Object[]> currentArgs = this.getMobEffect(item);

		if (args.length == 0)
			return this;

		for (Object[] arg : args) {
			if (arg.length == 0)
				continue;

			Object[] toPass = new Object[3];

			if (arg[0] instanceof StatusEffect) {
				toPass[0] = arg[0];
				toPass[1] = 10;
				toPass[2] = 0;

				if (arg.length == 2) {
					if (arg[1] instanceof Float)
						toPass[1] = arg[1];
					else if (arg[1] instanceof Integer)
						toPass[2] = arg[1];
				}
				else if (arg.length == 3) {
					toPass[1] = arg[1];
					toPass[2] = arg[2];
				}

				currentArgs.add(new Object[] {toPass[0], toPass[1], toPass[2]});
			}
			else {
				if (arg.length == 3)
					DefensiveMeasures.LOGGER.warn("Effect source at " + this.getName().getString() + " was not " + (this.effectSourceHasEffect(item, (StatusEffect) arg[0]) ? "updated" : "registered") + " due to given array not matching the correct order of items in the array, having [" + arg[0].getClass().getName() + ", " + arg[1].getClass().getName() + ", " + arg[2].getClass().getName() + "] instead of [StatusEffect, Float, Integer]");
				else if (arg.length == 2)
					DefensiveMeasures.LOGGER.warn("Effect source at " + this.getName().getString() + " was not " + (this.effectSourceHasEffect(item, (StatusEffect) arg[0]) ? "updated" : "registered") + " due to given array not matching the correct order of items in the array, having [" + arg[0].getClass().getName() + ", " + arg[1].getClass().getName() + "] instead of [StatusEffect, Float] OR [StatusEffect, Integer]");
				else if (arg.length == 1)
					DefensiveMeasures.LOGGER.warn("Effect source at " + this.getName().getString() + " was not " + (this.effectSourceHasEffect(item, (StatusEffect) arg[0]) ? "updated" : "registered") + " due to given array not matching the correct order of items in the array, having [" + arg[0].getClass().getName() + "] instead of [StatusEffect]");
			}

		}

		this.effectSource.put(item, currentArgs);

		return this;
	}

	/**
	 * Identifies whether the submitted item is part of the effect source map.
	 * @param item The item in question
	 *
	 * @return boolean
	 *
	 * @see StatusEffect
	 */
	public boolean isEffectSource(Item item) {
		return this.effectSource.containsKey(item);
	}

	/**
	 * Retrieves the item and identifies the mob effects this should apply to this turret, otherwise, return {@code null}.
	 * @param item
	 *
	 * @return List<Object[]> An list of information consisting an array of the effect data. The data are set in this order:
	 * <ol>
	 * 	<li>{@code StatusEffect}</li>
	 * 	<li>{@code duration}</li>
	 * 	<li>{@code amplifier}</li>
	 * </ol>
	 */
	@Nullable
	public List<Object[]> getMobEffect(Item item) {
		return this.isEffectSource(item) ? this.effectSource.get(item) : null;
	}

	/**
	 * Identifies whether this item already have the effect in its list or not. This iterates through all the entries
	 *
	 * @param item The item in question
	 * @return boolean
	 */
	public boolean effectSourceHasEffect(Item item, StatusEffect effect) {
		for (Object[] registeredEffect : this.getMobEffect(item))
			if ((StatusEffect) registeredEffect[0] == effect)
				return true;
		return false;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Level", this.getLevel());
		nbt.putByte("FromItem", this.isFromItem());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setLevel(nbt.getInt("Level"));
		this.setFromItem(nbt.getByte("FromItem"));
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return false;
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (this.world.isClient()) {
			this.prevAttachedBlock = null;
		}
		return super.startRiding(entity, force);
	}

	@Override
	public void stopRiding() {
		super.stopRiding();

		if (this.world.isClient) {
			this.prevAttachedBlock = this.getBlockPos();
		}
		this.prevBodyYaw = 0.0f;
		this.bodyYaw = 0.0f;
	}

	@Override
	@Nullable
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
		if (entityData == null)
			entityData = new PassiveData(false);

		this.headYaw = this.prevHeadYaw;
		this.prevBodyYaw = 0.0f;
		this.resetPosition();

		if (entityNbt != null) {
			if (spawnReason == SpawnReason.SPAWN_EGG && entityNbt.contains("FromItem")) {
				this.setHealth(entityNbt.getFloat("Health"));
				return entityData;
			}
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public boolean isCollidable() {
		return this.isAlive();
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.BLOCKS;
	}

	@Override
	public byte isFromItem() {
		return this.dataTracker.get(FROM_ITEM);
	}

	@Override
	public void setFromItem(byte fromItem) {
		this.dataTracker.set(FROM_ITEM, fromItem);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void copyDataToStack(ItemStack stack) {
		Itemable.copyDataToStack(this, stack);
		NbtCompound nbtCompound = stack.getOrCreateNbt();

		nbtCompound.putInt("Level", this.getLevel());
		nbtCompound.putByte("FromItem", (byte) 1);
		nbtCompound.putFloat("Health", this.getHealth());

		Brain<?> brain = this.getBrain();
		if (brain.hasMemoryModule(MemoryModuleType.ATTACK_COOLING_DOWN)) {
			nbtCompound.putLong("AttackCoolingDown", brain.getMemory(MemoryModuleType.ATTACK_COOLING_DOWN));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void copyDataFromNbt(NbtCompound nbt) {
		Itemable.copyDataFromNbt(this, nbt);

		if (nbt.contains("Level"))
			this.setLevel(nbt.getInt("Level"));
		if (nbt.contains("FromItem"))
			this.setFromItem(nbt.getByte("FromItem"));
		if (nbt.contains("Health"))
			this.setHealth(nbt.getFloat("Health"));
		if (nbt.contains("AttackCoolingDown"))
			this.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, nbt.getLong("AttackCoolingDown"));
	}

	@Override
	public ItemStack getEntityItem() {
		return null;
	}

	@Override
	public SoundEvent getTurretRemoveSound() {
		return null;
	}

	@Override
	public void tick() {
		super.tick();

		if (!world.isClient()) {
			this.setHasTarget(this.getTarget() != null);
		} else {
			// SNAPPING THE TURRET BACK TO PLACE
			if (this.getVelocity().x == 0 && this.getVelocity().z == 0 && !this.hasVehicle()) {
				Vec3d newPos = new Vec3d(
					(double) MathHelper.floor(this.getX()) + 0.5,
					this.getY(),
					(double) MathHelper.floor(this.getZ()) + 0.5
				);

				this.tryAttachOrFall();
				super.setPosition(newPos);
				this.world.emitGameEvent(GameEvent.TELEPORT, newPos, GameEvent.Emitter.of(this));

				if (this.getVelocity() == Vec3d.ZERO && this.world.isClient && !this.hasVehicle()) {
					this.lastRenderX = this.getX();
					this.lastRenderY = this.getY();
					this.lastRenderZ = this.getZ();
				}
			}
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();

		if (!(this.world.isClient || this.hasVehicle() || this.canStay(this.getBlockPos(), this.getAttachedFace()))) {
			this.tryAttachOrFall();
		}
	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		if (!this.isShooting()) {
			this.setShooting(target != null);
		}

		try {
			this.setHasTarget(target != null);

			ProjectileEntity projectile = (ProjectileEntity) this.projectile.getConstructor(World.class, LivingEntity.class).newInstance(world, this);
			double vx = target.getX() - this.getX();
			double vy = target.getBodyY(1/2) - projectile.getY();
			double vz = target.getZ() - this.getZ();
			double variance = Math.sqrt(vx * vx + vz * vz);
			float divergence = 0 + this.world.getDifficulty().getId() * 2;

			projectile.setVelocity(vx, vy + variance * 0.125f, vz, 2.5f, divergence);
			this.world.spawnEntity(projectile);

			this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
			DefensiveMeasures.LOGGER.debug("");
			DefensiveMeasures.LOGGER.debug("	 DM ERROR OCCURED	 ");
			DefensiveMeasures.LOGGER.debug("===== ERROR MSG START =====");
			DefensiveMeasures.LOGGER.debug("LOCALIZED ERROR MESSAGE:");
			DefensiveMeasures.LOGGER.debug(e.getLocalizedMessage());
			DefensiveMeasures.LOGGER.debug("");
			DefensiveMeasures.LOGGER.debug("ERROR MESSAGE:");
			DefensiveMeasures.LOGGER.debug(e.getMessage());
			DefensiveMeasures.LOGGER.debug("===== ERROR MSG END =====");
			DefensiveMeasures.LOGGER.debug("");
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack item = player.getStackInHand(hand);
		boolean itemDecrement = false;

		// TURRET REMOVER
		if (item.getItem() == ModItems.TURRET_REMOVER) {
				item.damage(1, player, player2 -> player2.sendToolBreakStatus(hand));
		}

		// HEALABLES
		else if (this.isHealableItem(item.getItem())) {
			this.heal(this.getHealAmt(item.getItem()));

			// Decrement item amount (if it is a plain item) or durability (if it is a tool)
			if (item.getItem().isDamageable()) {
				if (item.getDamage() > item.getMaxDamage())
					item.decrement(1);
				else
					item.setDamage(item.getDamage() + 1);
			}
			else {
				item.decrement(1);
			}

			// Indicates a repair was done
			this.world.playSoundFromEntity(player, this, this.healSound, SoundCategory.MASTER, 1, 1);


			// Applies status effect if it provides one
			if (this.isEffectSource(item.getItem())) {
				itemDecrement = true;
				for (Object[] args : this.getMobEffect(item.getItem())) {
					addStatusEffect(
						new StatusEffectInstance(
							(StatusEffect) args[0],
							(int) args[1] * 20,
							(int) args[2]
						)
					);
				}
			}

			if (itemDecrement) {
				item.decrement(1);
				return ActionResult.success(this.world.isClient);
			}
		}

		// OTHERWISE
		return ActionResult.PASS;
	}

	public boolean isShooting() {
		return this.dataTracker.get(SHOOTING);
	}

	public void setShooting(boolean shooting) {
		this.dataTracker.set(SHOOTING, shooting);
	}

	public boolean getShootingFXDone() {
		return this.dataTracker.get(SHOOTING_FX_DONE);
	}

	public void setShootingFXDone(boolean status) {
		this.dataTracker.set(SHOOTING_FX_DONE, status);
	}

	public boolean hasTarget() {
		return this.dataTracker.get(HAS_TARGET);
	}

	public void setHasTarget(boolean hasTarget) {
		this.dataTracker.set(HAS_TARGET, hasTarget);
	}

	public void setPos(TrackedData<Float> axis, double value) {
		this.dataTracker.set(axis, (float) value);
	}

	public double getPos(TrackedData<Float> axis) {
		return this.dataTracker.get(axis);
	}

	public void setTrackedYaw(double value) {
		this.dataTracker.set(YAW, (float) value);
	}

	public double getTrackedYaw() {
		return (double) this.dataTracker.get(YAW);
	}

	public void setTrackedPitch(double value) {
		this.dataTracker.set(PITCH, (float) value);
	}

	public double getTrackedPitch() {
		return (double) this.dataTracker.get(PITCH);
	}

    @Override
    public Vec3d getVelocity() {
    	DamageRecord record = this.getDamageTracker().getMostRecentDamage();
    	boolean isExplosive = record == null ? false : record.getDamageSource().isExplosive();

    	if (this.world.getBlockState(this.getVelocityAffectingPos()).isOf(Blocks.BUBBLE_COLUMN) ||
    			this.world.getBlockState(this.getVelocityAffectingPos().add(0, 1, 0)).isOf(Blocks.BUBBLE_COLUMN) ||
    			this.world.getBlockState(this.getVelocityAffectingPos()).isOf(Blocks.WATER) ||
    			this.world.getBlockState(this.getVelocityAffectingPos().add(0, 1, 0)).isOf(Blocks.WATER) ||
    			this.getAttachedFace() != Direction.DOWN ||
    			isExplosive)
    		return new Vec3d(0, -Math.abs(super.getVelocity().getY()), 0);

    	return super.getVelocity();
    }

	static {
		LEVEL = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.INTEGER);
		FROM_ITEM = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.BYTE);
		SHOOTING = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		SHOOTING_FX_DONE = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		HAS_TARGET = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		ATTACHED_FACE = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FACING);

		X = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		Y = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		Z = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		YAW = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		PITCH = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);

		TARGET_POS_X = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		TARGET_POS_Y = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		TARGET_POS_Z = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		SOUTH_VECTOR = Util.make(() -> {
			Vec3i vec3i = Direction.SOUTH.getVector();
			return new Vec3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
		});
	}

	static class TurretBodyControl extends BodyControl {
		@SuppressWarnings("unused")
		private MobEntity entity;

		public TurretBodyControl(MobEntity mobEntity) {
            super(mobEntity);
            this.entity = mobEntity;
        }

        @Override
        public void tick() {
        }
    }
}