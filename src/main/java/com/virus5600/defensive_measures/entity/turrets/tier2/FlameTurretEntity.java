package com.virus5600.defensive_measures.entity.turrets.tier2;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.entity.projectiles.FlammableAerosolEntity;
import com.virus5600.defensive_measures.entity.turrets.interfaces.LoopableShootingSound;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.registry.tag.ModEntityTypeTags;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	private static final TrackedData<Boolean> IS_PRIMED;
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 1 second <b>(20 ticks times 1 second)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20;
	private static final Vec3d HINGE_POS;
	/**
	 * Defines the position of the lighter or the fire that ignites the fuel. This position is a
	 * relative position from the turret's barrel position, which in turn is relative to the
	 * position of the turret's barrel hinge, which is also relative to the eye of the turret.<br>
	 * <br>
	 * This position is used when the turret is at rest and not shooting.
	 *
	 * @see #LIGHTER_ATT_POS
	 */
	private static final Vec3d LIGHTER_POS;
	/**
	 * Defines the position of the lighter or the fire that ignites the fuel. This position is a
	 * relative position from the turret's barrel position, which in turn is relative to the
	 * position of the turret's barrel hinge, which is also relative to the eye of the turret.<br>
	 * <br>
	 * This position is used when the turret has a target.
	 *
	 * @see #LIGHTER_POS
	 */
	private static final Vec3d LIGHTER_ATT_POS;
	private static final List<Vec3d> BARRELS;
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
	public FlameTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
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
	public void initGoals() {
		// Goal instances
		this.attackGoal = new ProjectileAttackGoal(
			this, 0,
			TOTAL_ATT_COOLDOWN, this.getMaxAttackRange(), this.getMinAttackRange()
		);

		// Set the standard goals
		super.initGoals();
	}

	@Override
	protected ActiveTargetGoal<?> getActiveTargetGoal() {
		return new ActiveTargetGoal<>(
			this, MobEntity.class, 80,
			true, false,
			this::targetPredicate
		);
	}

	@Override
	protected boolean targetPredicate(LivingEntity target, ServerWorld world) {
		boolean isInFiringArc = this.attackGoal.isWithinRotationLimit(target);
		boolean isGroundTarget = !target.getType().isIn(ModEntityTypeTags.FLYING_HOSTILES);
		boolean isNotTurret = !target.getType().isIn(ModEntityTypeTags.TURRETS);

		return isInFiringArc && isGroundTarget && isNotTurret;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		// Initialize standard data trackers
		super.initDataTracker(builder);

		builder
			.add(IS_PRIMED, false)
		;
	}

	public static @NotNull DefaultAttributeContainer.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(150);
		TurretEntity.setTurretMaxRange(10 + ModEntities.FLAME_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes()
			.add(EntityAttributes.ARMOR, 5)
			.add(EntityAttributes.ARMOR_TOUGHNESS, 3);
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
		this.dataTracker.set(IS_PRIMED, isPrimed);
	}

	public boolean isPrimed() {
		return this.dataTracker.get(IS_PRIMED);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	protected void lightLighter() {
		Vec3d barrelOrigin = this.getRelativePosFrom(
			this.getEyePos(), HINGE_POS,
			false
		);

		Vec3d lighterPos = this.getRelativePosFrom(
			barrelOrigin, LIGHTER_POS,
			true
		);
		Vec3d lighterAttPos = this.getRelativePosFrom(
			barrelOrigin, LIGHTER_ATT_POS,
			true
		);

		Vec3d newPos = lighterPos.lerp(lighterAttPos, this.currentLighterPos / 20f);
		if (this.isPrimed()) {
			this.currentLighterPos = MathUtil.clamp(
				this.currentLighterPos + 1, 0, 20
			);
		} else {
			this.currentLighterPos = MathUtil.clamp(
				this.currentLighterPos - 1, 0, 20
			);
		}

		this.getEntityWorld()
			.addParticleClient(
				ModParticles.LIGHTER_FLAME,
				newPos.getX(), newPos.getY(), newPos.getZ(),
				0, 0, 0
			);
	}

	@Override
	protected <P extends ProjectileEntity> void onProjectileCreateCallback(P projectile) {
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

		if (!this.getEntityWorld().isClient() && !this.isPrimed()) {
			this.setIsPrimed(true);
		}
	}

	@Override
	protected void updateTrackedLockedButNotAttacking() {
		super.updateTrackedLockedButNotAttacking(0);
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		TurretProjectileVelocity velocityData = TurretProjectileVelocity.init(this)
			.setPower(1f)
			.setUpwardVelocityMultiplier(0.05f)
			.setVelocity(target);

		super.shootAt(velocityData);
	}

	@Override
	public void tick() {
		super.tick();

		this.playShootSound(this);

		World world = this.getEntityWorld();

		if (!world.isClient()) {
			this.setHasTarget(this.getTarget() != null);

			if (!this.hasTarget() && this.isPrimed()) {
				this.setIsPrimed(false);
			}
		}

		if (world.isClient()) {
			if (this.isPrimed()) {
				Random rand = this.getRandom();
				Vec3d barrelPos = this.getRelativePos(this.getCurrentBarrel(false));
				Vec3d direction = Vec3d.fromPolar(
					this.getPitch(),
					this.getHeadYaw()
				);
				Vec3d vel = direction.multiply(
					rand.nextBetween(25, 50) / 100f,
					rand.nextBetween(25, 50) / 100f,
					rand.nextBetween(25, 50) / 100f
				);

				for (int i = 0; i < rand.nextBetween(5, 10); i++) {
					ParticleEffect particle = rand.nextBoolean() ?
						ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME;

					world.addImportantParticleClient(
						particle,
						barrelPos.getX(), barrelPos.getY(), barrelPos.getZ(),
						vel.getX(), vel.getY(), vel.getZ()
					);
				}
			}
		}
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override @Nullable
	protected SoundEvent getHurtSound(DamageSource src) {
		return ModSoundEvents.TURRET_FLAME_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_FLAME_DESTROYED;
	}

	@Override
	public int getMaxLookPitchChange() {
		return 25;
	}

	@Override
	public int getMinLookPitchChange() {
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

	protected List<Vec3d> getTurretProjectileSpawn() {
		List<Vec3d> barrels = new ArrayList<>();

		for (Vec3d barrel : BARRELS) {
			Vec3d barrelOrigin = this.getRelativePosFrom(
				this.getEyePos(), HINGE_POS,
				false
			);

			Vec3d barrelPos = this.getRelativePosFrom(
				barrelOrigin, barrel,
				true
			);

			float pitchRad = MathUtil.degToRad(this.getPitch());
			float yawRad = MathUtil.degToRad(this.getHeadYaw());

			barrels.add(
				barrelPos.subtract(this.getEyePos())
					.rotateY(yawRad)
					.rotateX(pitchRad)
			);
		}

		return barrels;
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
		IS_PRIMED = DataTracker.registerData(FlameTurretEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

		DAMAGE = new double[] {
			15.00,
			16.25,
			17.50
		};

		HINGE_POS = new Vec3d(0, 0, 0.575);

		LIGHTER_POS = new Vec3d(0, -0.125, 1.3125);
		LIGHTER_ATT_POS = new Vec3d(0, 0, 1.275);

		BARRELS = List.of(
			new Vec3d(0, 0, 1.3125)
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
