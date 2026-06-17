package com.virus5600.defensive_measures.entity.turrets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.control.TurretLookControl;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.entity.ai.goal.TargetOtherTeamGoal;
import com.virus5600.defensive_measures.entity.projectiles.ExplosiveProjectileEntity;
import com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity;
import com.virus5600.defensive_measures.entity.turrets.interfaces.Itemable;
import com.virus5600.defensive_measures.entity.turrets.tier1.CannonTurretEntity;
import com.virus5600.defensive_measures.entity.turrets.tier2.AATurretEntity;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.item.turrets.TurretItem;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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
 * @see Mob
 * @see Itemable
 * @see RangedAttackMob
 */
public abstract class TurretEntity extends Mob implements Itemable, RangedAttackMob {
	/**
	 * Tracks the level (stage) of this turret entity.
	 */
	private static final EntityDataAccessor<Integer> LEVEL;
	/**
	 * Tracks whether this entity is spawned from an item.
	 */
	private static final EntityDataAccessor<Byte> FROM_ITEM;
	private static final EntityDataAccessor<Float> SHOOTING_PITCH;
	private static final EntityDataAccessor<Boolean> USE_BURST;

	/**
	 * Tracks the direction where this turret is attached to.
	 */
	protected static final EntityDataAccessor<Direction> ATTACHED_FACE;
	/**
	 * Tracks the X position of this turret.
	 */
	protected static final EntityDataAccessor<Float> X;
	/**
	 * Tracks the Y position of this turret.
	 */
	protected static final EntityDataAccessor<Float> Y;
	/**
	 * Tracks the Z position of this turret.
	 */
	protected static final EntityDataAccessor<Float> Z;

	/**
	 * Tracks the amount of projectile to shoot in a burst.
	 */
	protected static final EntityDataAccessor<Integer> BURST_COUNT;
	/**
	 * Tracks the amount of projectiles currently fired in a burst.
	 */
	protected static final EntityDataAccessor<Integer> BURST_PROJECTILE_FIRED;
	/**
	 * Tracks the delay between each shot projectile in a burst.
	 */
	protected static final EntityDataAccessor<Integer> BURST_DELAY;
	protected static final EntityDataAccessor<Boolean> SHOOTING;
	protected static final EntityDataAccessor<Boolean> IS_LOCKED_BUT_NOT_ATTACKING;
	protected static final EntityDataAccessor<Boolean> HAS_TARGET;
	protected static final EntityDataAccessor<Boolean> SETTING_UP;
	protected static final EntityDataAccessor<Boolean> TEARING_DOWN;
	/**
	 * The maximum health of this turret entity. Change this value using the {@link #setTurretMaxHealth(float)}
	 * method before calling the {@link TurretEntity#setAttributes()} method to set the max health
	 * of this entity properly.
	 *
	 * @see TurretEntity#setTurretMaxHealth(float)
	 * @see TurretEntity#setAttributes()
	 * @see Attributes#MAX_HEALTH
	 */
	protected static float MAX_HEALTH = 20.0F;

	/**
	 * The maximum health of this turret entity. Change this value using the {@link #setTurretMaxRange(float)}
	 * method before calling the {@link TurretEntity#setAttributes()} method to set the max range
	 * of this entity properly.
	 *
	 * @see TurretEntity#setTurretMaxRange(float)
	 * @see TurretEntity#setAttributes()
	 * @see Attributes#FOLLOW_RANGE
	 */
	protected static float MAX_RANGE = 16.0F;

	// ////////////////// //
	// INSTANCE VARIABLES //
	// ////////////////// //
	private int setupTicks = 0;
	private int teardownTicks = 0;
	/**
	 * Identifies what item to drop on teardown. When {@code null}, the entity won't drop anything
	 * on retrieval.
	 *
	 * @implNote Use {@link Itemable#tryItem(TurretEntity, Item, Level)} to populate this field's value.
	 */
	private @Nullable ItemEntity itemToDrop = null;
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
	 * 	[MobEffect, duration, amplifier]
	 * 	[MobEffect, duration, amplifier]
	 * 	[MobEffect, duration, amplifier]
	 * ]
	 * </code></pre>
	 */
	private Map<Item, List<Object[]>> effectSource;
	/**
	 * Used by the {@link #tick() tick} method to keep track of the
	 * delay between each shot from a burst.
	 */
	private int burstDelayTimer = 0;
	private TurretProjectileVelocity velocityData = null;
	/**
	 * A simple tracker for when a turret is spawned intentionally without AI. This allows the
	 * setup animation to play without the turret trying to attack and look at targets by
	 * having the turret without AI at first before enabling it if this field's value is {@code false}.
	 */
	private boolean hasAiOnSpawn = false;

	/**
	 * The sound the turret makes when it is being healed.
	 */
	protected SoundEvent healSound = SoundEvents.IRON_GOLEM_REPAIR;
	/**
	 * The sound the turret makes when it is shooting.
	 */
	protected SoundEvent shootSound = SoundEvents.ARROW_SHOOT;
	/**
	 * Defines what kind of projectile this turret will shoot.
	 */
	protected EntityType<?> projectile;
	/**
	 * Defines the attack goal of this turret. Uses the {@link ProjectileAttackGoal} class instead
	 * of the default {@link net.minecraft.world.entity.ai.goal.RangedAttackGoal Vanilla ProjectileAttackGoal}.
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
	 * Tracks how long before the idle animation is played again.
	 */
	protected int idleAnimationTimeout = 0;
	/**
	 * Plays the shoot sound when shooting. {@code true} by default.
	 */
	protected boolean playShootSound = true;

	protected final AnimationState setupAnimationState = new AnimationState();
	protected final AnimationState teardownAnimationState = new AnimationState();
	protected final AnimationState idleAnimationState = new AnimationState();
	protected final AnimationState shootAnimationState = new AnimationState();
	protected final AnimationState deathAnimationState = new AnimationState();

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

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

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
	public TurretEntity(EntityType<? extends Mob> entityType, Level world,
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
	 * @see EntityType
	 */
	public TurretEntity(EntityType<? extends Mob> entityType, Level world,
                        TurretMaterial material, Item itemable
	) {
		super(entityType, world);

		this.material = material;
		this.itemable = itemable;
		this.lookControl = new TurretLookControl(this);

		if (this.projectile == null) {
			this.projectile = EntityType.ARROW;
		}

		if (!this.isHeldInPlace()) {
			List.of(
				Attributes.EXPLOSION_KNOCKBACK_RESISTANCE,
				Attributes.KNOCKBACK_RESISTANCE
			).forEach(attr -> {
				AttributeInstance attrIns = this.getAttribute(attr);
				if (attrIns != null) {
					attrIns.setBaseValue(
						getKnockbackResistanceValues()
							.getOrDefault(attr, 0.0)
					);
				}
			});
		}
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //

	/**
	 * {@inheritDoc}
	 * <br><br>
	 * Initializes the standard goals for this entity. These goals are as follows:
	 * <ul>
	 * 	<li>{@link ProjectileAttackGoal} - The goal that allows this entity to shoot projectiles.</li>
	 * 	<li>{@link LookAtPlayerGoal} - The goal that allows this entity to look at other entities.</li>
	 * 	<li>{@link RandomLookAroundGoal} - The goal that allows this entity to look around.</li>
	 * 	<li>{@link NearestAttackableTargetGoal} - The goal that allows this entity to target other entities.</li>
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
	 * turret. However, for more precise control, you can override the {@link #registerGoals()} method
	 * and set the {@link ProjectileAttackGoal} with the desired parameters but this still requires
	 * the overriding of both {@link #getMaxAttackRange()} and {@link #getMinAttackRange()} methods.
	 *
	 * @see ProjectileAttackGoal
	 * @see #getMaxAttackRange()
	 * @see #getMinAttackRange()
	 */
	@Override
	protected void registerGoals() {
		if (attackGoal == null) {
			this.attackGoal = new ProjectileAttackGoal(this, 0, 20, this.getMaxAttackRange(), this.getMinAttackRange());
		}

		// Goals
		this.goalSelector.addGoal(1, this.attackGoal);
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Mob.class, 8.0F, 0.02F, false));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

		// Targets
		this.targetSelector.addGoal(1, this.getActiveTargetGoal());
		this.targetSelector.addGoal(3, new TargetOtherTeamGoal(this));
	}

	/**
	 * A method that returns the {@link NearestAttackableTargetGoal} of this turret. By default, this method returns an
	 * instance of {@link NearestAttackableTargetGoal} that targets {@link Mob} with a target chance of 10
	 * and uses the {@link #targetPredicate(LivingEntity, ServerLevel) targetPredicate} method to
	 * identify valid targets. This method can be overridden to change the target type, target chance,
	 * and whether to check visibility and reachability of the target.
	 *
	 * @return {@link NearestAttackableTargetGoal}
	 */
	protected NearestAttackableTargetGoal<?> getActiveTargetGoal() {
		return new NearestAttackableTargetGoal<>(
			this, Mob.class, 10,
			true, false,
			this::targetPredicate
		);
	}

	/**
	 * A predicate method to identify if an entity is a valid target for this turret. By default,
	 * this predicate checks if the target is a {@link Enemy} and if the target is within the
	 * rotation limit of this turret by using the custom {@link ProjectileAttackGoal} class
	 * and its {@link ProjectileAttackGoal#isWithinRotationLimit(LivingEntity) #isWithinRotationLimit(LivingEntity)}
	 * method.
	 * <br><br>
	 * This method can be overridden to add more conditions to the target predicate. For example,
	 * you can add a condition that checks if the target is not a player or if the target is not.
	 * Furthermore, you can completely overwrite this method to create your own custom conditions
	 * similar to what {@link AATurretEntity#targetPredicate(LivingEntity, ServerLevel)} did; only
	 * targets entities that are airborne.
	 *
	 * @param target The target to check.
	 * @param world The server world where this entity is in.
	 * @return {@code boolean} Returns {@code true} if the target is valid, otherwise {@code false}.
	 */
	protected boolean targetPredicate(LivingEntity target, ServerLevel world) {
		return target instanceof Enemy && this.attackGoal.isWithinRotationLimit(target);
	}

	@Override
	protected void defineSynchedData(@NonNull Builder builder) {
		super.defineSynchedData(builder);

		// Entity related tracking
		builder.define(LEVEL, this.level)
			.define(FROM_ITEM, (byte) 1)
			.define(SHOOTING_PITCH, 0f)

		// Position related tracking
			.define(ATTACHED_FACE, Direction.DOWN)
			.define(X, 0f)
			.define(Y, 0f)
			.define(Z, 0f)

		// Shooting related tracking
			.define(USE_BURST, false)
			.define(BURST_COUNT, 0)
			.define(BURST_PROJECTILE_FIRED, 0)
			.define(BURST_DELAY, 0)
			.define(SHOOTING, false)
			.define(IS_LOCKED_BUT_NOT_ATTACKING, false)
			.define(HAS_TARGET, false)

		// Setup and Teardown related tracking
			.define(SETTING_UP, false)
			.define(TEARING_DOWN, false)

		;
	}

	/**
	 * Sets the standard attributes for this entity. The default attributes that are set are as follows:
	 * <ul>
	 *	<li>
	 * 		<b>{@link Attributes#MAX_HEALTH}</b> - The maximum health of this entity. By
	 * 		default, it is set to 20 like most mobs. You can leave it be or change it to a value
	 * 		more suitable for your entity.
	 * 		<br><br>
	 * 		<b>NOTE:</b> When setting the max health, it is recommended to use the {@link #setTurretMaxHealth(float)}
	 * 		method to set the default max health value for the turret entity since this is the one
	 * 		that is used to set the max health of the entity. Call the said method inside the subclass's
	 * 		{@code setAttributes()} before calling {@code TurretEntity.setAttributes()}.
	 *	</li>
	 * 	<li>
	 * 		<b>{@link Attributes#FOLLOW_RANGE}</b> - The range this entity can follow its
	 * 		target. By default, it is 16 like most mobs.
	 * 	</li>
	 * 	<li>
	 * 		<b>{@link Attributes#MOVEMENT_SPEED}</b> - The speed of this entity. This is by
	 * 		design set to 0 to prevent the entity from moving as it is a stationary turret. If you
	 * 		want to make a mobile turret, you can set this to a value greater than 0.
	 * 	</li>
	 * 	<li>
	 * 		<b>{@link Attributes#KNOCKBACK_RESISTANCE}</b> - The knockback resistance of this
	 * 		entity. This is also by design set to 1.0 to prevent the entity from being knocked back.
	 * 		You can set this to a value less than 1.0 to make the entity more susceptible to
	 * 		knockback.
	 * 	</li>
	 * 	<li>
	 * 		<b>{@link Attributes#EXPLOSION_KNOCKBACK_RESISTANCE}</b> - The explosion knockback
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
	 * @return {@link net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder} The builder that contains the attributes.
	 *
	 * @see Attributes
	 * @see net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder
	 */
	public static AttributeSupplier.Builder setAttributes() {
		return TurretEntity.createMobAttributes()
			.add(Attributes.MAX_HEALTH, TurretEntity.getTurretMaxHealth())
			.add(Attributes.FOLLOW_RANGE, TurretEntity.getTurretMaxRange())
			.add(Attributes.MOVEMENT_SPEED, 0)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
			.add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1.0);
	}

	@Override @NonNull
	protected BodyRotationControl createBodyControl() {
		return new TurretBodyControl(this);
	}

	@Override @Nullable
	public SpawnGroupData finalizeSpawn(@NonNull ServerLevelAccessor world, @NonNull DifficultyInstance difficulty, @NonNull EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
		this.yHeadRot = this.yHeadRotO;
		this.yBodyRotO = 0.0f;
		this.setOldPosAndRot();

		this.startSetupAnim();

		if (spawnReason == EntitySpawnReason.SPAWN_ITEM_USE) {
			this.setFromItem((byte) 1);

			return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
		}

		this.setFromItem((byte) 0);
		return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	private void startSetupAnim() {
		if (this.isSettingUp()) return;

		this.hasAiOnSpawn = !this.isNoAi();

		this.setNoAi(true);
		this.setSettingUpStatus(true);
		this.setupAnimationState.start(this.tickCount);
	}

	private void endSetupAnim() {
		this.setNoAi(!this.hasAiOnSpawn);
	}

	private void startTeardownAnim() {
		this.hasAiOnSpawn = !this.isNoAi();

		this.setNoAi(true);
		this.setTearingDownStatus(true);
		this.teardownAnimationState.start(this.tickCount);
	}

	private void endTeardownAnim() {
		if (this.itemToDrop != null) {
			this.level().addFreshEntity(this.itemToDrop);
			this.itemToDrop = null;
		}

		this.setNoAi(!this.hasAiOnSpawn);
		this.discard();
	}

	/**
	 * An optional overridable method that is called when a projectile is created by this turret.
	 * This method can be used to modify attributes and various aspects of the assigned projectile
	 * that this turret shoots.<br>
	 * <br>
	 * By default, the base method ({@link TurretEntity#onProjectileCreateCallback(Projectile)})
	 * does nothing.
	 *
	 * @param projectile The projectile that is created by this turret.
	 * @param <P>        The type of the projectile, which must be a subclass of {@link Projectile}.
	 */
	@SuppressWarnings("JavadocDeclaration")
	protected <P extends Projectile> void onProjectileCreateCallback(P projectile) {
	}

	/**
	 * Updates the tracked data for whether this turret is locked but not attacking. This is used
	 * to determine whether the turret is in the "charging" state, which is when the turret is
	 * locked on to a target but has not yet attacked. The default value for the
	 * {@code afterAttackTick} parameter is 5, which means that the turret will be considered as
	 * "charging" if it is locked on to a target for more than 5 ticks after the last attack.
	 *
	 * @see #updateTrackedLockedButNotAttacking(int)
	 *
	 * @apiNote This method can be overloaded to change the default value of the {@code afterAttackTick}
	 * parameter. To change the said value, simply override this method and call the overloaded
	 * version with the desired value for the {@code afterAttackTick} parameter.
	 */
	protected void updateTrackedLockedButNotAttacking() {
		this.updateTrackedLockedButNotAttacking(5);
	}

	/**
	 * Updates the tracked data for whether this turret is locked but not attacking. This is used
	 * to determine whether the turret is in the "charging" state, which is when the turret is
	 * locked on to a target but has not yet attacked. The default value for the
	 * {@code afterAttackTick} parameter is 5 (defined via the
	 * {@link #updateTrackedLockedButNotAttacking()}), which means that the turret will be
	 * considered as "charging" if it is locked on to a target for more than 5 ticks after the last
	 * attack.
	 *
	 * @param afterAttackTick The number of ticks after the last attack before the turret is
	 *                           considered as "charging". The default value is 5 ticks.
	 *
	 * @see #updateTrackedLockedButNotAttacking()
	 *
	 * @apiNote This method is considered final and designed to only be called by the overloaded
	 * version with no parameter. This method should also not be called again inside the
	 * {@link #tick()} method as the overloaded one is the method being called inside said method. To
	 * change the {@code afterAttackTick} value, override the overload method and call this inside
	 * said method, passing the desired value for the {@code afterAttackTick} parameter.
	 */
	protected final void updateTrackedLockedButNotAttacking(int afterAttackTick) {
		int updateCountdownTicks = this.attackGoal.getUpdateCountdownTicks(),
			beforeAttackTick = this.getTotalAttCooldown() - afterAttackTick;

		// Marked as charging if the countdown ticks is larger than the designated "shoot" time
		// which is defined as afterAttackTick.
		boolean isCharging = updateCountdownTicks > afterAttackTick &&
			updateCountdownTicks < beforeAttackTick;

		this.setTrackedLockedButNotAttacking(isCharging);
	}

	@Override
	protected void tickDeath() {
		boolean forRemoval = this.deathTime >= this.getDeathAnimDuration();
		boolean isMainThread = !this.level().isClientSide();
		boolean stillExisting = !this.isRemoved();

		++this.deathTime;
		if (forRemoval && isMainThread && stillExisting) {
			this.level().broadcastEntityEvent(this, EntityEvent.POOF);
			this.remove(RemovalReason.KILLED);
		}
	}

	/**
	 * Attempts to attach this entity to a block or fall if there is no block to attach to
	 * at the current position. If the entity is attached to a block, it will not fall but
	 * will instead snap to the block's position. However, if the entity is not attached to
	 * a block, it will look for a block to attach to around it and if there is none, it will
	 * fall.
	 */
	protected void tryAttachOrFall() {
		Direction dir = this.findAttachableSide(this.blockPosition());
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
		if (this.isNoAi() || !this.isAlive())
			return false;

		BlockPos blockPos = this.blockPosition().offset(new Vec3i(0, -1, 0));
		if (this.isValidFallingPosition(blockPos)) {
			this.setDeltaMovement(super.getDeltaMovement().scale(0.98));
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
	@Override @NonNull
	protected InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		boolean isSurvival = !player.isCreative();
		boolean isSuccess = false;

		// Turret Remover Interaction
		if (!this.level().isClientSide() && item.getItem().equals(ModItems.TURRET_REMOVER)
			&& !this.level().isClientSide()) {
			if (isSurvival) {
				item.hurtAndBreak(1, player, hand.asEquipmentSlot());
			}

			this.startTeardownAnim();

			// Only drop when the player is in survival, or if in creative, if sneaking.
			if (isSurvival || (player.isCreative() && player.isShiftKeyDown())) {
				this.itemToDrop = Itemable.tryItem(this, this.itemable, this.level());
			}

			return Itemable.tryItem(player, hand, this, item.getItem(), this.itemable, false, false)
				.orElse(super.mobInteract(player, hand));
		}

		// Conditions for applying healables and effects
		boolean isHealable = this.isHealableItem(item.getItem());
		boolean isEffSrc = this.isEffectSource(item.getItem());

		boolean isDamaged = this.getHealth() < this.getMaxHealth();

		// Healables - Only heal when damaged, preventing item usage when entity doesn't need healing
		if (isHealable && isDamaged) {
			// Heals the turret
			this.heal(this.getHealAmt(item.getItem()));

			// Indicates a repair was done
			float pitch = 1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
			this.playSound(this.getHealSound(), 1F, pitch);

			isSuccess = true;
		}

		// Effect Source - Only apply when item has effect properties
		if (isEffSrc) {
			for (Object[] args : this.getMobEffect(item.getItem())) {
				this.addEffect(
					new MobEffectInstance(
						(Holder<MobEffect>) args[0],
						(int) ((float) ((Integer) args[1]) * 20),
						(Integer) args[2]
					)
				);
			}

			if (!isSuccess) {
				isSuccess = true;

				// Indicates an effect was applied
				float pitch = 1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
				this.playSound(this.getHealSound(), 1F, pitch);
			}

		}

		if (isSuccess) {
			if (item.isDamageableItem()) {
				item.hurtWithoutBreaking(1, player);
			}
			else {
				item.consume(1, player);
			}

			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(player, hand);
	}

	@Override
	protected float getBlockJumpFactor() {
		return 0.0f;
	}

	@Override
	public void readAdditionalSaveData(@NonNull ValueInput view) {
		super.readAdditionalSaveData(view);
		this.setTrackedLevel(view.getInt("Level").orElse(this.level));
		this.setFromItem(view.getByteOr("FromItem", this.isFromItem()));
	}

	@Override
	public boolean startRiding(@NonNull Entity entity, boolean force, boolean emitEvent) {
		if (this.level().isClientSide()) {
			this.prevAttachedBlock = null;
		}

		this.setAttachedFace(Direction.DOWN);
		return super.startRiding(entity, force, emitEvent);
	}

	@Override
	public void stopRiding() {
		super.stopRiding();

		if (this.level().isClientSide()) {
			this.prevAttachedBlock = this.blockPosition();
		}

		this.yBodyRotO = 0.0f;
		this.yBodyRot = 0.0f;
	}

	/**
	 * Sets the target of this turret, accounting for the minimum and maximum attack range unlike its
	 * parent method {@link Mob#setTarget(LivingEntity)}.
	 *
	 * @param target The target to set. Can be {@code null}.
	 *
	 * @see Mob#setTarget(LivingEntity)
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
		if (inRange) {
			super.setTarget(target);
		}
	}

	@Override
	@Nullable
	public ItemStack getPickResult() {
		TurretItem turretItem = TurretItem.forEntity(this.getType());
		return turretItem == null ? null : new ItemStack(turretItem);
	}

	@Override @NonNull
	public Vec3 getDeltaMovement() {
		if (this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(Blocks.BUBBLE_COLUMN)
			|| this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement().offset(0, 1, 0)).is(Blocks.BUBBLE_COLUMN)
			|| this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(Blocks.WATER)
			|| this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement().offset(0, 1, 0)).is(Blocks.WATER)
			|| this.getAttachedFace() != Direction.DOWN)
			return new Vec3(0, -Math.abs(super.getDeltaMovement().y()), 0);

		return super.getDeltaMovement();
	}

	@Override
	public void die(@NonNull DamageSource damageSource) {
		if (!this.level().isClientSide() && this.hasCustomName()) {
			DefensiveMeasures.LOGGER.info("Named entity {} died: {}", this, this.getCombatTracker().getDeathMessage().getString());
		}

		super.die(damageSource);
	}

	@Override
	public void tick() {
		super.tick();

		// CLIENT SIDE
		if (this.level().isClientSide()) {
			// Play Animations
			this.updateAnimations();
		}
		// SERVER SIDE
		else {
			if (this.isSettingUp()) {
				this.setupTicks++;
				if (this.setupTicks >= this.getSetupAnimDuration()) {
					this.setSettingUpStatus(false);
				}
			}

			if (this.isTearingDown()) {
				this.teardownTicks++;
				if (this.teardownTicks >= this.getTeardownAnimDuration()) {
					this.setTearingDownStatus(false);
				}
			}

			// Handles the "locked but not attacking" data.
			this.updateTrackedLockedButNotAttacking();

			boolean hasTarget = this.getTarget() != null;
			if (this.hasTarget() == hasTarget) {
				this.setHasTarget(hasTarget);
			}

			// SNAPPING THE TURRET BACK IN PLACE
			if (this.getDeltaMovement().x == 0 && this.getDeltaMovement().z == 0 && !this.isPassenger()) {
				double offset = this.getType().getDimensions().width() % 2 == 0 ?
					0 : 0.5;

				Vec3 newPos = new Vec3(
					this.getBlockX() + offset,
					this.getY(),
					this.getBlockZ() + offset
				);

				this.tryAttachOrFall();
				this.setPos(newPos);
				this.level().gameEvent(this, GameEvent.TELEPORT, newPos);

				if (this.getDeltaMovement() == Vec3.ZERO && this.level().isClientSide() && !this.isPassenger()) {
					this.xOld = this.getX();
					this.yOld = this.getY();
					this.zOld = this.getZ();
				}
			}

			// Negates the levitation effect
			if (this.hasEffect(MobEffects.LEVITATION) && this.isHeldInPlace()) {
				this.removeEffect(MobEffects.LEVITATION);
			}

			// When using the burst attack
			if (this.getTrackedUseBurst()) {
				if (this.getTarget() == null) {
					this.resetBurst();
				}
				else {
					// Set the burst data into vars
					int burstCount = this.entityData.get(BURST_COUNT);
					int burstFired = this.entityData.get(BURST_PROJECTILE_FIRED);
					int burstDelay = this.entityData.get(BURST_DELAY);

					// Once the burst delay is done...
					if (--this.burstDelayTimer <= 0) {

						// ... shoot the projectile
						if (this.velocityData == null) {
							this.shoot(this.getTarget());
						}
						else {
							this.shoot(this.velocityData);
						}
						burstFired++;

						// ... check if the fired projectile is equal to the burst count
						if (burstFired >= burstCount) {
							// If it is, reset the burst data
							this.resetBurst();
						}
						// Otherwise, update tracked data from vars
						else {
							this.burstDelayTimer = burstDelay;

							this.entityData.set(BURST_COUNT, burstCount);
							this.entityData.set(BURST_PROJECTILE_FIRED, burstFired);
						}
					}
				}
			}

			// Using the idle pitch
			if (this.getIdlePitch().isPresent()) {
				this.setTrackedLockedButNotAttacking(this.getTarget() != null);

				float idlePitch = this.getIdlePitch().get();

				if (this.getTarget() == null) {
					this.setXRot(idlePitch);
					this.setTrackedPitch(idlePitch);
				}
			}

			this.setTrackedPitch(this.getXRot());
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (!(this.level().isClientSide()
			|| this.isPassenger()
			|| this.canStay(this.blockPosition(), this.getAttachedFace()))) {
			this.tryAttachOrFall();
		}
	}

	/**
	 * Resets the burst attack data.
	 * <br><br>
	 * When burst attacking, turrets will tend to stop mid-way if the
	 * target does not exist or is out of range anymore. This results to
	 * dirty data which may potentially carry over to the next attack.
	 * <br><br>
	 * This method serves as a cleaner for that issue (unless you wanted
	 * that behavior to happen), resetting tracked data and timers to
	 * its default state.
	 * <br><br>
	 * Lastly, this method is made {@code public} to allow other classes
	 * to call it if needed.
	 */
	public void resetBurst() {
		this.setTrackedUseBurst(false);
		this.entityData.set(BURST_COUNT, 0);
		this.entityData.set(BURST_PROJECTILE_FIRED, 0);
		this.entityData.set(BURST_DELAY, 0);
		this.burstDelayTimer = 0;
		this.velocityData = null;
	}

	// //////////////////////////////// //
	// QUESTION METHODS (True or False) //
	// //////////////////////////////// //

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

		if (!this.level().loadedAndEntityCanStandOnFace(pos.relative(dir), this, opposite))
			return false;

		AABB box = this.makeBoundingBox().move(pos).deflate(1.0E-6);
		return this.level().noCollision(this, box);
	}

	/**
	 * Identifies whether the position is a valid falling position for this turret.
	 * Blocks that are considered valid falling blocks are air, water, and bubble columns.
	 * @param pos The position to check.
	 * @return {@code boolean} Returns {@code true} if the position is valid, otherwise {@code false}.
	 */
	protected boolean isValidFallingPosition(BlockPos pos) {
		BlockState bState = this.level().getBlockState(pos);

		if (bState.isAir()
			|| (bState.is(Blocks.BUBBLE_COLUMN) && pos.equals(this.blockPosition()))
			|| (bState.is(Blocks.WATER) && pos.equals(this.blockPosition()))
		)
			return true;

		return !(bState.is(Blocks.MOVING_PISTON) && pos.equals(this.blockPosition()));
	}

	public boolean removeWhenFarAway(final double distSqr) {
		return false;
	}

	/**
	 * Identifies whether the submitted item is part of the healable map.
	 * @param item The item in question
	 * @return {@code boolean}
	 */
	public boolean isHealableItem(Item item) {
		return this.healables.containsKey(item);
	}

	/**
	 * Identifies whether the submitted item is part of the effect source map.
	 * @param item The item in question
	 *
	 * @return {@code boolean}
	 *
	 * @see MobEffect
	 */
	public boolean isEffectSource(Item item) {
		return this.effectSource.containsKey(item);
	}

	/**
	 * Identifies whether this item already have the effect in its list or not. This iterates through all the entries
	 *
	 * @param item The item in question
	 * @param effect The effect to check
	 *
	 * @return {@code boolean}
	 */
	public boolean effectSourceHasEffect(Item item, MobEffect effect) {
		List<Object[]> mobEffects = this.getMobEffect(item);
		if (mobEffects!= null)
			for (Object[] registeredEffect : mobEffects)
				if (registeredEffect[0] == effect)
					return true;
		return false;
	}

	@Override
	public boolean hasLineOfSight(Entity entity, ClipContext.@NonNull Block shapeType, ClipContext.@NonNull Fluid fluidHandling, double entityY) {
		if (entity.level() != this.level()) {
			return false;
		} else {
			Vec3 vec3 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
			Vec3 vec3d2 = new Vec3(entity.getX(), entityY, entity.getZ());
			if (vec3d2.distanceTo(vec3) > this.getMaxAttackRange()) {
				return false;
			} else {
				return this.level().clip(new ClipContext(vec3, vec3d2, shapeType, fluidHandling, this)).getType() == HitResult.Type.MISS;
			}
		}
	}

	@Override
	public boolean canBeLeashed() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean canAttack(@NonNull LivingEntity target) {
		return (!(target instanceof Player) || this.level().getDifficulty() != Difficulty.PEACEFUL) && target.canBeSeenAsEnemy();
	}

	public boolean isWithinRange(Entity entity, double minRange, double maxRange) {
		return this.isWithinRange(entity.getX(), entity.getY(), entity.getZ(), minRange, maxRange);
	}

	public boolean isWithinRange(double x, double y, double z, double minRange, double maxRange) {
		double squaredDistance = this.distanceToSqr(x, y, z);
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

	// //////////// //
	// TRACKED DATA //
	// //////////// //

	public boolean isSettingUp() {
		return this.entityData.get(SETTING_UP);
	}

	public final void setSettingUpStatus(boolean status) {
		this.entityData.set(SETTING_UP, status);

		if (!status) {
			this.endSetupAnim();
		}
	}

	public boolean isTearingDown() {
		return this.entityData.get(TEARING_DOWN);
	}

	public final void setTearingDownStatus(boolean status) {
		this.entityData.set(TEARING_DOWN, status);

		if (!status) {
			this.endTeardownAnim();
		}
	}

	public int getTrackedLevel() {
		return Math.clamp(this.entityData.get(LEVEL), 1, this.getMaxLevel());
	}

	public void setTrackedLevel(int level) {
		this.entityData.set(LEVEL, Math.clamp(level, 1, this.getMaxLevel()));
	}

	public float getTrackedPitch() {
		return this.entityData.get(SHOOTING_PITCH);
	}

	public void setTrackedPitch(float pitch) {
		this.entityData.set(SHOOTING_PITCH, pitch);
	}

	public boolean getTrackedUseBurst() {
		return this.entityData.get(USE_BURST);
	}

	public void setTrackedUseBurst(boolean usingBurst) {
		this.entityData.set(USE_BURST, usingBurst);
	}

	/**
	 * Retrieves the direction where this turret is attached to.
	 * @return {@code Direction} The direction where this turret is attached to.
	 */
	protected Direction getAttachedFace() {
		return this.entityData.get(ATTACHED_FACE);
	}

	/**
	 * Sets the direction where this will be attached to.
	 * @param dir The direction to attach to.
	 */
	protected void setAttachedFace(Direction dir) {
		this.entityData.set(ATTACHED_FACE, dir);
	}

	public void setTrackedShooting(boolean shooting) {
		this.entityData.set(SHOOTING, shooting);
	}

	public boolean getTrackedShooting() {
		return this.entityData.get(SHOOTING);
	}

	public void setTrackedLockedButNotAttacking(boolean locked) {
		this.entityData.set(IS_LOCKED_BUT_NOT_ATTACKING, locked);
	}

	public boolean getTrackedLockedButNotAttacking() {
		return this.entityData.get(IS_LOCKED_BUT_NOT_ATTACKING);
	}

	public void setHasTarget(boolean locked) {
		this.entityData.set(HAS_TARGET, locked);
	}

	public boolean hasTarget() {
		return this.entityData.get(HAS_TARGET);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource source) {
		return this.getTurretMaterial().equals(TurretMaterial.METAL) ?
			ModSoundEvents.TURRET_HURT_METAL : ModSoundEvents.TURRET_HURT_WOOD;
	}

	/**
	 * Retrieves the maximum level of this turret.
	 * <br><br>
	 * The default maximum level is 3. This can value can be changed
	 * by overriding this method and returning a different value.
	 * <br><br>
	 * This method is also used by {@link #getTrackedLevel()} and {@link #setTrackedLevel(int)}
	 * to clamp the level between 1 and the maximum level, ensuring that the
	 * level is within the bounds of the maximum level.
	 *
	 * @return {@code int} The maximum level of this turret.
	 */
	public int getMaxLevel() {
		return 3;
	}

	@Override @NonNull
	public TurretLookControl getLookControl() {
		return (TurretLookControl) this.lookControl;
	}

	public static float getTurretMaxHealth() {
		return TurretEntity.MAX_HEALTH;
	}

	public static void setTurretMaxHealth(float health) {
		TurretEntity.MAX_HEALTH = health;
	}

	public static float getTurretMaxRange() {
		return TurretEntity.MAX_RANGE;
	}

	public static void setTurretMaxRange(float range) {
		TurretEntity.MAX_RANGE = range;
	}

	@Override @NonNull
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	/**
	 * Identifies the position of a point relative to this turret's rotation and
	 * {@link #getEyePosition()} eye position}.
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
	 * @return Vec3 the relative position of this point, assuming that the origin of the offset is at <b>[0, 0, 0]</b>
	 */
	public Vec3 getRelativePos(double xOffset, double yOffset, double zOffset) {
		return MathUtil.getRelativePos(
			this.getEyePosition(),
			xOffset, yOffset, zOffset,
			-this.getYHeadRot(), -this.getXRot()
		);
	}

	/**
	 * Identifies the position of a point relative to the origin provided.
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param origin The origin that the calculations will use.
	 * @param xOffset The offset of the point at the local X-Axis of this turret.
	 * @param yOffset The offset of the point at the local Y-Axis of this turret.
	 * @param zOffset The offset of the point at the local Z-Axis of this turret.
	 * @param includePitch Whether to include the pitch rotation in the calculations or not.
	 *
	 * @return Vec3 the relative position of this point, assuming that the origin of the offset is at <b>[0, 0, 0]</b>
	 */
	public Vec3 getRelativePosFrom(Vec3 origin, double xOffset, double yOffset, double zOffset, boolean includePitch) {
		if (includePitch) {
			return MathUtil.getRelativePos(
				origin,
				xOffset, yOffset, zOffset,
				-this.getYHeadRot(), -this.getXRot()
			);
		}

		return MathUtil.getRelativePos(
			origin,
			xOffset, yOffset, zOffset,
			-this.getYHeadRot()
		);
	}

	/**
	 * Identifies the position of a point relative to this turret's rotation and
	 * {@link #getEyeY()} eye position}.
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param offsets The offsets of the point at the local X, Y, and Z-Axis of this turret.
	 *
	 * @return Vec3 the relative position of this point, assuming that the origin of the offset is at <b>[0, 0, 0]</b>
	 */
	public Vec3 getRelativePos(Vec3 offsets) {
		return this.getRelativePos(offsets.x(), offsets.y(), offsets.z());
	}

	/**
	 * Identifies the position of a point relative to this turret's rotation and
	 * provided origin.
	 * <br><br>
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param origin The origin that the calculations will use.
	 * @param offsets The offsets of the point at the local X, Y, and Z-Axis of this turret.
	 * @param includePitch Whether to include the pitch rotation in the calculations or not.
	 *
	 * @return Vec3 the relative position of this point, assuming that the origin of the offset is at <b>[0, 0, 0]</b>
	 */
	public Vec3 getRelativePosFrom(Vec3 origin, Vec3 offsets, boolean includePitch) {
		return this.getRelativePosFrom(origin, offsets.x(), offsets.y(), offsets.z(), includePitch);
	}

	@Override
	public int getMaxHeadXRot() {
		return 90;
	}

	public int getMinHeadXRot() {
		return -this.getMaxHeadYRot();
	}

	@Override
	public int getMaxHeadYRot() {
		return 360;
	}

	public int getMinHeadYRot() {
		return -this.getMaxHeadYRot();
	}

	@Override
	public int getAmbientSoundInterval() {
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
	 * @see MobEffect
	 * @see TurretEntity
	 */
	public TurretEntity addEffectSource(Item item, MobEffect effect, float duration, int amplifier) {
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
	 * @see MobEffect
	 * @see TurretEntity
	 */
	public TurretEntity addEffectSource(List<Item> group, MobEffect effect, float duration, int amplifier) {
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
	 * @param effectSource A key-value pair made from a {@code Map}, The map uses the {@code Item} as the key, and a {@code List} of {@code Object} array for the effect. The array should only have three items in order: {@code MobEffect}, {@code duration}, and {@code amplifier}.
	 * @return TurretEntity
	 *
	 * @see #effectSource
	 * @see Item
	 * @see Items
	 * @see Map
	 * @see MobEffect
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
	 * @param args An Object array that consists of {@code MobEffect,} {@code duration} (in seconds), and {@code amplifier} level
	 *
	 * @return TurretEntity
	 *
	 * @see #effectSource
	 * @see Item
	 * @see Items
	 * @see MobEffect
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

			if (arg[0] instanceof MobEffect) {
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
					DefensiveMeasures.LOGGER.warn("Effect source at {} was not {} due to given array not matching the correct order of items in the array, having [{}, {}, {}] instead of [MobEffect, Float, Integer]", this.getName().getString(), this.effectSourceHasEffect(item, (MobEffect) arg[0]) ? "updated" : "registered", arg[0].getClass().getName(), arg[1].getClass().getName(), arg[2].getClass().getName());
				else if (arg.length == 2)
					DefensiveMeasures.LOGGER.warn("Effect source at {} was not {} due to given array not matching the correct order of items in the array, having [{}, {}] instead of [MobEffect, Float] OR [MobEffect, Integer]", this.getName().getString(), this.effectSourceHasEffect(item, (MobEffect) arg[0]) ? "updated" : "registered", arg[0].getClass().getName(), arg[1].getClass().getName());
				else if (arg.length == 1)
					DefensiveMeasures.LOGGER.warn("Effect source at {} was not {} due to given array not matching the correct order of items in the array, having [{}] instead of [MobEffect]", this.getName().getString(), this.effectSourceHasEffect(item, (MobEffect) arg[0]) ? "updated" : "registered", arg[0].getClass().getName());
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
	 * 	<li>{@code MobEffect}</li>
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
	 * @see Attributes#FOLLOW_RANGE
	 */
	public float getMaxAttackRange() {
		return (float) Math.floor(
			this.getAttributes()
				.getValue(Attributes.FOLLOW_RANGE)
		) + this.getEyeHeight();
	}

	public float getMinAttackRange() {
		return 0.1f;
	}

	/**
	 * Retrieves the current barrel this turret will use to shoot.
	 *
	 * @param increment Whether to increment the barrel or not.
	 *
	 * @return {@code Vec3} The current barrel to use for shooting.
	 */
	public Vec3 getCurrentBarrel(boolean increment) {
		int currentBarrel = this.currentBarrel;

		if (increment) {
			this.currentBarrel = this.currentBarrel >= this.getTurretProjectileSpawn().size() - 1 ?
				0 : this.currentBarrel + 1;
		}

		return this.getTurretProjectileSpawn()
			.get(currentBarrel);
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	@Override
	public byte isFromItem() {
		return this.getEntityData().get(FROM_ITEM);
	}

	@Override
	public void setFromItem(byte fromItem) {
		this.getEntityData().set(FROM_ITEM, fromItem);
	}

	public void copyDataToStack(ItemStack stack) {
		Itemable.copyDataToStack(this, stack);
	}

	@Override
	public void copyDataFromNbt(CompoundTag nbt) {
		Itemable.copyDataFromNbt(this, nbt);
	}

	@Override
	public void performRangedAttack(@NonNull LivingEntity target, float pullProgress) {
		this.shoot(target);
	}

	public void shootAt(LivingEntity target) {
		this.shoot(target);
	}

	public void shootAt(TurretProjectileVelocity velocityData) {
		this.shoot(velocityData);
	}

	public AnimationState getSetupAnimationState() {
		return this.setupAnimationState;
	}

	public AnimationState getTeardownAnimationState() {
		return this.teardownAnimationState;
	}

	public AnimationState getIdleAnimationState() {
		return this.idleAnimationState;
	}

	public AnimationState getShootAnimationState() {
		return this.shootAnimationState;
	}

	public AnimationState getDeathAnimationState() {
		return this.deathAnimationState;
	}

	// ////////////////////////////// //
	// ABSTRACT & OVERRIDABLE METHODS //
	// ////////////////////////////// //

	// ABSTRACTS //

	/**
	 * Defines where the turret's projectile spawn is. This is, basically, the barrel of a cannon or
	 * machine gun, or the crossbow's bolt holder.
	 * <br><br>
	 * The returned value should be an instance of a {@link List} of {@link Vec3} so that it can
	 * also account for turrets that have multiple projectile spawns like for instance, the
	 * dual-barrel AA turret.
	 * <hr>
	 * For manipulating the actual <b>"shoot"</b> behavior, override the {@link #performRangedAttack(LivingEntity, float)}
	 * method of {@link TurretEntity} class.
	 *
	 * @return {@code List<Vec3>} The list of projectile spawn points.
	 */
	protected abstract List<Vec3> getTurretProjectileSpawn();

	/**
	 * Defines how long the turret's death animation is. This is defined by simply multiplying
	 * the death animation duration in seconds by twenty ({@code animDur * 20}).
	 *
	 * @return {@code int} The duration of the turret's death animation in ticks.
	 *
	 * @apiNote Must be manually updated whenever the death animation duration on the client side is updated
	 */
	protected abstract int getDeathAnimDuration();

	/**
	 * Defines the velocity of the projectile shot by this turret. This includes the speed, power,
	 * and uncertainty of the projectile.
	 *
	 * @param target The target of this entity.
	 *
	 * @return {@link TurretProjectileVelocity} The velocity data of the projectile shot by this turret.
	 */
	public abstract TurretProjectileVelocity getProjectileVelocityData(LivingEntity target);

	/**
	 * Determines the damage this turret's projectile will deal. This only
	 * works for instances of {@link com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity TurretProjectileEntity}
	 * or its subclasses due to the overridden methods and tailored behavior.
	 *
	 * @return {@code double} The damage this turret's projectile will deal.
	 */
	public abstract double getProjectileDamage();

	/**
	 * Determines the number of entities this turret's projectile can pierce. This only
	 * works for instances of {@link com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity TurretProjectileEntity}
	 * or its subclasses due to the overridden methods and tailored behavior.
	 *
	 * @return {@code byte} The projectile's pierce level.
	 */
	public abstract byte getProjectilePierceLevel();

	/**
	 * Retrieves the reload or attack cooldown of the turret in ticks. To properly get
	 * the second or millisecond value:
	 * <ul>
	 *     <li>{@code float seconds = val / 20}</li>
	 *     <li>{@code float milliseconds = seconds * 1000}</li>
	 * </ul>
	 * <hr>
	 * <b>NOTE:</b> When the value is to be used against animations, change {@code 20} to {@code 50}
	 * to match the renderer's tick time.</li>
	 *
	 * @return {@code int} The turret's reload time in ticks.
	 */
	public abstract int getTotalAttCooldown();

	// OVERRIDABLES //

	/**
	 * Defines how long the turret's setup animation is. This is defined by simply multiplying
	 * the setup animation duration in seconds by twenty ({@code animDur * 20}).
	 *
	 * @return {@code int} The duration of the turret's setup animation in ticks.
	 *
	 * @apiNote Must be manually updated whenever the setup animation duration on the client side is updated
	 * @implNote The default duration is 2.5 seconds (50 ticks) as it is also the duration of the fallback animation when none is defined for this turret.
	 */
	protected int getSetupAnimDuration() {
		return (int) (2.5F * 20);
	}

	/**
	 * Defines how long the turret's teardown animation is. This is defined by simply multiplying
	 * the teardown animation duration in seconds by twenty ({@code animDur * 20}).
	 *
	 * @return {@code int} The duration of the turret's teardown animation in ticks.
	 *
	 * @apiNote Must be manually updated whenever the teardown animation duration on the client side is updated
	 * @implNote The default duration is 2.5 seconds (50 ticks) as it is also the duration of the fallback animation when none is defined for this turret.
	 */
	protected int getTeardownAnimDuration() {
		return (int) (2.5F * 20);
	}

	protected double getTargetHeightScale() {
		return 1.0 / 3.0;
	}

	/**
	 * Sets the velocity of a projectile, along with the power and uncertainty of the projectile.
	 * <br><br>
	 * For more optional parameter control, see overloaded versions of this method.
	 * <br><br>
	 * For cases where there's no target, use {@link #setProjectileVelocity(Projectile, TurretProjectileVelocity)}.
	 *
	 * @param target The target of this entity. This will be used to calculate the velocity of the
	 *                  projectile towards the target's direction.
	 * @param projectile The projectile to set the velocity to.
	 */
	protected void setProjectileVelocity(LivingEntity target, Projectile projectile) {
		this.setProjectileVelocity(target, projectile, TurretProjectileVelocity.init(this));
	}

	/**
	 * Sets the velocity of a projectile, along with the power and uncertainty of the projectile.
	 *
	 * @param target The target of this entity. This will be used to calculate the velocity of the
	 * @param projectile The projectile to set the velocity to.
	 * @param velocityData The data that will be used to set the velocity of the projectile.
	 */
	protected void setProjectileVelocity(LivingEntity target, Projectile projectile, TurretProjectileVelocity velocityData) {
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
	protected void setProjectileVelocity(Projectile projectile, TurretProjectileVelocity velocityData) {
		Vec3 velocity = velocityData.getDeltaMovement();
		double vx = velocity.x();
		double vy = velocity.y();
		double vz = velocity.z();

		projectile.shoot(
			vx, vy, vz,
			velocityData.getPower(),
			velocityData.getUncertainty()
		);
	}

	/**
	 * Handles the basic animation of the turrets such as:
	 * <ul>
	 *     <li>{@code idleAnimationState}</li>
	 *     <li>{@code shootAnimationState}</li>
	 *     <li>{@code deathAnimationState}</li>
	 * </ul>
	 * Check the super method on how it was implemented if you wish
	 * to override the method and prevent some of it from running
	 * but also wish to keep others.
	 * <br><br>
	 * Applying particle effects to a turret must be done inside this
	 * method. Call the superclass's method prior to the particle logic
	 * (see {@link CannonTurretEntity Cannon Turret's class} for reference.
	 *
	 * @apiNote This method is only run on the client side, so any logic that is meant to be run on the server side should not be placed here.
	 */
	@Environment(EnvType.CLIENT)
	protected void updateAnimations() {
		this.idleAnimationState.startIfStopped(this.tickCount);

		this.setupAnimationState.animateWhen(this.isSettingUp(), this.tickCount);
		this.teardownAnimationState.animateWhen(this.isTearingDown(), this.tickCount);

		if (this.isDeadOrDying()) {
			this.deathAnimationState.startIfStopped(this.tickCount);
		}

		float elapsedTime = this.shootAnimationState.getTimeInMillis(this.tickCount);
		float reload = (this.getTotalAttCooldown() / 50f) * 1000;

		boolean isAnimPlaying = this.shootAnimationState.isStarted();
		boolean isAttacking = elapsedTime >= reload;
		boolean isShooting = this.getTrackedShooting();

		if (isShooting) {
			this.shootAnimationState.startIfStopped(this.tickCount);
		}

		if (!isShooting && isAnimPlaying && isAttacking && elapsedTime > 1000) {
			this.shootAnimationState.stop();
		}
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

	/**
	 * Shoots a burst of projectiles with a specified count and delay between each shot.
	 * <br><br>
	 * This method is used when a turret is set to shoot multiple projectiles in a single attack,
	 * allowing for a burst of projectiles to be fired in quick succession. The {@code count} parameter
	 * determines how many projectiles will be fired in the burst, while the {@code delay} parameter
	 * determines the delay between each shot.
	 *
	 * @param count The number of projectiles to shoot in the burst.
	 * @param delay The delay between each shot in ticks.
	 */
	protected void shootBurst(int count, int delay) {
		this.entityData.set(BURST_COUNT, count);
		this.entityData.set(BURST_DELAY, delay);
		this.setTrackedUseBurst(true);

		if (!this.level().isClientSide()) {
			this.burstDelayTimer = delay;
		}
	}

	/**
	 * Shoots a burst of projectiles with a specified count and delay between each shot.
	 * <br><br>
	 * This method is used when a turret is set to shoot multiple projectiles in a single attack,
	 * allowing for a burst of projectiles to be fired in quick succession. The {@code count} parameter
	 * determines how many projectiles will be fired in the burst, while the {@code delay} parameter
	 * determines the delay between each shot.
	 * <br><br>
	 * In this overloaded version, the target of the burst attack is also specified, allowing for
	 * the burst to target a specific entity.
	 *
	 * @param count The number of projectiles to shoot in the burst.
	 * @param delay The delay between each shot in ticks.
	 * @param target The target of the burst attack.
	 */
	protected void shootBurst(int count, int delay, LivingEntity target) {
		this.shootBurst(count, delay);
		this.setTarget(target);
	}

	/**
	 * Shoots a burst of projectiles with a specified count and delay between each shot.
	 * <br><br>
	 * This method is used when a turret is set to shoot multiple projectiles in a single attack,
	 * allowing for a burst of projectiles to be fired in quick succession. The {@code count} parameter
	 * determines how many projectiles will be fired in the burst, while the {@code delay} parameter
	 * determines the delay between each shot.
	 * <br><br>
	 * In this overloaded version, the velocity data is instead used instead of a target, allowing
	 * for the burst to shoot in said direction using the velocity data that will be applied to the
	 * projectile.
	 *
	 * @param count The number of projectiles to shoot in the burst
	 * @param delay The delay between each shot in ticks
	 * @param velocityData An instance of {@link TurretProjectileVelocity} that will be applued to the projectile
	 *
	 * @see TurretProjectileVelocity
	 */
	protected void shootBurst(int count, int delay, TurretProjectileVelocity velocityData) {
		this.shootBurst(count, delay);

		if (!this.level().isClientSide()) {
			this.velocityData = velocityData;
		}
	}

	protected void shoot(TurretProjectileVelocity velocityData) {
		try {
			this.shootAnimationState.startIfStopped(this.tickCount);

			Projectile projectile = (Projectile) this.projectile.create(
				this.level(),
				EntitySpawnReason.TRIGGERED
			);

			if (projectile == null) {
				DefensiveMeasures.LOGGER.warn("Projectile is null for {}", this.getName().getString());
				return;
			}

			Vec3 barrelPos = this.getCurrentBarrel(true);
			Vec3 pos = this.getRelativePos(barrelPos);

			if (projectile instanceof TurretProjectileEntity turretProjectile) {
				turretProjectile.setSpawnPos(pos);
			}
			else {
				projectile.setPos(pos);
				projectile.moveOrInterpolateTo(pos);
			}

			this.setProjectileVelocity(projectile, velocityData);

			// Set projectile direction
			Vec3 dir = velocityData.getDeltaMovement().normalize();
			float pitch = MathUtil.radToDeg((float) Mth.atan2(dir.y(), Math.sqrt(dir.x() * dir.x() + dir.z() * dir.z())));
			float yaw = MathUtil.radToDeg((float) Mth.atan2(dir.x(), dir.z()));

			pitch *= projectile instanceof ExplosiveProjectileEntity ? -1 : 1;
			yaw *= projectile instanceof ExplosiveProjectileEntity ? -1 : 1;
			yaw += projectile instanceof ExplosiveProjectileEntity ? 180 : 0;

			projectile.setYRot(yaw);
			projectile.setXRot(pitch);
			projectile.setYHeadRot(yaw);
			projectile.setYBodyRot(yaw);

			projectile.setOwner(this);

			// Calls the callback.
			this.onProjectileCreateCallback(projectile);

			// Plays shooting sound by default (via the this.playShootSound)
			if (this.playShootSound) {
				this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
			}

			this.level().addFreshEntity(projectile);
//			System.out.println("[" + projectile.getName().getString() + "] Gravity: " + projectile.getGravity() + " | Velocity: " + projectile.getDeltaMovement());
		} catch (IllegalArgumentException | SecurityException e) {
			DefensiveMeasures.printErr(e);
		}
	}

	protected final List<Holder<Attribute>> resistanceKnockbackResistanceAttributes() {
		return List.of(
			Attributes.EXPLOSION_KNOCKBACK_RESISTANCE,
			Attributes.KNOCKBACK_RESISTANCE
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
	 * 	<li>{@link Attributes#EXPLOSION_KNOCKBACK_RESISTANCE}</li>
	 * 	<li>{@link Attributes#KNOCKBACK_RESISTANCE}</li>
	 * </ul>
	 *
	 * @return {@code Map<RegistryEntry<EntityAttribute>, Double>} The map of resistance values.
	 */
	protected Map<Holder<Attribute>, Double> getKnockbackResistanceValues() {
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

	/**
	 * An overridable method that determines the idle pitch of the turret. By default,
	 * this returns an empty {@code Optional<Float>}, letting the turret look up and down on idle.
	 * This can be overridden to make the turret look up or down when idle.
	 * <br><br>
	 * Do note that when this method is overriden and returns a non-empty value, the turret will
	 * always look at the specified pitch when idle and has no targets.
	 *
	 * @return {@code Optional<Float>} The idle pitch of the turret. If empty, the turret will look up and down when idle.
	 */
	public Optional<Float> getIdlePitch() {
		return Optional.empty();
	}

	// ///////////// //
	// LOCAL CLASSES //
	// ///////////// //

	static class TurretBodyControl extends BodyRotationControl {
		public TurretBodyControl(Mob entity) {
			super(entity);
		}

		@Override
		public void clientTick() {
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
	 * The default values for the uncertainty and launch angle are set to {@code 2 * difficulty},
	 * and {@code 7.5f} respectively. These values can be changed via their respective setters
	 * after initializing an instance with the use of the {@link #init(TurretEntity)} method.
	 * <hr>
	 * <h1>Setting the Velocity</h1>
	 * There are several ways to set the velocity of the projectile since by default, the velocity
	 * is set to {@link Vec3#ZERO}:
	 * <ol>
	 *     <li>Setting the velocity directly using {@link #setVelocity(Vec3)} or {@link #setVelocity(double, double, double)}</li>
	 *     <li>Setting the velocity using the target's position using {@link #setVelocity(LivingEntity)}</li>
	 * </ol>
	 *
	 * <hr>
	 *
	 * <h1>Properties</h1>
	 * The properties defined in this class are:
	 * <ul>
	 * 	<li>{@link #turret} - The turret entity that will be shooting the projectile</li>
	 * 	<li>{@link #launchAngle} - The projectile's launch angle</li>
	 * 	<li>{@link #uncertainty} - The distortion of the projectile's trajectory, allowing for inaccuracies</li>
	 * 	<li>{@link #launchAngle} - The multiplier for the upward velocity of the projectile</li>
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
		private Vec3 lastTargetPos;
		/** The turret entity that will be shooting the projectile */
		private final TurretEntity turret;
		/** The distortion of the projectile's trajectory, allowing for inaccuracies */
		private float uncertainty;
		/** The projectile's launching angle */
		private float launchAngle;
		/**
		 * The projectile's speed if it has no gravity.
		 */
		private float speed;
		/** The velocity of the projectile */
		private Vec3 velocity;
		/** A flag that determines whether the projectile's trajectory is parabolic */
		private boolean isParabolic;
		/** Determines after calculation whether the projectile has gravity or not. */
		private boolean hasNoGravity;

		/**
		 * Initializes the velocity data of the turret's projectile. This can only be initialized
		 * using the {@link #init(TurretEntity)} method.
		 *
		 * @param turret The turret entity that will be shooting the projectile
		 */
		private TurretProjectileVelocity(TurretEntity turret) {
			this.turret = turret;
			this.uncertainty = turret.level().getDifficulty().getId() * 2;
			this.launchAngle = 7.5f;
			this.speed = 1f;
			this.velocity = Vec3.ZERO;
			this.hasNoGravity = false;
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
		 * Applies a speed to a projectile with 0 gravity or has the {@code isNoGravity()} to
		 * {@code true}. If the projectile has gravity more than 0 <b>AND</b> the
		 * {@code isNoGravity()} is {@code false}, then the speed, irregardless of value, will be
		 * ignored.
		 *
		 * @param speed The speed of the projectile if it has no gravity.
		 *
		 * @see #speed
		 */
		public TurretProjectileVelocity setSpeed(float speed) {
			this.speed = speed;
			this.recalculateVelocity();
			return this;
		}

		/**
		 * Retrieves the power will instead be calculated based on the magnitude
		 * of the velocity vector, allowing for more dynamic power values that
		 * are based on the velocity.
		 * <br><br>
		 * When the projectile has no gravity, it uses the provided {@link #speed}
		 * to match the differently used formula for its velocity.
		 *
		 * @return float The power of the projectile's velocity
		 */
		public float getPower() {
			if (this.isParabolic) {
				return (float) this.velocity.length();
			}

			float multiplier = this.hasNoGravity ?
				1f : 1.5f;

			return this.speed * multiplier;
		}

		/**
		 * Sets the uncertainty of the projectile's trajectory, allowing for inaccuracies.
		 *
		 * @param uncertainty The distortion of the projectile's trajectory
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #uncertainty
		 * @see #addUncertainty(float)
		 */
		public TurretProjectileVelocity setUncertainty(float uncertainty) {
			this.uncertainty = uncertainty;
			this.recalculateVelocity();
			return this;
		}

		/**
		 * Adds to the current uncertainty of the projectile's trajectory, allowing for increasing
		 * or decreasing the inaccuracy of the projectile from its current value.
		 *
		 * @param uncertainty The amount to add to the current distortion of the projectile's trajectory. This can be a negative value if you want to decrease the uncertainty.
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #uncertainty
		 * @see #setUncertainty(float)
		 */
		public TurretProjectileVelocity addUncertainty(float uncertainty) {
			this.uncertainty += uncertainty;
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
		 * Sets the launching angle of the projectile. The launching angle is the angle at which
		 * the projectile is launched from the turret, allowing for different trajectories such as
		 * shooting in a parabolic arc or shooting in a straight line.
		 *
		 * @param launchAngle The launching angle of the projectile in degrees. A positive value will make the projectile shoot in a parabolic arc, while a value of 0 will make the projectile shoot in a straight line.
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #launchAngle
		 */
		public TurretProjectileVelocity setLaunchAngle(float launchAngle) {
			this.launchAngle = launchAngle;
			this.recalculateVelocity();
			return this;
		}

		/**
		 * Retrieves the launching angle of the projectile.
		 * <br><br>
		 * The launching angle, by default, is 7.5°.
		 *
		 * @return float The launching angle of the projectile in degrees.
		 *
		 * @see #launchAngle
		 * @see #getLaunchAngleRad()
		 */
		public float getLaunchAngle() {
			return this.launchAngle;
		}

		/**
		 * Retrieves the launching angle of the projectile in radians. This is useful for velocity
		 * calculations that require the angle to be in radians.
		 *
		 * @return float The launching angle of the projectile in radians.
		 *
		 * @see #launchAngle
		 * @see #getLaunchAngle()
		 */
		public float getLaunchAngleRad() {
			return MathUtil.degToRad(this.launchAngle);
		}

		/**
		 * Sets the velocity of the projectile. When this method is used to set the velocity, the
		 * projectile's velocity won't respect the following properties:
		 * <ul>
		 * 	<li>{@link #launchAngle Upward Velocity Multiplier}</li>
		 * 	<li>{@link #isParabolic Parabolic Arc}</li>
		 * </ul>
		 *
		 * @param velocity The velocity of the projectile
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #velocity
		 */
		public TurretProjectileVelocity setVelocity(Vec3 velocity) {
			this.calculateVelocity(velocity, false);
			return this;
		}

		/**
		 * Sets the velocity of the projectile. When this method is used to set the velocity, the
		 * projectile's velocity won't respect the following properties:
		 * <ul>
		 * 	<li>{@link #launchAngle Launch Angle}</li>
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
			return this.setVelocity(new Vec3(x, y, z));
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
				target.getY(this.turret.getTargetHeightScale()),
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
		 * @see #setVelocityFromPos(double, double, double)
		 */
		public TurretProjectileVelocity setVelocityFromPos(Vec3 pos) {
			this.calculateVelocity(pos, true);
			return this;
		}

		/**
		 * Sets the velocity of the projectile based from the target position given by the x, y,
		 * and z coordinates.
		 *
		 * @param x The X coordinate of the target position.
		 * @param y The Y coordinate of the target position.
		 * @param z The Z coordinate of the target position.
		 *
		 * @return {@code TurretProjectileVelocity} The instance of the {@link TurretProjectileVelocity} class
		 *
		 * @see #velocity
		 * @see #setVelocityFromPos(Vec3)
		 */
		public TurretProjectileVelocity setVelocityFromPos(double x, double y, double z) {
			return this.setVelocityFromPos(new Vec3(x, y, z));
		}

		/**
		 * Retrieves the velocity of the projectile.
		 *
		 * @return {@code Vec3} The velocity of the projectile
		 *
		 * @see #velocity
		 */
		public Vec3 getDeltaMovement() {
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
			Vec3 targetRange = this.turret
				.getRelativePos(0, 0, this.turret.getMaxAttackRange() * range)
				.subtract(this.turret.getEyePosition());

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
		 * and continue using the provided velocity from {@link #setVelocity(Vec3)}.
		 */
		private void recalculateVelocity() {
			if (this.lastTargetPos != null)
				this.calculateVelocity(this.lastTargetPos, true);
		}

		/**
		 * Calculates the new velocity.
		 * <br><br>
		 * If the provided {@code v3d} provided is a position, it calculates it accordingly,
		 * depending on whether this instance is a {@link #isParabolic parabloic} trajectory or
		 * a straight trajectory. However, if the {@code v3d} is a velocity vector, then the
		 * provided value becomes the new velocity data, foregoing the entire calculation process.
		 * <br><br>
		 * The {@code v3d} value is identified whether it is a position or not is via the
		 * {@code isPos} parameter. A {@code true} value meant it is a position vector while
		 * {@code false} meant it is a velocity vector.
		 *
		 * @param v3d   The target position to calculate the velocity OR the new velocity to use.
		 * @param isPos Whether the provided {@code v3d} is a position or a velocity.
		 */
		private void calculateVelocity(Vec3 v3d, boolean isPos) {
			if (!isPos) {
				this.lastTargetPos = null;
				this.velocity = v3d;
				return;
			}

			Vec3 barrel = this.turret
				.getRelativePos(this.turret.getCurrentBarrel(false));

			this.lastTargetPos = v3d;

			this.calculateVelocity(
				v3d, barrel,
				this.isParabolic ? 45 : this.getLaunchAngle()
			);
		}

		/**
		 * Calculates the velocity of the projectile towards the target position using a binary
		 * search algorithm to find the optimal horizontal velocity that allows the projectile to
		 * reach the target, accounting for gravity and drag. The method simulates the projectile's
		 * trajectory over time to determine if it lands on the target, adjusting the velocity
		 * accordingly until it finds a suitable value.
		 *
		 * @param v3d    The target position to calculate the velocity towards.
		 * @param barrel The position of the turret's barrel, which is the starting point of the projectile.
		 * @param angle  The launch angle of the projectile in degrees.
		 */
		private void calculateVelocity(Vec3 v3d, Vec3 barrel, double angle) {
			Entity projectile = turret.projectile
				.create(this.turret.level(), EntitySpawnReason.TRIGGERED);
			double g = 0;
			double drag = 0.95;

			if (projectile != null) {
				g = projectile.getGravity();
				this.hasNoGravity = projectile.isNoGravity() || g == 0;

				if (projectile instanceof TurretProjectileEntity tpe) {
					drag = tpe.getFinalDrag();
				}

				projectile.discard();
				projectile.remove(RemovalReason.DISCARDED);
			}

			// For when a projectile has no gravity...
			if (this.hasNoGravity || !this.isParabolic) {
				double vx = (v3d.x() - barrel.x()) * 1.0625;
				double vy = (v3d.y() - barrel.y());
				double vz = (v3d.z() - barrel.z()) * 1.0625;

				double variance = Math.sqrt(vx * vx + vz * vz);
				vy += variance * ((this.hasNoGravity ? 0.1 : this.launchAngle) * 0.1);

				// Applies a scale factor to make sure that when the power
				// is applied, it will still be around its target's position.
				Vec3 rawV = new Vec3(vx, vy, vz);
				double magnitude = rawV.length();
				double power = this.speed * 1.5f;
				double scaleFactor = magnitude / power;
				this.velocity = rawV.scale(scaleFactor);

				return;
			}

			double rawVx = v3d.x() - barrel.x();
			double rawVz = v3d.z() - barrel.z();
			double dy = v3d.y() - barrel.y();

			double horizontalDist = Math.sqrt(rawVx * rawVx + rawVz * rawVz);
			double dirX = rawVx / horizontalDist;
			double dirZ = rawVz / horizontalDist;

			double theta = MathUtil.degToRad(angle);
			double tanTheta = Math.tan(theta);

			// Scale iterations and ticks based on distance
			// Close range = less iterations/ticks needed
			// Far range = more iterations/ticks needed
			int iterations = (int) Math.clamp(horizontalDist * 0.5, 16, 32);
			int maxTicks = (int) Math.clamp(horizontalDist * 3, 100, 600);

			// Binary search for vH that lands on target
			double lo = 0.01, hi = 50.0, vH = 1.0;

			for (int i = 0; i < iterations; i++) {
				vH = (lo + hi) / 2.0;

				double simX = 0, simY = 0, simVY = vH * tanTheta;
				double simVH = vH;

				for (int t = 0; t < maxTicks; t++) {
					simVY -= g;
					simVY *= drag;
					simVH *= drag;
					simY += simVY;
					simX += simVH;

					// For targets above: break when projectile has peaked and is now falling past dy.
					// For targets below (or same level): break when projectile is falling and has dropped to/past dy.
					boolean isPastPeak = simVY < 0;
					boolean hasReachedTargetY = (dy >= 0) ? (simY >= dy) : (simY <= dy);

					if (isPastPeak && hasReachedTargetY) {
						break;
					}
				}

				if (simX < horizontalDist - 0.1) {
					lo = vH;
				}
				else {
					hi = vH;
				}
			}

			double vyApplied = vH * tanTheta;

			this.velocity = new Vec3(
				dirX * vH,
				vyApplied,
				dirZ * vH
			);
		}
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		LEVEL = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.INT);
		FROM_ITEM = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.BYTE);
		SHOOTING_PITCH = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.FLOAT);
		USE_BURST = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.BOOLEAN);
		IS_LOCKED_BUT_NOT_ATTACKING = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.BOOLEAN);
		HAS_TARGET = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.BOOLEAN);

		ATTACHED_FACE = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.DIRECTION);
		X = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.FLOAT);
		Y = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.FLOAT);
		Z = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.FLOAT);

		BURST_COUNT = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.INT);
		BURST_PROJECTILE_FIRED = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.INT);
		BURST_DELAY = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.INT);
		SHOOTING = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.BOOLEAN);

		SETTING_UP = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.BOOLEAN);
		TEARING_DOWN = SynchedEntityData.defineId(TurretEntity.class, EntityDataSerializers.BOOLEAN);
	}
}
