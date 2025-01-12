package com.virus5600.defensive_measures.entity.turrets;

import java.util.*;

import com.virus5600.defensive_measures.entity.ModEntities;
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

import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

public class CannonTurretEntity extends TurretEntity implements GeoEntity {
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 5 seconds <b>(20 ticks times 5 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20 * 5;
	private static final Map<String, RawAnimation> ANIMATIONS;
	private static final Map<Offsets, List<Vec3d>> OFFSETS;

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
	private static final double[] DAMAGE;

	//////////////////
	// CONSTRUCTORS //
	//////////////////
	public CannonTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.CANNONBALL, ModItems.CANNON_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_CANNON_SHOOT);
		this.setHealSound(ModSoundEvents.TURRET_REPAIR_METAL);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
	}

	//////////////////
	// INITIALIZERS //
	//////////////////
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

		return TurretEntity.setAttributes()
			.add(EntityAttributes.FOLLOW_RANGE, 24)
			.add(EntityAttributes.ARMOR, 3)
			.add(EntityAttributes.ARMOR_TOUGHNESS, 2);
	}

	/////////////////////
	// PROCESS METHODS //
	/////////////////////

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		super.shootAt(target, pullProgress);
		this.stopTriggeredAnim("FiringSequence", "charge");
		this.triggerAnim("FiringSequence", "shoot");
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.getWorld().isClient) {
			int updateCountdownTicks = this.attackGoal.getUpdateCountdownTicks(),
				afterAttackTick = 5,
				beforeAttackTick = CannonTurretEntity.TOTAL_ATT_COOLDOWN - afterAttackTick;

			if (updateCountdownTicks > afterAttackTick && updateCountdownTicks < beforeAttackTick ) {
				this.triggerAnim("FiringSequence", "charge");
			}
		}

	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////

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

	///////////////////////////
	// ANIMATION CONTROLLERS //
	///////////////////////////

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
			Vec3d barrelPos = this.getRelativePos(this.getTurretProjectileSpawn().getFirst()),
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

	///////////////////////////////
	// INTERFACE IMPLEMENTATIONS //
	///////////////////////////////

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
		return CannonTurretEntity.DAMAGE[this.getLevel() - 1];
	}

	public byte getProjectilePierceLevel() {
		return 0;
	}

	/////////////////////////
	// LOCAL CLASSES/ENUMS //
	/////////////////////////
	public enum Offsets {
		BARREL, FUSE
	}

	///////////////////////
	// STATIC INITIALIZE //
	///////////////////////

	static {
		DAMAGE = new double[] {
			10.0
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

		healables = new HashMap<>() {
			{
				for (Item item : TurretEntity.PLANKS)
					put(item, 1.0f);
				put(Items.IRON_NUGGET, 1f);
				put(Items.IRON_INGOT, 10f);
				put(Items.IRON_BLOCK, 100f);
			}
		};

		effectSource = new HashMap<>() {
			{
				put(Items.IRON_BLOCK, new ArrayList<>() {
					{
						add(new Object[] {StatusEffects.ABSORPTION, 60, 2});
					}
				});
			}
		};
	}
}
