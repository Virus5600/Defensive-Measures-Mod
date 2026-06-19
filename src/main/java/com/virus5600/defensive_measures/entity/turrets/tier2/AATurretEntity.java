package com.virus5600.defensive_measures.entity.turrets.tier2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.registry.tag.ModEntityTypeTags;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the Anti-Air (AA) Turret entity.
 * <br><br>
 * An Anti-Air Turret, or in short AA Turret, is a metal turret that shoots flak at flying enemies.
 * However, with the developer modeling the AA Turret using the Flak 88 as a reference, the
 * developer plans to also have the AA Turret able to target ground targets in the future.
 * <br><br>
 * The AA Turrets has very long ranges to accommodate the targeting of flying enemies who are mostly
 * flying 96 blocks or more away from the ground. It deals an AoE damage capable of downing flying
 * targets easily on direct hits while severely wounding them when the round explodes near the
 * targets. Despite the long range, however, the AA Turret has a slow rate of fire but has increased
 * accuracy compared to other turrets, making it a specialized turret for dealing airborne targets.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 100</li>
 *     <li><b>Base Damage:</b> 15.0</li>
 *     <li><b>Base Pierce Level:</b> 0</li>
 *     <li><b>Attack Cooldown:</b> 10 seconds</li>
 *     <li><b>Attack Range:</b> 8-96 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> 0 to +90°</li>
 *     <li><b>Armor:</b> 2</li>
 *     <li><b>Armor Toughness:</b> 1</li>
 * </ul>
 *
 * @see TurretEntity
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class AATurretEntity extends TurretEntity {
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 10 seconds <b>(20 ticks times 10 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20 * 10;
	private static final Vec3 HINGE_POS;
	private static final List<Vec3> BARRELS;
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

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public AATurretEntity(EntityType<? extends TurretEntity> entityType, Level world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.FLAK_PROJECTILE, ModItems.AA_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_ANTI_AIR_SHOOT);
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
		return this.attackGoal.isWithinRotationLimit(target) &&
			RegistryHelper.isOf(target.getType(), ModEntityTypeTags.FLYING_HOSTILES);
	}

	@Override
	protected void defineSynchedData(@NonNull Builder builder) {
		// Initialize standard data trackers
		super.defineSynchedData(builder);
	}

	public static @NotNull AttributeSupplier.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(100);
		TurretEntity.setTurretMaxRange(96 + ModEntities.AA_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes()
			.add(Attributes.ARMOR, 2)
			.add(Attributes.ARMOR_TOUGHNESS, 1);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void performRangedAttack(@NonNull LivingEntity target, float pullProgress) {
		TurretProjectileVelocity velocityData = this.getProjectileVelocityData(target);
		super.shootAt(velocityData);
	}

	@Override
	public void tick() {
		super.tick();
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource src) {
		return ModSoundEvents.TURRET_ANTI_AIR_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_ANTI_AIR_DESTROYED;
	}

	@Override
	public int getMinHeadXRot() {
		return 0;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.AA_TURRET);
	}

	@Override
	public SoundEvent getEntityRemoveSound() {
		return ModSoundEvents.TURRET_REMOVED_METAL;
	}

	/**
	 * Sets the minimum attack range of the AA Turret to 8 blocks.
	 *
	 * @return The minimum attack range of the AA Turret, which is 8 blocks.
	 */
	public float getMinAttackRange() {
		return 8f;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //

	/**
	 * {@inheritDoc}
	 * @see {@code AATurretAnimation#ANIM_AA_TURRET_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1.5F * 20);
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
			.setLaunchAngle(30f)
			.setSpeed(5f)
			.setVelocity(target);
	}

	public double getProjectileDamage() {
		return DAMAGE[this.getTrackedLevel() - 1];
	}

	public byte getProjectilePierceLevel() {
		return PIERCE_LEVELS[this.getTrackedLevel() - 1];
	}

	public int getTotalAttCooldown() {
		return TOTAL_ATT_COOLDOWN;
	}

	public Optional<Float> getIdlePitch() {
		return Optional.of(-30f);
	}

	// //////////// //
	// OVERRIDABLES //
	// //////////// //

	@Environment(EnvType.CLIENT) @Override
	protected void updateAnimations() {
		// Calls all the previous animation logics first before handling
		// particle logic
		super.updateAnimations();
	}

	@Override
	public boolean canAttack(final @NonNull LivingEntity target) {
		return RegistryHelper.isOf(target.getType(), ModEntityTypeTags.FLYING_HOSTILES) && super.canAttack(target);
	}

	protected int getSetupAnimDuration() {
		return (int) (5.0F * 20);
	}

	protected int getTeardownAnimDuration() {
		return (int) (5.0F * 20);
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

		HINGE_POS = new Vec3(0, 0.0625, -0.59375);

		BARRELS = List.of(
			new Vec3(0, 0, 2.5)
		);

		healables = Map.of(
			Items.IRON_NUGGET, 1f,
			Items.IRON_INGOT, 10f,
			Items.IRON_BLOCK, 100f
		);

		effectSource = Map.of(
			Items.IRON_BLOCK, List.<Object[]>of(
				new Object[] { MobEffects.RESISTANCE, 60, 2 }
			)
		);
	}
}
