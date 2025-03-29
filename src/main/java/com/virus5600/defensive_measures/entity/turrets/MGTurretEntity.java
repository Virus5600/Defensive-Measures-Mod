package com.virus5600.defensive_measures.entity.turrets;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.keyframe.event.ParticleKeyframeEvent;
import software.bernie.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import java.util.List;
import java.util.Map;

/**
 * Represents the Machine Gun Turret entity.
 * <br><br>
 * A Machine Gun Turret, or in short MG Turret, is a metal turret that shoots bullets at enemies.
 * It has a long range and deals a good amount of damage while providing a superb amount of fire
 * rate, shooting 5 bullets per burst, with a 0.15 seconds cooldown between each bullet in a burst
 * and a 3.75 seconds cooldown between each burst.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 25</li>
 *     <li><b>Base Damage:</b> 5.0</li>
 *     <li><b>Base Pierce Level:</b> 5</li>
 *     <li><b>Attack Cooldown:</b> 0.1 seconds per bullets / 3.75 seconds per burst</li>
 *     <li><b>Attack Range:</b> 20 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> ±27.5°</li>
 * </ul>
 *
 * @see TurretEntity
 * @see GeoEntity
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class MGTurretEntity extends TurretEntity implements GeoEntity {
	/**
	 * Defines how many seconds the machine gun should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 3.75 seconds <b>(20 ticks times 3.75 seconds)</b>.
	 * <br><br>
	 * Though, this cooldown is for its burst attack. The machine gun will, however, shoot 5 bullets
	 * per burst with a 0.15 seconds cooldown between each bullet. This part is not included in the
	 * cooldown attribute and will be handled by the {@link #tick() tick()} method.
	 */
	private static final int TOTAL_ATT_COOLDOWN = (int) (20 * 3.75);
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
	public MGTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.MG_BULLET, ModItems.MG_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_MG_SHOOT);
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
	protected void initDataTracker(DataTracker.Builder builder) {
		// Initialize standard data trackers
	super.initDataTracker(builder);
	}

	public static Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(25);
		TurretEntity.setTurretMaxRange(20 + ModEntities.MG_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes()
			.add(EntityAttributes.ARMOR, 2)
			.add(EntityAttributes.ARMOR_TOUGHNESS, 1);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		this.triggerAnim("FiringSequence", "shoot");

		float dist = (float) this.getPos().distanceTo(target.getPos());

		TurretProjectileVelocity velocityData = TurretProjectileVelocity.init(this)
			.setVelocity(target)
			.setUpwardVelocityMultiplier(dist * 0.125f);

		super.shootBurst(5, 3, velocityData);
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override
	public int getMaxLookPitchChange() {
		return 27;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ModSoundEvents.TURRET_MG_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_MG_DESTROYED;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.MG_TURRET);
	}

	@Override
	public SoundEvent getEntityRemoveSound() {
		return ModSoundEvents.TURRET_REMOVED_METAL;
	}

	// ///////////////////// //
	// ANIMATION CONTROLLERS //
	// ///////////////////// //

	private <E extends MGTurretEntity> PlayState deathController(final AnimationState<E> event) {
		if (!this.isAlive() && !this.animPlayed) {
			this.animPlayed = true;
			event.setAnimation(ANIMATIONS.get("Death"));
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	private <E extends MGTurretEntity>PlayState idleController(final AnimationState<E> event) {
		return event
			.setAndContinue(ANIMATIONS.get("Idle"));
	}

	private <E extends MGTurretEntity>PlayState shootController(final AnimationState<E> event) {
		return PlayState.STOP;
	}

	private void shootKeyframeHandler(ParticleKeyframeEvent<MGTurretEntity> state) {
		final String LOCATOR = state.getKeyframeData()
			.getLocator();

		if (LOCATOR.equals("barrel")) {
			Vec3d barrelPos = this.getRelativePos(this.getCurrentBarrel(false)),
				velocityModifier = this.getRelativePos(0, 0, 0).subtract(this.getEyePos());

			this.getWorld().addParticle(
				ModParticles.SUSPENDED_SPARKS,
				barrelPos.getX(), barrelPos.getY(), barrelPos.getZ(),
				velocityModifier.getX(), velocityModifier.getY(), velocityModifier.getZ()
			);
		}
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	// GeoEntity //
	@Override
	public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
		controllers
			.add(
				new AnimationController<>(this, "Death", this::deathController),
				new AnimationController<>(this, "Idle", this::idleController),
				new AnimationController<>(this, "FiringSequence", this::shootController)
					.triggerableAnim("shoot", ANIMATIONS.get("Shoot"))
					.setParticleKeyframeHandler(this::shootKeyframeHandler)
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
		BARREL
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		DAMAGE = new double[] {
			5.0,
			6.25,
			7.5
		};

		PIERCE_LEVELS = new byte[] {
			5,
			5,
			6
		};

		OFFSETS = Map.of(
			Offsets.BARREL, List.of(
				new Vec3d(0.0, 0.0, 0.5)
			)
		);

		ANIMATIONS = Map.of(
			"Death", RawAnimation.begin()
				.thenPlayAndHold("animation.machine_gun_turret.death"),
			"Idle", RawAnimation.begin()
				.thenLoop("animation.machine_gun_turret.setup"),
			"Shoot", RawAnimation.begin()
				.thenPlay("animation.machine_gun_turret.shoot")
		);

		healables = Map.of(
			Items.IRON_NUGGET, 1f,
			Items.IRON_INGOT, 10f,
			Items.IRON_BLOCK, 100f
		);

		effectSource = Map.of(
			Items.IRON_BLOCK, List.<Object[]>of(
				new Object[] { StatusEffects.RESISTANCE, 60, 2 }
			)
		);
	}
}
