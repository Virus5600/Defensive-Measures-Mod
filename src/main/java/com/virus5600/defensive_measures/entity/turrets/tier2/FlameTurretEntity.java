package com.virus5600.defensive_measures.entity.turrets.tier2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.entity.projectiles.FlammableAerosolEntity;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.entity.turrets.interfaces.LoopableShootingSound;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.registry.tag.ModEntityTypeTags;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the Flame Turret entity.<br>
 * <br>
 * A Flame Turret is also a metallic turret that spews out fire at ground targets. The Flame Turret
 * is limited to only ground targets but might be able to also encompass all surface targets. While
 * limited to (possibly) surface targets only, the Flame Turret has a short range with an extremely
 * high damage and has the ability to create firestorms when multiple flames conjugates in an area.
 * This allows the Flame Turret to be a frontline and thus, has high armor and health points,
 * allowing it to tank and assume the frontline role for turrets.<br>
 * <br>
 * Flame Turrets, while having a limited target repository and range, and compensated by high
 * damage, are also great against hordes of enemies as its attack can damage multiple targets at
 * once in a large area, damaging everything its attack hits.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 150</li>
 *     <li><b>Base Damage:</b> 5.0 (10 per second)</li>
 *     <li><b>Base Pierce Level:</b> 0</li>
 *     <li><b>Attack Cooldown:</b> 1 second</li>
 *     <li><b>Attack Range:</b> 2 to 10 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> -30 to 22.5°</li>
 *     <li><b>Armor:</b> 5</li>
 *     <li><b>Armor Toughness:</b> 3</li>
 * </ul>
 * <br><br>
 * In addition to the above attributes, the flame it spews also has its own attributes:
 * <ul>
 *     <li><b>Initial Radius:</b> 0.5 blocks</li>
 *     <li><b>Max Radius:</b> 2 blocks</li>
 *     <li><b>Fire Ticks:</b> 2.5 seconds</li>
 *     <li><b>Flame Duration:</b> 1.5 seconds</li>
 *     <li><b>Damage Interval:</b> 0.5 seconds</li>
 *     <li><b>Ignore Armore:</b> Yes</li>
 * </ul>
 *
 * @see TurretEntity
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class FlameTurretEntity extends TurretEntity implements LoopableShootingSound {
	private static final EntityDataAccessor<Boolean> IS_PRIMED;
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 1 second <b>(20 ticks times 1 second)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20;
	private static final Vec3 HINGE_POS;
	/**
	 * Defines the position of the lighter or the fire that ignites the fuel. This position is a
	 * relative position from the turret's barrel position, which in turn is relative to the
	 * position of the turret's barrel hinge, which is also relative to the eye of the turret.<br>
	 * <br>
	 * This position is used when the turret is at rest and not shooting.
	 *
	 * @see #LIGHTER_ATT_POS
	 */
	private static final Vec3 LIGHTER_POS;
	/**
	 * Defines the position of the lighter or the fire that ignites the fuel. This position is a
	 * relative position from the turret's barrel position, which in turn is relative to the
	 * position of the turret's barrel hinge, which is also relative to the eye of the turret.<br>
	 * <br>
	 * This position is used when the turret has a target.
	 *
	 * @see #LIGHTER_POS
	 */
	private static final Vec3 LIGHTER_ATT_POS;
	private static final List<Vec3> BARRELS;
	private static final double[] DAMAGE;
	/**
	 * Contains all the items that can heal this entity.
	 */
	protected static final Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity
	 */
	protected static final Map<Item, List<Object[]>> effectSource;

	private float currentLighterPos = 0;
	private boolean wasShooting = false;


	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public FlameTurretEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.FLAMMABLE_AEROSOL, ModItems.FLAME_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_FLAME_SHOOT_LOOP);
		this.setHealSound(ModSoundEvents.TURRET_REPAIR_METAL);

		this.addHealables(healables)
			.addEffectSource(effectSource)
		;
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	public void registerGoals() {
		// Goal instances
		this.attackGoal = new ProjectileAttackGoal(
			this, 0,
			TOTAL_ATT_COOLDOWN, this.getMaxAttackRange(), this.getMinAttackRange()
		);

		// Set the standard goals
		super.registerGoals();
	}

	@Override
	protected NearestAttackableTargetGoal<?> getActiveTargetGoal() {
		return new NearestAttackableTargetGoal<>(
			this, Mob.class, 80,
			true, false,
			this::targetPredicate
		);
	}

	@Override
	protected boolean targetPredicate(LivingEntity target, ServerLevel world) {
		boolean isInFiringArc = this.attackGoal.isWithinRotationLimit(target);
		boolean isGroundTarget = !target.getType().is(ModEntityTypeTags.FLYING_HOSTILES);
		boolean isMonster = target instanceof Enemy;
		boolean isNotTurret = !target.getType().is(ModEntityTypeTags.TURRETS);

		return isInFiringArc && isGroundTarget && isMonster && isNotTurret;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
		// Initialize standard data trackers
		super.defineSynchedData(builder);

		builder
			.define(IS_PRIMED, false)
		;
	}

	public static @NotNull net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(150);
		TurretEntity.setTurretMaxRange(10 + ModEntities.FLAME_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes()
			.add(Attributes.ARMOR, 5)
			.add(Attributes.ARMOR_TOUGHNESS, 3);
	}

	// //////////// //
	// TRACKED DATA //
	// //////////// //

	/**
	 * Identifies whether the turret is primed and ready to shoot or not.
	 *
	 * @param isPrimed whether the turret is primed and ready to shoot or not
	 */
	public void setIsPrimed(boolean isPrimed) {
		this.entityData.set(IS_PRIMED, isPrimed);
	}

	public boolean isPrimed() {
		return this.entityData.get(IS_PRIMED);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	protected void lightLighter() {
		Vec3 barrelOrigin = this.getRelativePosFrom(
			this.getEyePosition(), HINGE_POS,
			false
		);

		Vec3 lighterPos = this.getRelativePosFrom(
			barrelOrigin, LIGHTER_POS,
			true
		);
		Vec3 lighterAttPos = this.getRelativePosFrom(
			barrelOrigin, LIGHTER_ATT_POS,
			true
		);

		Vec3 newPos = lighterPos.lerp(lighterAttPos, this.currentLighterPos / 20f);
		if (this.isPrimed()) {
			this.currentLighterPos = MathUtil.clamp(
				this.currentLighterPos + 1, 0, 20
			);
		} else {
			this.currentLighterPos = MathUtil.clamp(
				this.currentLighterPos - 1, 0, 20
			);
		}

		this.level()
			.addParticle(
				ModParticles.LIGHTER_FLAME,
				newPos.x(), newPos.y(), newPos.z(),
				0, 0, 0
			);
	}

	@Override
	protected <P extends Projectile> void onProjectileCreateCallback(P projectile) {
		FlammableAerosolEntity fae = (FlammableAerosolEntity) projectile;

		int fireDuration = (int) (2.5 * 20);
		float initRad = 0.5f;
		float targetRad = 2f;
		float radGrowth = ((targetRad - initRad) / 1.5f) / 20;

		fae.setTargetAge(0);
		fae.setDamage(15);
		fae.setRadius(initRad);
		fae.setTargetRadius(targetRad);
		fae.setFireDuration(fireDuration);
		fae.setRadiusGrowth(radGrowth);
		fae.setReApplicationDelay(10);

		if (!this.level().isClientSide() && !this.isPrimed()) {
			this.setIsPrimed(true);
		}
	}

	@Override
	protected void updateTrackedLockedButNotAttacking() {
		super.updateTrackedLockedButNotAttacking(0);
	}

	@Override
	public void performRangedAttack(@NonNull LivingEntity target, float pullProgress) {
		TurretProjectileVelocity velocityData = this.getProjectileVelocityData(target);

		super.shootAt(velocityData);
	}

	@Override
	public void tick() {
		super.tick();

		this.playShootSound(this);

		Level world = this.level();

		if (!world.isClientSide()) {
			this.setHasTarget(this.getTarget() != null);

			if (!this.hasTarget() && this.isPrimed()) {
				this.setIsPrimed(false);
			}
		}

		if (world.isClientSide()) {
			if (this.isPrimed()) {
				RandomSource rand = this.getRandom();
				Vec3 barrelPos = this.getRelativePos(this.getCurrentBarrel(false));
				Vec3 direction = Vec3.directionFromRotation(
					this.getXRot(),
					this.getYHeadRot()
				);
				Vec3 vel = direction.multiply(
					rand.nextIntBetweenInclusive(25, 50) / 100f,
					rand.nextIntBetweenInclusive(25, 50) / 100f,
					rand.nextIntBetweenInclusive(25, 50) / 100f
				);

				for (int i = 0; i < rand.nextIntBetweenInclusive(5, 10); i++) {
					ParticleOptions particle = rand.nextBoolean() ?
						ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME;

					world.addAlwaysVisibleParticle(
						particle,
						barrelPos.x(), barrelPos.y(), barrelPos.z(),
						vel.x(), vel.y(), vel.z()
					);
				}
			}
		}
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource src) {
		return ModSoundEvents.TURRET_FLAME_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_FLAME_DESTROYED;
	}

	@Override
	public int getMaxHeadXRot() {
		return 25;
	}

	@Override
	public int getMinHeadXRot() {
		return -35;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.FLAME_TURRET);
	}

	@Override
	public SoundEvent getEntityRemoveSound() {
		return ModSoundEvents.TURRET_REMOVED_METAL;
	}

	/**
	 * Sets the minimum attack range of the Flame Turret to 2 blocks.
	 *
	 * @return The minimum attack range of the Flame Turret, which is 2 blocks.
	 */
	@Override
	public float getMinAttackRange() {
		return 2f;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //

	/**
	 * {@inheritDoc}
	 * @see {@code FlameTurretAnimation#ANIM_FLAME_TURRET_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1.25F * 20);
	}

	protected List<Vec3> getTurretProjectileSpawn() {
		List<Vec3> barrels = new ArrayList<>();

		for (Vec3 barrel : BARRELS) {
			Vec3 barrelOrigin = this.getRelativePosFrom(
				this.getEyePosition(), HINGE_POS,
				false
			);

			Vec3 barrelPos = this.getRelativePosFrom(
				barrelOrigin, barrel,
				true
			);

			float pitchRad = MathUtil.degToRad(this.getXRot());
			float yawRad = MathUtil.degToRad(this.getYHeadRot());

			barrels.add(
				barrelPos.subtract(this.getEyePosition())
					.yRot(yawRad)
					.xRot(pitchRad)
			);
		}

		return barrels;
	}

	public TurretProjectileVelocity getProjectileVelocityData(LivingEntity target) {
		return TurretProjectileVelocity
			.init(this)
			.setLaunchAngle(0.05f)
			.setVelocity(target);
	}

	public double getProjectileDamage() {
		return DAMAGE[this.getTrackedLevel() - 1];
	}

	public byte getProjectilePierceLevel() {
		return 0;
	}

	public int getTotalAttCooldown() {
		return TOTAL_ATT_COOLDOWN;
	}

	// //////////// //
	// OVERRIDABLES //
	// //////////// //

	@Override
	protected double getTargetHeightScale() {
		return (double) 1 / 10;
	}

	@Environment(EnvType.CLIENT) @Override
	protected void updateAnimations() {
		super.updateAnimations();

		this.lightLighter();
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override
	public void setShootingLastTick(boolean isShooting) {
		this.wasShooting = isShooting;
	}

	@Override
	public boolean wasShootingLastTick() {
		return this.wasShooting;
	}

	@Override
	public SoundEvent getShootStartSound() {
		return ModSoundEvents.TURRET_FLAME_SHOOT_START;
	}

	@Override
	public SoundEvent getShootLoopSound() {
		return ModSoundEvents.TURRET_FLAME_SHOOT_LOOP;
	}

	@Override
	public SoundEvent getShootEndSound() {
		return ModSoundEvents.TURRET_FLAME_SHOOT_END;
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		IS_PRIMED = SynchedEntityData.defineId(FlameTurretEntity.class, EntityDataSerializers.BOOLEAN);

		DAMAGE = new double[] {
			15.00,
			16.25,
			17.50
		};

		HINGE_POS = new Vec3(0, 0, 0.575);

		LIGHTER_POS = new Vec3(0, -0.125, 1.3125);
		LIGHTER_ATT_POS = new Vec3(0, 0, 1.275);

		BARRELS = List.of(
			new Vec3(0, 0, 1.3125)
		);

		healables = Map.of(
			Items.COPPER_NUGGET, 1f,
			Items.IRON_NUGGET, 1f,
			Items.COPPER_INGOT, 5f,
			Items.IRON_INGOT, 10f,
			Items.COPPER_BLOCK, 50f,
			Items.IRON_BLOCK, 100f
		);

		effectSource = Map.of(
			Items.IRON_BLOCK, List.<Object[]>of(
				new Object[] { MobEffects.RESISTANCE, 60, 2 }
			),
			Items.COPPER_BLOCK, List.<Object[]>of(
				new Object[] { MobEffects.RESISTANCE, 60, 1 }
			)
		);
	}
}
