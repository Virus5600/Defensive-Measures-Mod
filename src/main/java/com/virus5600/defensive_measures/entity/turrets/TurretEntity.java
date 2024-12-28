package com.virus5600.defensive_measures.entity.turrets;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.item.turrets.TurretItem;

import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.0.0
 *
 * @see MobEntity
 * @see Itemable
 * @see RangedAttackMob
 *
 */
public class TurretEntity extends MobEntity implements Itemable, RangedAttackMob {
	/**
	 * Tracks the level (stage) of this turret entity.
	 */
	private static final TrackedData<Integer> LEVEL;
	/**
	 * Tracks whether this entity is spawned from an item.
	 */
	private static final TrackedData<Byte> FROM_ITEM;

	/**
	 * Tracks the direction where this turret is attached to.
	 */
	protected static final TrackedData<Direction> ATTACHED_FACE;
	/**
	 * Tracks the X position of this turret.
	 */
	protected static final TrackedData<Float> X;
	/**
	 * Tracks the Y position of this turret.
	 */
	protected static final TrackedData<Float> Y;
	/**
	 * Tracks the Z position of this turret.
	 */
	protected static final TrackedData<Float> Z;

	////////////////////////
	// INSTANCE VARIABLES //
	////////////////////////
	/**
	 * Acts as a storage for the turret's world position, allowing the turret to snap in place.
	 */
	private BlockPos prevAttachedBlock;
	/**
	 * Contains all the items that can heal this entity.
	 */
	private Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity, and must be stored in a 2D array.
	 * An item can have multiple effects and is structured as this:<br>
	 * <pre><code>
	 * [
	 * 	[StatusEffect, duration, amplifier]
	 * 	[StatusEffect, duration, amplifier]
	 * 	[StatusEffect, duration, amplifier]
	 * ]
	 * </code></pre>
	 */
	private Map<Item, List<Object[]>> effectSource;

	/**
	 * The sound the turret makes when it is being healed.
	 */
	protected SoundEvent healSound = SoundEvents.ENTITY_IRON_GOLEM_REPAIR;
	/**
	 * The sound the turret makes when it is shooting.
	 */
	protected SoundEvent shootSound = SoundEvents.ENTITY_ARROW_SHOOT;
	/**
	 * Defines what kind of projectile this turret will shoot.
	 */
	protected Class<?> projectile;
	/**
	 * Defines the level of this turret. The higher the level, the stronger
	 * the turret.
	 */
	protected int level;
	/**
	 * The material of this turret.
	 */
	protected TurretMaterial material;
	/**
	 * The item counterpart of this item. Refer to {@link ModItems} list
	 * to see the list of items that can be converted to this entity.
	 * The items should be a subclass of {@link TurretItem}.
	 */
	protected Item itemable;
	/**
	 * A randomizer for this turret's instance.
	 */
	protected final Random random;

	/**
	 * List of plank items in the game. This allows easy insertion of all the items when needed by iterating through the List.
	 */
	public static final List<Item> PLANKS = Arrays.asList(
		Items.ACACIA_PLANKS,
		Items.BAMBOO_PLANKS,
		Items.BIRCH_PLANKS,
		Items.CHERRY_PLANKS,
		Items.CRIMSON_PLANKS,
		Items.DARK_OAK_PLANKS,
		Items.JUNGLE_PLANKS,
		Items.MANGROVE_PLANKS,
		Items.OAK_PLANKS,
		Items.SPRUCE_PLANKS,
		Items.WARPED_PLANKS
	);

	/**
	 * List of log items in the game. This allows easy insertion of all the items when needed by iterating through the List.
	 */
	public static final List<Item> LOGS = Arrays.asList(
		Items.ACACIA_LOG,
		Items.BAMBOO,
		Items.BIRCH_LOG,
		Items.CHERRY_LOG,
		Items.CRIMSON_STEM,
		Items.DARK_OAK_LOG,
		Items.JUNGLE_LOG,
		Items.MANGROVE_LOG,
		Items.OAK_LOG,
		Items.SPRUCE_LOG,
		Items.WARPED_HYPHAE,
		Items.WARPED_STEM,
		Items.STRIPPED_ACACIA_LOG,
		Items.STRIPPED_BIRCH_LOG,
		Items.STRIPPED_CHERRY_LOG,
		Items.STRIPPED_CRIMSON_STEM,
		Items.STRIPPED_DARK_OAK_LOG,
		Items.STRIPPED_JUNGLE_LOG,
		Items.STRIPPED_MANGROVE_LOG,
		Items.STRIPPED_OAK_LOG,
		Items.STRIPPED_SPRUCE_LOG,
		Items.STRIPPED_WARPED_HYPHAE,
		Items.STRIPPED_WARPED_STEM
	);

	//////////////////
	// CONSTRUCTORS //
	//////////////////

	/**
	 * Constructs a new {@code TurretEntity} with the given {@code EntityType}, {@code World},
	 * {@code TurretMaterial}, {@code Class} of the projectile, and {@code Item}.
	 *
	 * @param entityType The type of this entity.
	 * @param world The world this entity is in.
	 * @param material The material of this turret.
	 * @param projectile The class of the projectile this turret will shoot.
	 * @param itemable The itemable counterpart of this entity.
	 *
	 * @see #itemable
	 */
	public TurretEntity(EntityType<? extends MobEntity> entityType, World world, TurretMaterial material, Class<?> projectile, Item itemable) {
		this(entityType, world, material, itemable);
		this.projectile = projectile;
	}

	/**
	 * Constructs a new {@code TurretEntity} with the given {@code EntityType}, {@code World},
	 * {@code TurretMaterial}, and {@code Item}. If the {@code projectile} is not yet defined, the default
	 * projectile will be used, which is an {@code ArrowEntity}.
	 *
	 * @param entityType The type of this entity.
	 * @param world The world this entity is in.
	 * @param material The material of this turret.
	 * @param itemable The itemable counterpart of this entity.
	 *
	 * @see #itemable
	 */
	public TurretEntity(EntityType<? extends MobEntity> entityType, World world, TurretMaterial material, Item itemable) {
		super(entityType, world);
		DefensiveMeasures.LOGGER.debug("Creating a new TurretEntity called {}", entityType.getName());

		this.material = material;
		this.itemable = itemable;
		this.random = Random.create();
		this.lookControl = new TurretEntity.TurretLookControl(this);

		if (this.projectile == null) {
			this.projectile = ArrowEntity.class;
		}
	}

	//////////////////
	// INITIALIZERS //
	//////////////////

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		// Entity related tracking
		builder.add(LEVEL, this.level)
			.add(FROM_ITEM, (byte) 1)

		// Position related tracking
			.add(ATTACHED_FACE, Direction.DOWN)
			.add(X, 0f)
			.add(Y, 0f)
			.add(Z, 0f);
		super.initDataTracker(builder);
	}

	@Override
	protected BodyControl createBodyControl() {
		return new TurretBodyControl(this);
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		this.headYaw = this.prevHeadYaw;
		this.prevBodyYaw = 0.0f;
		this.resetPosition();

		if (spawnReason == SpawnReason.SPAWN_ITEM_USE) {
			return entityData;
		}

		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	/////////////////////
	// PROCESS METHODS //
	/////////////////////

	/**
	 * Attempts to attach this entity to a block or fall if there is no block to attach to
	 * at the current position. If the entity is attached to a block, it will not fall but
	 * will instead snap to the block's position. However, if the entity is not attached to
	 * a block, it will look for a block to attach to around it and if there is none, it will
	 * fall.
	 */
	protected void tryAttachOrFall() {
		Direction dir = this.findAttachableSide(this.getBlockPos());
		if (dir != null)
			this.setAttachedFace(dir);
		else
			this.tryFall();
	}

	/**
	 * Attempts to let this turret fall regardless of whether it is attached to a block or not.
	 *
	 * @return {@code boolean} Returns {@code true} if the turret is falling, otherwise {@code false}.
	 */
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

	/**
	 * Retrieves the currently attached side of this turret.
	 * @param pos The current position of this turret.
	 * @return {@code Direction} The direction where this turret is attached to.
	 */
	@Nullable
	protected Direction findAttachableSide(BlockPos pos) {
		for (Direction dir : Direction.values()) {
			if (!this.canStay(pos, dir))
				continue;
			return dir;
		}
		return null;
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack item = player.getStackInHand(hand);
		boolean isSurvival = !player.isCreative();
		boolean isSuccess = false;

		// Turret Remover Interaction
		if (item.getItem().getName().equals(ModItems.TURRET_REMOVER)) {
			if (isSurvival)
				item.damage(1, player);

			isSuccess = true;
		}

		// Healables
		else if (this.isHealableItem(item.getItem())) {
			// Heals the turret
			this.heal(this.getHealAmt(item.getItem()));

			// Indicates a repair was done
			float pitch = 1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
			this.playSound(this.getHealSound(), 1F, pitch);

			isSuccess = true;
		}

		// Effect Source
		if (this.isEffectSource(item.getItem())) {
			for (Object[] args : this.getMobEffect(item.getItem())) {
				this.addStatusEffect(
					new StatusEffectInstance(
						(RegistryEntry<StatusEffect>) args[0],
						(int) ((float) ((Integer) args[1]) * 20),
						(Integer) args[2]
					)
				);
			}

			if (item.isDamageable()) {
				item.damage(1, player);
			}
			else {
				item.decrementUnlessCreative(1, player);
			}

			isSuccess = true;
		}

		if (isSuccess)
			return ActionResult.SUCCESS;

		if (item.getItem() == ModItems.TURRET_REMOVER) {
			return Itemable.tryItem(player, hand, this, ModItems.TURRET_REMOVER, this.itemable)
				.orElse(super.interactMob(player, hand));
		}
		return super.interactMob(player, hand);
	}

	@Override
	protected float getJumpVelocityMultiplier() {
		return 0.0f;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setLevel(nbt.getInt("Level"));
		this.setFromItem(nbt.getByte("FromItem"));
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (this.getWorld().isClient()) {
			this.prevAttachedBlock = null;
		}

		this.setAttachedFace(Direction.DOWN);
		return super.startRiding(entity, force);
	}

	@Override
	public void stopRiding() {
		super.stopRiding();

		if (this.getWorld().isClient) {
			this.prevAttachedBlock = this.getBlockPos();
		}

		this.prevBodyYaw = 0.0f;
		this.bodyYaw = 0.0f;
	}

	@Override
	@Nullable
	public ItemStack getPickBlockStack() {
		TurretItem turretItem = TurretItem.forEntity(this.getType());

		if (turretItem == null)
			return null;

		return new ItemStack(turretItem);
	}

	@Override
	public Vec3d getVelocity() {
		if (this.getWorld().getBlockState(this.getVelocityAffectingPos()).isOf(Blocks.BUBBLE_COLUMN)
			|| this.getWorld().getBlockState(this.getVelocityAffectingPos().add(0, 1, 0)).isOf(Blocks.BUBBLE_COLUMN)
			|| this.getWorld().getBlockState(this.getVelocityAffectingPos()).isOf(Blocks.WATER)
			|| this.getWorld().getBlockState(this.getVelocityAffectingPos().add(0, 1, 0)).isOf(Blocks.WATER)
			|| this.getAttachedFace() != Direction.DOWN)
			return new Vec3d(0, -Math.abs(super.getVelocity().getY()), 0);

		return super.getVelocity();
	}

	@Override
	public void tick() {
		super.tick();

		if (this.getWorld().isClient()) {
			// SNAPPING THE TURRET BACK IN PLACE
			if (this.getVelocity().x == 0 && this.getVelocity().z == 0 && !this.hasVehicle()) {
				Vec3d newPos = new Vec3d(
					(double) MathHelper.floor(this.getX()) + 0.5,
					this.getY(),
					(double) MathHelper.floor(this.getZ()) + 0.5
				);

				this.tryAttachOrFall();
				super.setPosition(newPos);
				this.getWorld().emitGameEvent(this, GameEvent.TELEPORT, newPos);

				if (this.getVelocity() == Vec3d.ZERO && this.getWorld().isClient() && !this.hasVehicle()) {
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

		if (!(this.getWorld().isClient()
			|| this.hasVehicle()
			|| this.canStay(this.getBlockPos(), this.getAttachedFace()))) {
			this.tryAttachOrFall();
		}
	}

	//////////////////////////////////////
	// QUESTION METHODS (True or False) //
	//////////////////////////////////////

	/**
	 * Determines whether this turret can stay on the given position or not.
	 * @param pos The position to check.
	 * @param dir The direction to check.
	 * @return {@code boolean} Returns {@code true} if the turret can stay, otherwise {@code false}.
	 */
	protected boolean canStay(BlockPos pos, Direction dir) {
		if (!this.isValidFallingPosition(pos))
			return false;

		dir = dir == null ? Direction.DOWN : dir;
		Direction opposite = dir.getOpposite();

		if (!this.getWorld().isDirectionSolid(pos.offset(dir), this, opposite))
			return false;

		Box box = this.calculateBoundingBox().offset(pos).contract(1.0E-6);
		return this.getWorld().isSpaceEmpty(this, box);
	}

	/**
	 * Identifies whether the position is a valid falling position for this turret.
	 * Blocks that are considered valid falling blocks are air, water, and bubble columns.
	 * @param pos The position to check.
	 * @return {@code boolean} Returns {@code true} if the position is valid, otherwise {@code false}.
	 */
	protected boolean isValidFallingPosition(BlockPos pos) {
		BlockState bState = this.getWorld().getBlockState(pos);

		if (bState.isAir()
			|| (bState.isOf(Blocks.BUBBLE_COLUMN) && pos.equals(this.getBlockPos()))
			|| (bState.isOf(Blocks.WATER) && pos.equals(this.getBlockPos()))
		)
			return true;

		return !(bState.isOf(Blocks.MOVING_PISTON) && pos.equals(this.getBlockPos()));
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
	 * Identifies whether this item already have the effect in its list or not. This iterates through all the entries
	 *
	 * @param item The item in question
	 * @param effect The effect to check
	 * @return boolean
	 */
	public boolean effectSourceHasEffect(Item item, StatusEffect effect) {
		List<Object[]> mobEffects = this.getMobEffect(item);
		if (mobEffects!= null)
			for (Object[] registeredEffect : mobEffects)
				if (registeredEffect[0] == effect)
					return true;
		return false;
	}

	@Override
	public boolean canBeLeashed() {
		return false;
	}

	@Override
	public boolean isCollidable() {
		return this.isAlive();
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////

	@Override
	protected MoveEffect getMoveEffect() {
		return MoveEffect.NONE;
	}

	/**
	 * Identifies the position of a point relative to this turret's rotation and
	 * {@link #getEyePos() eye position}.
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param xOffset The offset of the point at the local X-Axis of this turret.
	 * @param yOffset The offset of the point at the local Y-Axis of this turret.
	 * @param zOffset The offset of the point at the local Z-Axis of this turret.
	 *
	 * @return Vec3d the relative position of this point, assuming that the origin of the offset is at <b>[0, 0, 0]</b>
	 */
	public Vec3d getRelativePos(double xOffset, double yOffset, double zOffset) {
		float pitchRad = -this.getPitch() * (float) (Math.PI / 180.0);
		float yawRad = -this.getHeadYaw() * (float) (Math.PI / 180.0);

		Vec3d rotated = new Vec3d(xOffset, yOffset, zOffset)
			.rotateX(pitchRad)
			.rotateY(yawRad);

		return this.getEyePos()
			.add(rotated);
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

	public SoundEvent getShootSound() {
		return this.shootSound;
	}
	public void setShootSound(SoundEvent sound) {
		this.shootSound = sound;
	}

	public SoundEvent getHealSound() {
		return this.healSound;
	}
	public void setHealSound(SoundEvent sound) {
		this.healSound = sound;
	}

	public TurretMaterial getTurretMaterial() {
		return this.material;
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
		if (this.healables == null || this.healables.isEmpty())
			this.healables = healables;
		else
			this.healables.putAll(healables);
		return this;
	}
	/**
	 * Retrieves the item and identifies the heal amount of this item, otherwise, return {@code null}.
	 * @param item The item to get the heal amount from.
	 * @return float Amount of healing this item will give to the entity; otherwise, {@code 0}
	 */
	public float getHealAmt(Item item) {
		return this.isHealableItem(item) ? this.healables.get(item) : 0;
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
	public TurretEntity addEffectSource(Item item, StatusEffect effect, float duration, int amplifier) {
		if (this.effectSource == null)
			this.effectSource = new HashMap<>();
		this.effectSource.put(item, new ArrayList<>() {{
			add(new Object[]{effect, duration, amplifier});
		}});

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

		List<Object[]> args = new ArrayList<>() {{
			add(new Object[]{effect, duration, amplifier});
		}};
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
	 * @return TurretEntity
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

				if (currentArgs != null)
					currentArgs.add(toPass);
				else
					currentArgs = Arrays.asList(new Object[][] {toPass});
			}
			else {
				if (arg.length == 3)
					DefensiveMeasures.LOGGER.warn("Effect source at {} was not {} due to given array not matching the correct order of items in the array, having [{}, {}, {}] instead of [StatusEffect, Float, Integer]", this.getName().getString(), this.effectSourceHasEffect(item, (StatusEffect) arg[0]) ? "updated" : "registered", arg[0].getClass().getName(), arg[1].getClass().getName(), arg[2].getClass().getName());
				else if (arg.length == 2)
					DefensiveMeasures.LOGGER.warn("Effect source at {} was not {} due to given array not matching the correct order of items in the array, having [{}, {}] instead of [StatusEffect, Float] OR [StatusEffect, Integer]", this.getName().getString(), this.effectSourceHasEffect(item, (StatusEffect) arg[0]) ? "updated" : "registered", arg[0].getClass().getName(), arg[1].getClass().getName());
				else if (arg.length == 1)
					DefensiveMeasures.LOGGER.warn("Effect source at {} was not {} due to given array not matching the correct order of items in the array, having [{}] instead of [StatusEffect]", this.getName().getString(), this.effectSourceHasEffect(item, (StatusEffect) arg[0]) ? "updated" : "registered", arg[0].getClass().getName());
			}

		}

		this.effectSource.put(item, currentArgs);

		return this;
	}

	/**
	 * Retrieves the item and identifies the mob effects this should apply to this turret, otherwise, returns an empty {@code List<Object[]>}.
	 * @param item The item to get the mob effects from.
	 *
	 * @return {@code List<Object[]>} A list of information consisting an array of the effect data. The data are set in this order:
	 * <ol>
	 * 	<li>{@code StatusEffect}</li>
	 * 	<li>{@code duration}</li>
	 * 	<li>{@code amplifier}</li>
	 * </ol>
	 */
	public List<Object[]> getMobEffect(Item item) {
		return this.isEffectSource(item) ? this.effectSource.get(item) : List.of();
	}

	//////////////////
	// TRACKED DATA //
	//////////////////

	public int getLevel() {
		return this.dataTracker.get(LEVEL);
	}
	public void setLevel(int level) {
		this.dataTracker.set(LEVEL, level);
	}

	/**
	 * Retrieves the direction where this turret is attached to.
	 * @return {@code Direction} The direction where this turret is attached to.
	 */
	protected Direction getAttachedFace() {
		return this.dataTracker.get(ATTACHED_FACE);
	}
	/**
	 * Sets the direction where this will be attached to.
	 * @param dir The direction to attach to.
	 */
	protected void setAttachedFace(Direction dir) {
		this.dataTracker.set(ATTACHED_FACE, dir);
	}

	///////////////////////////////
	// INTERFACE IMPLEMENTATIONS //
	///////////////////////////////

	@Override
	public byte isFromItem() {
		return this.getDataTracker().get(FROM_ITEM);
	}

	@Override
	public void setFromItem(byte fromItem) {
		this.getDataTracker().set(FROM_ITEM, fromItem);
	}

	@Override
	public void copyDataToStack(ItemStack stack) {
		Itemable.copyDataToStack(this, stack);
	}

	@Override
	public void copyDataFromNbt(NbtCompound nbt) {
		Itemable.copyDataFromNbt(this, nbt);
	}

	public ItemStack getEntityItem() {
		return null;
	}

	public SoundEvent getEntityRemoveSound() {
		return null;
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		try {
			String targetName = target != null ? target.getName().getString() : "(nothing)";
			System.out.println("Shooting at " + targetName + " with a pull progress of " + pullProgress);

			if (target != null) {
				ProjectileEntity projectile = (ProjectileEntity) this.projectile
					.getConstructor(World.class, LivingEntity.class)
					.newInstance(this.getWorld(), this);

				double[] velocity = new double[] {
					target.getX() - this.getX(),
					target.getBodyY((double) 1 / 2) - projectile.getX(),
					target.getZ() - this.getZ(),
				};
				double variance = Math.sqrt(velocity[0] * velocity[0] + velocity[2] + velocity[2]);
				float divergence = this.getWorld().getDifficulty().getId() * 2;

				projectile.setVelocity(velocity[0], velocity[1] + variance * 0.125F, velocity[2], 2.5F, divergence);
				this.getWorld().spawnEntity(projectile);
				this.playSound(this.getShootSound(), 1F, 1F / (this.random.nextFloat() * 0.4F + 0.8F));
			}

		} catch (InstantiationException
			| IllegalAccessException
			| IllegalArgumentException
			| InvocationTargetException
			| SecurityException
			| NoSuchMethodException e)
		{
			e.printStackTrace(System.out);

			DefensiveMeasures.LOGGER.error("");
			DefensiveMeasures.LOGGER.error("\t {} ERROR OCCURRED\t ", DefensiveMeasures.MOD_ID.toUpperCase());
			DefensiveMeasures.LOGGER.error("===== ERROR MSG START =====");
			DefensiveMeasures.LOGGER.error("LOCALIZED ERROR MESSAGE:");
			DefensiveMeasures.LOGGER.error(e.getLocalizedMessage());
			DefensiveMeasures.LOGGER.error("");
			DefensiveMeasures.LOGGER.error("ERROR MESSAGE:");
			DefensiveMeasures.LOGGER.error(e.getMessage());
			DefensiveMeasures.LOGGER.error("===== ERROR MSG END =====");
			DefensiveMeasures.LOGGER.error("");
		}
	}

	///////////////////
	// LOCAL CLASSES //
	///////////////////

	static class TurretBodyControl extends BodyControl {
		public TurretBodyControl(MobEntity entity) {
			super(entity);
		}

		@Override
		public void tick() {
		}
	}

	static class TurretLookControl extends LookControl {
		public TurretLookControl(MobEntity entity) {
			super(entity);
		}

		@Override
		protected void clampHeadYaw() {
		}
	}

	///////////////////////
	// STATIC INITIALIZE //
	///////////////////////

	static {
		LEVEL = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.INTEGER);
		FROM_ITEM = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.BYTE);

		ATTACHED_FACE = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FACING);
		X = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		Y = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		Z = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
	}
}
