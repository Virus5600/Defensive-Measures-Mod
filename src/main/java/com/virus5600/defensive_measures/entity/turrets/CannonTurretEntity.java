package com.virus5600.defensive_measures.entity.turrets;

import java.util.*;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.keyframe.event.ParticleKeyframeEvent;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

/**
 * Represents the Cannon Turret entity.
 * <br><br>
 * A Cannon Turret is a metal turret that shoots cannonballs at enemies. It has medium range and
 * deals a good amount of damage while dealing a good amount of damage. Albeit the longer range and
 * higher damage, the cannon turret has a longer cooldown compared to the other turrets, but with
 * its AoE damage, it can deal with multiple enemies at once.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 50</li>
 *     <li><b>Base Damage:</b> 10.0</li>
 *     <li><b>Base Pierce Level:</b> 0</li>
 *     <li><b>Attack Cooldown:</b> 5 seconds</li>
 *     <li><b>Attack Range:</b> 24 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> ±30°</li>
 *     <li><b>Armor:</b> 3</li>
 *     <li><b>Armor Toughness:</b> 2</li>
 * </ul>
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class CannonTurretEntity extends TurretEntity implements GeoEntity {
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 5 seconds <b>(20 ticks times 5 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20 * 5;
	private static final Map<String, RawAnimation> ANIMATIONS;
	private static final Map<Offsets, List<Vec3d>> OFFSETS;
	private static final double[] DAMAGE;
	private static final byte[] PIERCE_LEVELS;
	/**
	 * Contains all the items that can heal this entity.
	 */
	protected static final Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity
	 */
	protected static final Map<Item, List<Object[]>> effectSource;

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
	private boolean animPlayed = false;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public CannonTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.CANNONBALL, ModItems.CANNON_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_CANNON_SHOOT);
		this.setHealSound(ModSoundEvents.TURRET_REPAIR_METAL);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	protected void initGoals() {
		// Goal instances
		this.attackGoal = new ProjectileAttackGoal(this, 0, TOTAL_ATT_COOLDOWN, this.getMaxAttackRange(), this.getMinAttackRange());

		// Set the standard goals
		super.initGoals();
	}

	@Override
	protected void initDataTracker(Builder builder) {
		// Initialize standard data trackers
		super.initDataTracker(builder);
	}

	public static DefaultAttributeContainer.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(50);
		TurretEntity.setTurretMaxRange(24 + ModEntities.CANNON_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes()
			.add(EntityAttributes.ARMOR, 3)
			.add(EntityAttributes.ARMOR_TOUGHNESS, 2);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		float dist = (float) this.getPos().distanceTo(target.getPos());

		TurretProjectileVelocity velocityData = TurretProjectileVelocity.init(this)
			.setVelocity(target)
			.setUpwardVelocityMultiplier(dist * 0.1625f);

		super.shootAt(velocityData);
		this.stopTriggeredAnim("FiringSequence", "charge");
		this.triggerAnim("FiringSequence", "shoot");
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.getWorld().isClient) {
			int updateCountdownTicks = this.attackGoal.getUpdateCountdownTicks(),
				afterAttackTick = 5,
				beforeAttackTick = TOTAL_ATT_COOLDOWN - afterAttackTick;

			if (updateCountdownTicks > afterAttackTick && updateCountdownTicks < beforeAttackTick ) {
				this.triggerAnim("FiringSequence", "charge");
			}
		}
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override
	public int getMaxLookPitchChange() {
		return 30;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ModSoundEvents.TURRET_CANNON_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_CANNON_DESTROYED;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.CANNON_TURRET);
	}

	@Override
	public SoundEvent getEntityRemoveSound() {
		return ModSoundEvents.TURRET_REMOVED_METAL;
	}

	@Override
	public float getMinAttackRange() {
		return 3f;
	}

	// ///////////////////// //
	// ANIMATION CONTROLLERS //
	// ///////////////////// //

	private <E extends CannonTurretEntity>PlayState deathController(final AnimationState<E> event) {
		if (!this.isAlive() && !this.animPlayed) {
			this.animPlayed = true;
			event.setAnimation(ANIMATIONS.get("Death"));
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	private <E extends CannonTurretEntity>PlayState idleController(final AnimationState<E> event) {
		return event
			.setAndContinue(ANIMATIONS.get("Idle"));
	}

	private <E extends CannonTurretEntity>PlayState firingSequenceController(final AnimationState<E> event) {
		return PlayState.STOP;
	}

	private void firingSequenceKeyframeHandler(ParticleKeyframeEvent<CannonTurretEntity> state) {
		final String LOCATOR = state.getKeyframeData()
			.getLocator();

		if (LOCATOR.equals("barrel")) {
			Vec3d barrelPos = this.getRelativePos(this.getCurrentBarrel(false)),
				velocityModifier = this.getRelativePos(0, 0, 1.5).subtract(this.getEyePos());

			this.getWorld().addParticle(
				ModParticles.CANNON_FLASH,
				barrelPos.getX(), barrelPos.getY(), barrelPos.getZ(),
				velocityModifier.getX(), velocityModifier.getY(), velocityModifier.getZ()
			);
		}
		else if (LOCATOR.equals("fuse")) {
			Vec3d fusePos = this.getRelativePos(
				OFFSETS.get(Offsets.FUSE).getFirst()
			);

			this.getWorld().addParticle(
				ModParticles.CANNON_FUSE,
				fusePos.getX(), fusePos.getY(), fusePos.getZ(),
				0, 0.225, -0.50
			);
		}
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	// GeoEntity //
	@Override
	public void registerControllers(final ControllerRegistrar controllers) {
		controllers
			.add(
				new AnimationController<>(this, "Death", this::deathController),
				new AnimationController<>(this, "Idle", this::idleController),
				new AnimationController<>(this, "FiringSequence", this::firingSequenceController)
					.triggerableAnim("charge", ANIMATIONS.get("Charge"))
					.triggerableAnim("shoot", ANIMATIONS.get("Shoot"))
					.setParticleKeyframeHandler(this::firingSequenceKeyframeHandler)
			);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //
	protected List<Vec3d> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.BARREL);
	}

	public double getProjectileDamage() {
		return DAMAGE[this.getLevel() - 1];
	}

	public byte getProjectilePierceLevel() {
		return PIERCE_LEVELS[this.getLevel() - 1];
	}

	// /////////////////// //
	// LOCAL CLASSES/ENUMS //
	// /////////////////// //
	public enum Offsets {
		BARREL, FUSE
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		DAMAGE = new double[] {
			10.0,
			15.0,
			20.0
		};

		PIERCE_LEVELS = new byte[] {
			0,
			1,
			2
		};

		OFFSETS = Map.of(
			Offsets.BARREL, List.of(
				new Vec3d(0, 0, 0.875)
			),
			Offsets.FUSE, List.of(
				new Vec3d(0, 0.25, -0.55)
			)
		);

		ANIMATIONS = Map.of(
			"Death", RawAnimation.begin()
				.thenPlayAndHold("animation.cannon_turret.death"),
			"Idle", RawAnimation.begin()
				.thenLoop("animation.cannon_turret.setup"),
			"Charge", RawAnimation.begin()
				.thenPlay("animation.cannon_turret.fuse"),
			"Shoot", RawAnimation.begin()
				.thenPlay("animation.cannon_turret.shoot")
		);

		healables = Map.of(
			Items.IRON_NUGGET, 1f,
			Items.IRON_INGOT, 10f,
			Items.IRON_BLOCK, 100f
		);

		effectSource = Map.of(
			Items.IRON_BLOCK, List.<Object[]>of(
				new Object[] { StatusEffects.ABSORPTION, 60, 2 },
				new Object[] { StatusEffects.RESISTANCE, 60, 2 }
			)
		);
	}
}
