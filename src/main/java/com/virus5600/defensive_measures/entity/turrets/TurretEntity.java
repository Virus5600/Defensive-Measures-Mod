package com.virus5600.defensive_measures.entity.turrets;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
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
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.entity.ai.goal.TargetOtherTeamGoal;
import com.virus5600.defensive_measures.entity.turrets.interfaces.Itemable;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.item.turrets.TurretItem;

import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

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
 * <hr>
 * <h2>Stationary or Mobile:</h2>
 * {@code TurretEntity}, by default, assumes that the turret is a stationary
 * (defined at {@link #isHeldInPlace()} method) entity that can shoot projectiles.
 * This means that the entity will not move even if an explosion occurs near it. This
 * behavior can be overridden by simply overriding the said method and returning {@code false}.
 * However, this also means that the entity's resistance will not revert to {@code 0.0}.
 * If you want the mobile turret to have a bit of resistance, set the value of the
 * knockback resistance attributes to a value less than {@code 1.0} in {@link #getKnockbackResistanceValues()}
 * method.
 * <br>
 * The reason for overwriting the {@code #getResistanceValues()} method is because
 * the {@link #setAttributes()} method is called before any instance of {@code TurretEntity}
 * can be created. To overwrite the default resistance values, the {@code TurretEntity} class
 * updates the resistance values in its constructor if and only if the entity is not held in place.
 * This call utilizes the {@link #getKnockbackResistanceValues()} method to get the resistance values and
 * update the attributes accordingly.
 *
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.0.1
 *
 * @see MobEntity
 * @see Itemable
 * @see RangedAttackMob
 *
 */
public abstract class TurretEntity extends MobEntity implements Itemable, RangedAttackMob {
	/**
	 * Tracks the level (stage) of this turret entity.
	 */
	private static final TrackedData<Integer> LEVEL;
	/**
	 * Tracks whether this entity is spawned from an item.
	 */
	private static final TrackedData<Byte> FROM_ITEM;
	private static final TrackedData<Float> SHOOTING_PITCH;

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
	/**
	 * The maximum health of this turret entity. Change this value using the {@link #setTurretMaxHealth(float)}
	 * method before calling the {@link TurretEntity#setAttributes()} method to set the max health
	 * of this entity properly.
	 *
	 * @see TurretEntity#setTurretMaxHealth(float)
	 * @see TurretEntity#setAttributes()
	 */
	protected static float MAX_HEALTH = 20.0F;

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
	protected EntityType<?> projectile;
	/**
	 * Defines the attack goal of this turret. Uses the {@link ProjectileAttackGoal} class instead
	 * of the default {@link net.minecraft.entity.ai.goal.ProjectileAttackGoal Vanilla ProjectileAttackGoal}.
	 */
	protected ProjectileAttackGoal attackGoal;
	/**
	 * Defines how many projectile spawnpoints (barrels, bolt holder, etc.) this turret has. Useful
	 * for iterating through multiple barrels where the turret can shoot.
	 */
	protected int barrels = 1;
	/**
	 * Determines what barrel is next to shoot the projectile.
	 */
	protected int currentBarrel = 0;
	/**
	 * Defines the level of this turret. The higher the level, the stronger
	 * the turret.
	 */
	protected int level = 1;
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
	 * @param projectile The registry key of the projectile this turret will shoot. (e.g. {@code EntityType.ARROW})
	 * @param itemable The itemable counterpart of this entity.
	 *
	 * @see #itemable
	 * @see EntityType
	 */
	public TurretEntity(EntityType<? extends MobEntity> entityType, World world,
						TurretMaterial material, EntityType<?> projectile, Item itemable
	) {
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
	public TurretEntity(EntityType<? extends MobEntity> entityType, World world,
						TurretMaterial material, Item itemable
	) {
		super(entityType, world);

		this.material = material;
		this.itemable = itemable;
		this.random = Random.create();
		this.lookControl = new TurretEntity.TurretLookControl(this);

		if (this.projectile == null) {
			this.projectile = EntityType.ARROW;
		}

		if (!this.isHeldInPlace()) {
			List.of(
				EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE,
				EntityAttributes.KNOCKBACK_RESISTANCE
			).forEach(attr -> {
				EntityAttributeInstance attrIns = this.getAttributeInstance(attr);
				if (attrIns != null) {
					attrIns.setBaseValue(
						getKnockbackResistanceValues()
							.getOrDefault(attr, 0.0)
					);
				}
			});
		}
	}

	//////////////////
	// INITIALIZERS //
	//////////////////

	/**
	 * {@inheritDoc}
	 * <br><br>
	 * Initializes the standard goals for this entity. These goals are as follows:
	 * <ul>
	 * 	<li>{@link ProjectileAttackGoal} - The goal that allows this entity to shoot projectiles.</li>
	 * 	<li>{@link LookAtEntityGoal} - The goal that allows this entity to look at other entities.</li>
	 * 	<li>{@link LookAroundGoal} - The goal that allows this entity to look around.</li>
	 * 	<li>{@link ActiveTargetGoal} - The goal that allows this entity to target other entities.</li>
	 * 	<li>{@link TargetOtherTeamGoal} - The goal that allows this entity to target entities from other teams.</li>
	 * </ul>
	 * <br>
	 * By default, the {@link ProjectileAttackGoal} is set to {@code null} and can be initialized
	 * by defining the {@link #attackGoal} variable. If the said goal is not defined, then this
	 * entity will use {@code new ProjectileAttackGoal(this, 0, this.getMaxAttackRange(), this.getMinAttackRange())}
	 * as the default attack goal. This will allow the entity to shoot projectiles at a range of
	 * 16 blocks in a 1-second interval.
	 * <br><br>
	 * When changing the attack ranges, you can do so by overriding the {@link #getMaxAttackRange()}
	 * and {@link #getMinAttackRange()} methods to set the maximum and minimum attack ranges of this
	 * turret. However, for more precise control, you can override the {@link #initGoals()} method
	 * and set the {@link ProjectileAttackGoal} with the desired parameters but this still requires
	 * the overriding of both {@link #getMaxAttackRange()} and {@link #getMinAttackRange()} methods.
	 *
	 * @see ProjectileAttackGoal
	 * @see #getMaxAttackRange()
	 * @see #getMinAttackRange()
	 */
	@Override
	protected void initGoals() {
		if (attackGoal == null) {
			this.attackGoal = new ProjectileAttackGoal(this, 0, 20, this.getMaxAttackRange(), this.getMinAttackRange());
		}

		// Goals
		this.goalSelector.add(1, this.attackGoal);
		this.goalSelector.add(2, new LookAtEntityGoal(this, MobEntity.class, 8.0F, 0.02F, false));
		this.goalSelector.add(8, new LookAroundGoal(this));

		// Targets
		this.targetSelector.add(1, new ActiveTargetGoal<>(this, MobEntity.class, 10, true, false, this::targetPredicate));
		this.targetSelector.add(3, new TargetOtherTeamGoal(this));
	}

	/**
	 * A predicate method to identify if an entity is a valid target for this turret. By default,
	 * this predicate checks if the target is a {@link Monster} and if the target is within the
	 * rotation limit of this turret by using the custom {@link ProjectileAttackGoal} class
	 * and its {@link ProjectileAttackGoal#isWithinRotationLimit(LivingEntity) #isWithinRotationLimit(LivingEntity)}
	 * method.
	 * <br><br>
	 * This method can be overridden to add more conditions to the target predicate. For example,
	 * you can add a condition that checks if the target is not a player or if the target is not.
	 * Furthermore, you can completely overwrite this method to create your own custom conditions
	 * similar to what {@link AntiAirTurret#targetPredicate(LivingEntity, ServerWorld)} did; only
	 * targets entities that are airborne.
	 *
	 * @param target The target to check.
	 * @param world The server world where this entity is in.
	 * @return {@code boolean} Returns {@code true} if the target is valid, otherwise {@code false}.
	 */
	protected boolean targetPredicate(LivingEntity target, ServerWorld world) {
		return target instanceof Monster && this.attackGoal.isWithinRotationLimit(target);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);

		// Entity related tracking
		builder.add(LEVEL, this.level)
			.add(FROM_ITEM, (byte) 1)
			.add(SHOOTING_PITCH, 0f)

		// Position related tracking
			.add(ATTACHED_FACE, Direction.DOWN)
			.add(X, 0f)
			.add(Y, 0f)
			.add(Z, 0f);
	}

	/**
	 * Sets the standard attributes for this entity. The attributes that are set are as follows:
	 * <ul>
	 *	<li>
	 * 		<b>{@link EntityAttributes#MAX_HEALTH}</b> - The maximum health of this entity. By
	 * 		default, it is set to 20 like most mobs. You can leave it be or change it to a value
	 * 		more suitable for your entity.
	 * 		<br><br>
	 * 		<b>NOTE:</b> When setting the max health, it is recommended to use the {@link #setTurretMaxHealth(float)}
	 * 		method to set the default max health value for the turret entity since this is the one
	 * 		that is used to set the max health of the entity. Call the said method inside the subclass's
	 * 		{@code setAttributes()} before calling {@code TurretEntity.setAttributes()}.
	 *	</li>
	 * 	<li>
	 * 		<b>{@link EntityAttributes#FOLLOW_RANGE}</b> - The range this entity can follow its
	 * 		target. By default, it is 16 like most mobs.
	 * 	</li>
	 * 	<li>
	 * 		<b>{@link EntityAttributes#MOVEMENT_SPEED}</b> - The speed of this entity. This is by
	 * 		design set to 0 to prevent the entity from moving as it is a stationary turret. If you
	 * 		want to make a mobile turret, you can set this to a value greater than 0.
	 * 	</li>
	 * 	<li>
	 * 		<b>{@link EntityAttributes#KNOCKBACK_RESISTANCE}</b> - The knockback resistance of this
	 * 		entity. This is also by design set to 1.0 to prevent the entity from being knocked back.
	 * 		You can set this to a value less than 1.0 to make the entity more susceptible to
	 * 		knockback.
	 * 	</li>
	 * 	<li>
	 * 		<b>{@link EntityAttributes#EXPLOSION_KNOCKBACK_RESISTANCE}</b> - The explosion knockback
	 * 		resistance of this entity. This is also by design set to 1.0 to prevent the entity from
	 * 		being knocked back by explosions. You can set this to a value less than 1.0 to make the
	 * 		entity more susceptible to knockback from explosions.
	 * 	</li>
	 * </ul>
	 *
	 * <hr>
	 *
	 * Make sure to always call the {@code setAttributes()} method when setting the
	 * local attributes for this class's inheritor to ensure that the attributes are set correctly.
	 * If you plan to change the values of the standard attributes, you can do so by still calling
	 * this method and re-adding the attributes with the new values.
	 * <br><br>
	 * For example:
	 * <pre><code>
	 * TurretEntity.setAttributes()
	 * 	.add(EntityAttributes.FOLLOW_RANGE, 32);
	 * </code></pre>
	 * <br>
	 * This code will overwrite the default {@code FOLLOW_RANGE} attribute value from 16 to 32 blocks
	 * for the entity that calls this method, allowing to set a new {@code FOLLOW_RANGE} value while
	 * also keeping the other default attributes.
	 *
	 * @return {@link DefaultAttributeContainer.Builder} The builder that contains the attributes.
	 *
	 * @see EntityAttributes
	 * @see DefaultAttributeContainer.Builder
	 */
	public static DefaultAttributeContainer.Builder setAttributes() {
		return TurretEntity.createMobAttributes()
			.add(EntityAttributes.MAX_HEALTH, TurretEntity.getTurretMaxHealth())
			.add(EntityAttributes.FOLLOW_RANGE, 16)
			.add(EntityAttributes.MOVEMENT_SPEED, 0)
			.add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0)
			.add(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1.0);
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
			this.setFromItem((byte) 1);
			this.setPersistent();
			return entityData;
		}

		this.setFromItem((byte) 0);
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

	@SuppressWarnings("unchecked")
	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack item = player.getStackInHand(hand);
		boolean isSurvival = !player.isCreative();
		boolean isSuccess = false;

		// Turret Remover Interaction
		if (item.getItem().equals(ModItems.TURRET_REMOVER)) {
			if (isSurvival)
				item.damage(1, player);

			return Itemable.tryItem(player, hand, this, item.getItem(), this.itemable)
				.orElse(super.interactMob(player, hand));
		}

		// Healables
		if (this.isHealableItem(item.getItem())) {
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

	/**
	 * Sets the target of this turret, accounting for the minimum and maximum attack range unlike its
	 * parent method {@link MobEntity#setTarget(LivingEntity)}.
	 *
	 * @param target The target to set. Can be {@code null}.
	 *
	 * @see MobEntity#setTarget(LivingEntity)
	 */
	@Override
	public void setTarget(@Nullable LivingEntity target) {
		// Sets the target when it is null
		if (target == null) {
			super.setTarget(null);
			return;
		}

		// If the target isn't null, check if the target is within range.
		boolean inRange = this.isWithinRange(target, this.getMinAttackRange(), this.getMaxAttackRange());
		if (inRange)
			super.setTarget(target);
	}

	@Override
	@Nullable
	public ItemStack getPickBlockStack() {
		TurretItem turretItem = TurretItem.forEntity(this.getType());
		return turretItem == null ? null : new ItemStack(turretItem);
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

		// CLIENT SIDE
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

			// Head Pitch when shooting
			this.setPitch(this.getTrackedPitch());
		}
		// SERVER SIDE
		else {
			this.dataTracker.set(SHOOTING_PITCH, this.getPitch());

			if (this.getTarget() != null) {
				Vec3d velocity = TurretEntity.TurretProjectileVelocity
					.init(this)
					.setVelocity(this.getTarget())
					.getVelocity();

				float vx = MathHelper.sqrt((float) (velocity.x * velocity.x + velocity.z * velocity.z));
				float p = (float) -Math.atan2(velocity.y, vx);
				p *= (float) (180.0 / Math.PI);
				p = MathHelper.clamp(p, -this.getMaxLookPitchChange(), this.getMaxLookPitchChange());

				this.dataTracker.set(SHOOTING_PITCH, p);
			}

			// Negates the levitation effect
			if (this.hasStatusEffect(StatusEffects.LEVITATION) && this.isHeldInPlace()) {
				this.removeStatusEffect(StatusEffects.LEVITATION);
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

	@Override
	public boolean canTarget(EntityType<?> type) {
		return true;
	}

	public boolean isWithinRange(Entity entity, double minRange, double maxRange) {
		return this.isWithinRange(entity.getX(), entity.getY(), entity.getZ(), minRange, maxRange);
	}

	public boolean isWithinRange(double x, double y, double z, double minRange, double maxRange) {
		double squaredDistance = this.squaredDistanceTo(x, y, z);
		return squaredDistance >= minRange * minRange
			&& squaredDistance <= maxRange * maxRange;
	}

	/**
	 * Determines whether the entity is in close range of this turret.
	 * <br><br>
	 * An entity is in close range if it is within the minimum attack range
	 * and one-third of the maximum attack range.
	 *
	 * @param entity The entity to check.
	 * @return {@code boolean} Returns {@code true} if the entity is in close range, otherwise {@code false}.
	 */
	public boolean isInCloseRange(Entity entity) {
		return this.isWithinRange(
			entity,
			this.getMinAttackRange(),
			this.getMaxAttackRange() / 3
		);
	}

	/**
	 * Determines whether the entity is in mid-range of this turret.
	 * <br><br>
	 * An entity is in mid-range if it is within one-third of the maximum attack range
	 * and two-thirds of the maximum attack range.
	 *
	 * @param entity The entity to check.
	 * @return {@code boolean} Returns {@code true} if the entity is in mid-range, otherwise {@code false}.
	 */
	public boolean isInMidRange(Entity entity) {
		return this.isWithinRange(
			entity,
			this.getMaxAttackRange() / 3,
			this.getMaxAttackRange() * 2 / 3
		);
	}

	/**
	 * Determines whether the entity is in far range of this turret.
	 * <br><br>
	 * An entity is in far range if it is within two-thirds of the maximum attack range
	 * and the maximum attack range.
	 *
	 * @param entity The entity to check.
	 * @return {@code boolean} Returns {@code true} if the entity is in far range, otherwise {@code false}.
	 */
	public boolean isInFarRange(Entity entity) {
		return this.isWithinRange(
			entity,
			this.getMaxAttackRange() * 2 / 3,
			this.getMaxAttackRange()
		);
	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////

	public static float getTurretMaxHealth() {
		return TurretEntity.MAX_HEALTH;
	}

	public static void setTurretMaxHealth(float health) {
		TurretEntity.MAX_HEALTH = health;
	}

	@Override
	protected MoveEffect getMoveEffect() {
		return MoveEffect.NONE;
	}

	public float getTrackedPitch() {
		return this.dataTracker.get(SHOOTING_PITCH);
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
	 * @param offsets The offsets of the point at the local X, Y, and Z-Axis of this turret.
	 *
	 * @return Vec3d the relative position of this point, assuming that the origin of the offset is at <b>[0, 0, 0]</b>
	 */
	public Vec3d getRelativePos(Vec3d offsets) {
		return this.getRelativePos(offsets.getX(), offsets.getY(), offsets.getZ());
	}

	@Override
	public int getMaxLookPitchChange() {
		return 90;
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

	/**
	 * Retrieves the maximum attack range of this turret. The max range is
	 * dependent on the {@code FOLLOW_RANGE} attribute value, allowing the
	 * turret to have a range that is within the bounds of its sight.
	 * <br><br>
	 * The maximum attack range is calculated with the formula:
	 * <pre><code>
	 * 	Math.floor(FOLLOW_RANGE) + eyeHeight
	 * </code></pre>
	 * The additional {@code eyeHeight} is added to the {@code FOLLOW_RANGE} value
	 * to account for the eye displacement used by the attack goal when targeting
	 * entities.
	 *
	 * @return {@code float} The maximum attack range of this turret.
	 *
	 * @see EntityAttributes#FOLLOW_RANGE
	 */
	public float getMaxAttackRange() {
		return (float) Math.floor(
			this.getAttributes()
				.getBaseValue(EntityAttributes.FOLLOW_RANGE)
		) + this.getStandingEyeHeight();
	}

	public float getMinAttackRange() {
		return 0.1f;
	}

	/**
	 * Retrieves the current barrel this turret will use to shoot.
	 *
	 * @param increment Whether to increment the barrel or not.
	 *
	 * @return {@code Vec3d} The current barrel to use for shooting.
	 */
	public Vec3d getCurrentBarrel(boolean increment) {
		int currentBarrel = this.currentBarrel;

		if (increment) {
			this.currentBarrel = this.currentBarrel >= this.getTurretProjectileSpawn().size() - 1 ?
				0 : this.currentBarrel + 1;
		}

		return this.getTurretProjectileSpawn()
			.get(currentBarrel);
	}

	//////////////////
	// TRACKED DATA //
	//////////////////

	/**
	 * Retrieves the maximum level of this turret.
	 * <br><br>
	 * The default maximum level is 3. This can value can be changed
	 * by overriding this method and returning a different value.
	 * <br><br>
	 * This method is also used by {@link #getLevel()} and {@link #setLevel(int)}
	 * to clamp the level between 1 and the maximum level, ensuring that the
	 * level is within the bounds of the maximum level.
	 *
	 * @return {@code int} The maximum level of this turret.
	 */
	public int getMaxLevel() {
		return 3;
	}
	public int getLevel() {
		return Math.clamp(this.dataTracker.get(LEVEL), 1, this.getMaxLevel());
	}
	public void setLevel(int level) {
		this.dataTracker.set(LEVEL, Math.clamp(level, 1, this.getMaxLevel()));
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

	public void copyDataToStack(ItemStack stack) {
		Itemable.copyDataToStack(this, stack);
	}

	@Override
	public void copyDataFromNbt(NbtCompound nbt) {
		Itemable.copyDataFromNbt(this, nbt);
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		this.shoot(target);
	}

	public void shootAt(LivingEntity target) {
		this.shoot(target);
	}

	public void shootAt(TurretProjectileVelocity velocityData) {
		this.shoot(velocityData);
	}

	////////////////////////////////////
	// ABSTRACT & OVERRIDABLE METHODS //
	////////////////////////////////////

	// ABSTRACTS //
	/**
	 * Defines where the turret's projectile spawn is. This is, basically, the barrel of a cannon or
	 * machine gun, or the crossbow's bolt holder.
	 * <br><br>
	 * The returned value should be an instance of a {@link List} of {@link Vec3d} so that it can
	 * also account for turrets that have multiple projectile spawns like for instance, the
	 * dual-barrel AA turret.
	 * <hr>
	 * For manipulating the actual <b>"shoot"</b> behavior, override the {@link #shootAt(LivingEntity, float)}
	 * method of {@link TurretEntity} class.
	 *
	 * @return {@code List<Vec3d>} The list of projectile spawn points.
	 */
	abstract List<Vec3d> getTurretProjectileSpawn();

	/**
	 * Determines the damage this turret's projectile will deal. This only
	 * works for instances of {@link com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity TurretProjectileEntity}
	 * or its subclasses due to the overridden methods and tailored behavior.
	 *
	 * @return {@code double} The damage this turret's projectile will deal.
	 */
	public abstract double getProjectileDamage();

	/**
	 * Determines the knockback this turret's projectile will deal. This only
	 * works for instances of {@link com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity TurretProjectileEntity}
	 * or its subclasses due to the overridden methods and tailored behavior.
	 *
	 * @return {@code byte} The projectile's pierce level.
	 */
	public abstract byte getProjectilePierceLevel();

	// OVERRIDABLES //
	/**
	 * Sets the velocity of a projectile, along with the power and uncertainty of the projectile.
	 * <br><br>
	 * For more optional parameter control, see overloaded versions of this method.
	 * <br><br>
	 * For cases where there's no target, use {@link #setProjectileVelocity(ProjectileEntity, TurretProjectileVelocity)}.
	 *
	 * @param target The target of this entity. This will be used to calculate the velocity of the
	 *                  projectile towards the target's direction.
	 * @param projectile The projectile to set the velocity to.
	 */
	protected void setProjectileVelocity(LivingEntity target, ProjectileEntity projectile) {
		this.setProjectileVelocity(target, projectile, TurretProjectileVelocity.init(this));
	}

	/**
	 * Sets the velocity of a projectile, along with the power and uncertainty of the projectile.
	 *
	 * @param target The target of this entity. This will be used to calculate the velocity of the
	 * @param projectile The projectile to set the velocity to.
	 * @param velocityData The data that will be used to set the velocity of the projectile.
	 */
	protected void setProjectileVelocity(LivingEntity target, ProjectileEntity projectile, TurretProjectileVelocity velocityData) {
		this.setProjectileVelocity(projectile, velocityData.setVelocity(target));
	}

	/**
	 * Sets the velocity of a projectile, along with the power and uncertainty of the projectile. This
	 * method is particularly useful for setting the velocity of a projectile when there is no target,
	 * allowing for more control over the projectile's behavior.
	 * <br><br>
	 * Some potential application of this method is allowing a turret to misfire or shoot in a random
	 * direction, or to shoot in a specific direction without having to target an entity.
	 *
	 * @param projectile The projectile to set the velocity to.
	 * @param velocityData The data that will be used to set the velocity of the projectile.
	 */
	protected void setProjectileVelocity(ProjectileEntity projectile, TurretProjectileVelocity velocityData) {
		double vx = velocityData.getVelocity().getX();
		double vy = velocityData.getVelocity().getY();
		double vz = velocityData.getVelocity().getZ();

		projectile.setVelocity(
			vx, vy, vz,
			velocityData.getPower(),
			velocityData.getUncertainty()
		);
	}

	protected void shoot() {
		this.shoot(TurretProjectileVelocity.init(this));
	}

	protected void shoot(LivingEntity target) {
		this.shoot(
			TurretProjectileVelocity.init(this)
				.setVelocity(target)
		);
	}

	protected void shoot(TurretProjectileVelocity velocityData) {
		try {
			ProjectileEntity projectile = (ProjectileEntity) this.projectile.create(
				this.getWorld(),
				SpawnReason.TRIGGERED
			);

			if (projectile == null) {
				System.err.println("Projectile is null for " + this.getName().getString());
				DefensiveMeasures.LOGGER.info("Projectile is null for {}", this.getName().getString());
				return;
			}

			Vec3d pos = this.getRelativePos(this.getCurrentBarrel(true));
			projectile.setPosition(pos);
			projectile.setOwner(this);
			this.setProjectileVelocity(projectile, velocityData);

			this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
			this.getWorld().spawnEntity(projectile);
			System.out.println("[" + projectile.getName().getString() + "] Gravity: " + projectile.getFinalGravity());
		} catch (IllegalArgumentException | SecurityException e) {
			DefensiveMeasures.printErr(e);
		}
	}

	protected final List<RegistryEntry<EntityAttribute>> resistanceKnockbackResistanceAttributes() {
		return List.of(
			EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE,
			EntityAttributes.KNOCKBACK_RESISTANCE
		);
	}

	/**
	 * An overridable method that defines the base attribute values of this turret
	 * in regard to its knockback resistance. This default implementation respects
	 * the turret's {@link #isHeldInPlace()} method, which dictates whether the turret
	 * is held in place or not. By respecting that method, this method returns either
	 * a set of resistance values that are either 0.0 or 1.0, depending on the turret's
	 * state.
	 * <br><br>
	 * If the turret is held in place, the turret will have a resistance value of 1.0,
	 * lest it will have a resistance value of 0.0.
	 * <hr>
	 * The current list of resistance attributes are:
	 * <ul>
	 * 	<li>{@link EntityAttributes#EXPLOSION_KNOCKBACK_RESISTANCE}</li>
	 * 	<li>{@link EntityAttributes#KNOCKBACK_RESISTANCE}</li>
	 * </ul>
	 *
	 * @return {@code Map<RegistryEntry<EntityAttribute>, Double>} The map of resistance values.
	 */
	protected Map<RegistryEntry<EntityAttribute>, Double> getKnockbackResistanceValues() {
		double baseValue = this.isHeldInPlace() ? 1.0 : 0.0;

		return this.resistanceKnockbackResistanceAttributes()
			.stream()
			.collect(Collectors.toMap(
				attr -> attr,
				_ -> baseValue
			));
	}

	/**
	 * An overridable method that determines whether this turret is held in place or not. This
	 * dictates whether the turret will be affected by certain features such as gravity, knockback,
	 * and other forces that would otherwise move the turret from its position.
	 *
	 * @return {@code boolean} Returns {@code true} if the turret is held in place, otherwise {@code false}.
	 */
	public boolean isHeldInPlace() {
		return true;
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

	/**
	 * A class that holds the velocity data of a turret's projectile. This was created to provide a
	 * simplified way of setting the velocity of a projectile without having to deal with the complex
	 * calculations of the projectile's velocity.
	 * <br><br>
	 * This class can be overridden to provide customized velocity calculations for the projectile,
	 * allowing for more control over the projectile's behavior such as making it shoot up in a
	 * parabolic arc or making it shoot in a straight line.
	 * <br><br>
	 * The default values for the power, uncertainty, and upward velocity multiplier are set to
	 * {@code 1.5f}, {@code 2 * difficulty}, and {@code 0.1f} respectively. These values can be
	 * changed via their respective setters after initializing an instance with the use of the
	 * {@link #init(TurretEntity)} method.
	 *
	 * <hr>
	 *
	 * <h1>Setting the Velocity</h1>
	 * There are several ways to set the velocity of the projectile since by default, the velocity
	 * is set to {@link Vec3d#ZERO}:
	 * <ol>
	 *     <li>Setting the velocity directly using {@link #setVelocity(Vec3d)} or {@link #setVelocity(double, double, double)}</li>
	 *     <li>Setting the velocity using the target's position using {@link #setVelocity(LivingEntity)}</li>
	 * </ol>
	 *
	 * <hr>
	 *
	 * <h1>Properties</h1>
	 * The properties defined in this class are:
	 * <ul>
	 * 	<li>{@link #turret} - The turret entity that will be shooting the projectile</li>
	 * 	<li>{@link #power} - The power of the projectile's velocity</li>
	 * 	<li>{@link #uncertainty} - The distortion of the projectile's trajectory, allowing for inaccuracies</li>
	 * 	<li>{@link #upwardVelocityMultiplier} - The multiplier for the upward velocity of the projectile</li>
	 * 	<li>{@link #velocity} - The velocity of the projectile</li>
	 * </ul>
	 *
	 * Each of these properties can be set using their respective setters and retrieved using their
	 * respective getters.
	 */
	public static class TurretProjectileVelocity {
		/**
		 * Holds the data of the last target position of the projectile. This is used to hold
		 * the last position of the turret's target so when some information is updated, the
		 * projectile velocity can be recalculated.
		 * <br><br>
		 * When the velocity is updated, this value is also updated to the target's position. If
		 * the velocity update does not include a target, then this will be set to {@code null}.
		 */
		@Nullable
		private Vec3d lastTargetPos;
		/** The turret entity that will be shooting the projectile */
		private final TurretEntity turret;
		/** The power of the projectile's velocity */
		private float power;
		/** The distortion of the projectile's trajectory, allowing for inaccuracies */
		private float uncertainty;
		/** The multiplier for the upward velocity of the projectile */
		private float upwardVelocityMultiplier;
		/** The velocity of the projectile */
		private Vec3d velocity;
		/** A flag that determines whether the projectile's trajectory is parabolic */
		private boolean isParabolic;

		/**
		 * Initializes the velocity data of the turret's projectile. This can only be initialized
		 * using the {@link #init(TurretEntity)} method.
		 *
		 * @param turret The turret entity that will be shooting the projectile
		 */
		private TurretProjectileVelocity(TurretEntity turret) {
			this.turret = turret;
			this.power = 1f;
			this.uncertainty = turret.getWorld().getDifficulty().getId() * 2;
			this.upwardVelocityMultiplier = 1f;
			this.velocity = Vec3d.ZERO;
		}

		/**
		 * Creates an instance of the {@link TurretProjectileVelocity} class with the turret entity
		 * that will be shooting the projectile.
		 *
		 * @param turret The turret entity that will be shooting the projectile
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #turret
		 */
		public static TurretProjectileVelocity init(TurretEntity turret) {
			return new TurretProjectileVelocity(turret);
		}

		/**
		 * Sets the power of the projectile's velocity.
		 *
		 * @param power The power of the projectile's velocity
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #power
		 */
		public TurretProjectileVelocity setPower(float power) {
			this.power = power;
			this.recalculateVelocity();
			return this;
		}

		/**
		 * Retrieves the power of the projectile's velocity.
		 * The power scales by 1.5 times the value set as this
		 * was the most optimal value for the projectile's velocity.
		 *
		 * @return float The power of the projectile's velocity
		 *
		 * @see #power
		 */
		public float getPower() {
			return this.power * 1.5f;
		}

		/**
		 * Sets the uncertainty of the projectile's trajectory, allowing for inaccuracies.
		 *
		 * @param uncertainty The distortion of the projectile's trajectory
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #uncertainty
		 */
		public TurretProjectileVelocity setUncertainty(float uncertainty) {
			this.uncertainty = uncertainty;
			this.recalculateVelocity();
			return this;
		}

		/**
		 * Retrieves the uncertainty of the projectile's trajectory.
		 *
		 * @return float The distortion of the projectile's trajectory
		 *
		 * @see #uncertainty
		 */
		public float getUncertainty() {
			return this.uncertainty;
		}

		/**
		 * Sets the multiplier for the upward velocity of the projectile.
		 *
		 * @param upwardVelocityMultiplier The multiplier for the upward velocity of the projectile
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #upwardVelocityMultiplier
		 */
		public TurretProjectileVelocity setUpwardVelocityMultiplier(float upwardVelocityMultiplier) {
			this.upwardVelocityMultiplier = upwardVelocityMultiplier;
			this.recalculateVelocity();
			return this;
		}

		/**
		 * Retrieves the multiplier for the upward velocity of the projectile.
		 * The upward velocity multiplier scales by 0.1 times the value set as this
		 * was the most optimal value for the projectile's velocity.
		 *
		 * @return float The multiplier for the upward velocity of the projectile
		 *
		 * @see #upwardVelocityMultiplier
		 */
		public float getUpwardVelocityMultiplier() {
			return this.upwardVelocityMultiplier * 0.1f;
		}

		/**
		 * Sets the velocity of the projectile. When this method is used to set the velocity, the
		 * projectile's velocity won't respect the following properties:
		 * <ul>
		 * 	<li>{@link #power Power}</li>
		 * 	<li>{@link #upwardVelocityMultiplier Upward Velocity Multiplier}</li>
		 * 	<li>{@link #isParabolic Parabolic Arc}</li>
		 * </ul>
		 *
		 * @param velocity The velocity of the projectile
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #velocity
		 */
		public TurretProjectileVelocity setVelocity(Vec3d velocity) {
			this.calculateVelocity(velocity, false);
			return this;
		}

		/**
		 * Sets the velocity of the projectile. When this method is used to set the velocity, the
		 * projectile's velocity won't respect the following properties:
		 * <ul>
		 * 	<li>{@link #power Power}</li>
		 * 	<li>{@link #upwardVelocityMultiplier Upward Velocity Multiplier}</li>
		 * 	<li>{@link #isParabolic Parabolic Arc}</li>
		 * </ul>
		 *
		 * @param x The X-axis velocity of the projectile
		 * @param y The Y-axis velocity of the projectile
		 * @param z The Z-axis velocity of the projectile
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #velocity
		 */
		public TurretProjectileVelocity setVelocity(double x, double y, double z) {
			return this.setVelocity(new Vec3d(x, y, z));
		}

		/**
		 * Sets the velocity of the projectile using the target's position.
		 *
		 * @param target The target of the projectile
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 */
		public TurretProjectileVelocity setVelocity(LivingEntity target) {
			return this.setVelocityFromPos(
				target.getX(),
				target.getBodyY((double) 1 / 3),
				target.getZ()
			);
		}

		/**
		 * Sets the velocity of the projectile based from the target position given.
		 *
		 * @param pos The target position of the projectile
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #velocity
		 */
		public TurretProjectileVelocity setVelocityFromPos(Vec3d pos) {
			this.calculateVelocity(pos, true);
			return this;
		}

		public TurretProjectileVelocity setVelocityFromPos(double x, double y, double z) {
			return this.setVelocityFromPos(new Vec3d(x, y, z));
		}

		/**
		 * Retrieves the velocity of the projectile.
		 *
		 * @return {@code Vec3d} The velocity of the projectile
		 *
		 * @see #velocity
		 */
		public Vec3d getVelocity() {
			return this.velocity;
		}

		/**
		 * Sets the velocity of the projectile in the direction of where the turret is facing. The
		 * {@code range} parameter is a value from {@code 0} to {@code 1} representing a percentile
		 * of the set turret's attack range. So for example, if the turret's range is {@code 10} and
		 * the {@code range} is set to {@code 0.5}, the approximate distance of the projectile will
		 * be expected to travel is {@code 5} blocks.
		 *
		 * @param range {@code 0} to {@code 1} value representing a percentile of the turret's attack range
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 */
		public TurretProjectileVelocity setDirectionalVelocity(float range) {
			Vec3d targetRange = this.turret
				.getRelativePos(0, 0, this.turret.getMaxAttackRange() * range)
				.subtract(this.turret.getEyePos());

			return this.setVelocity(targetRange);
		}

		/**
		 * Sets the projectile's trajectory to be parabolic. This will make the projectile shoot in
		 * an arc-like trajectory, allowing for the creation of mortar-like projectiles.
		 *
		 * @param isParabolic {@code true} if the projectile's trajectory is parabolic, otherwise {@code false}
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 */
		public TurretProjectileVelocity setParabolic(boolean isParabolic) {
			this.isParabolic = isParabolic;
			this.recalculateVelocity();
			return this;
		}

		/**
		 * Retrieves whether the projectile's trajectory is parabolic.
		 *
		 * @return {@code boolean} {@code true} if the projectile's trajectory is parabolic, otherwise {@code false}
		 */
		public boolean isParabolic() {
			return this.isParabolic;
		}

		/**
		 * Recalculates the velocity of this projectile, accounting all changes.
		 * The method checks if there is a target position set, and if there is,
		 * it will proceed with the recalculation. Otherwise, it will just skip
		 * and continue using the provided velocity from {@link #setVelocity(Vec3d)}.
		 */
		private void recalculateVelocity() {
			if (this.lastTargetPos != null)
				this.calculateVelocity(this.lastTargetPos, true);
		}

		private void calculateVelocity(Vec3d v3d, boolean isPos) {
			if (isPos) {
				Vec3d barrel = this.turret
					.getRelativePos(this.turret.getCurrentBarrel(false));

				double vx = (v3d.getX() - barrel.getX()) * 1.0625;
				double vy = (v3d.getY() - barrel.getY());
				double vz = (v3d.getZ() - barrel.getZ()) * 1.0625;

				this.lastTargetPos = v3d;
				// TODO: Properly calculate the parabolic trajectory (low priority) - Apparently, it was because of the "acceleration" modifier in the "ExplosiveProjectileEntity" class. Fuck
				// Parabolic Trajectory
				if (this.isParabolic) {
					double d3D = Math.sqrt(vx * vx + vy * vy + vz * vz);

					double minAngle = 45 * Math.PI / 180;
					double maxAngle = 80 * Math.PI / 180;
					double angleFactor = Math.clamp(d3D / (d3D + vy), 0.0, 1.0);
					float theta = (float) (minAngle + angleFactor * (maxAngle - minAngle));
					double scalingFactor = Math.tan(theta);
					double scalingFactorSqrd = scalingFactor * scalingFactor;

					vx = (vx / scalingFactorSqrd);
					vy += (Math.abs(vy) * scalingFactor) - vy;
					vz = (vz / scalingFactorSqrd);

					vy *= this.power;
					System.out.println(
						"VY: " + vy +
						" | Power: " + this.getPower() +
						" | rawPower: " + this.power +
						" | scalingFactor: " + scalingFactor
					);
				}
				// Straight Trajectory
				else {
					double variance = Math.sqrt(vx * vx + vz * vz);
					vy += variance * this.getUpwardVelocityMultiplier();
				}

				// Applies a scale factor to make sure that when the power
				// is applied, it will still be around its target's position.
				Vec3d rawV = new Vec3d(vx, vy, vz);
				double magnitude = rawV.length();
				double scaleFactor = magnitude / this.getPower();
				this.velocity = rawV.multiply(scaleFactor);
			}
			else {
				this.lastTargetPos = null;
				this.velocity = v3d;
			}
		}
	}

	///////////////////////
	// STATIC INITIALIZE //
	///////////////////////

	static {
		LEVEL = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.INTEGER);
		FROM_ITEM = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.BYTE);
		SHOOTING_PITCH = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);

		ATTACHED_FACE = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FACING);
		X = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		Y = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
		Z = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FLOAT);
	}
}
