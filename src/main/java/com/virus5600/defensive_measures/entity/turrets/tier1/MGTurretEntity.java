package com.virus5600.defensive_measures.entity.turrets.tier1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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
 *     <li><b>Attack Cooldown:</b> 0.15 seconds per bullets / 3.75 seconds per burst</li>
 *     <li><b>Attack Range:</b> 20 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> -27.5 to 90°</li>
 *     <li><b>Armor:</b> 2</li>
 *     <li><b>Armor Toughness:</b> 1</li>
 * </ul>
 *
 * @see TurretEntity
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MGTurretEntity extends TurretEntity {
	/**
	 * Defines how many seconds the machine gun should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 3.75 seconds <b>(20 ticks times 3.75 seconds)</b>.
	 * <br><br>
	 * Though, this cooldown is for its burst attack. The machine gun will, however, shoot 5 bullets
	 * per burst with a 0.15 seconds cooldown between each bullet. This part is not included in the
	 * cooldown attribute and will be handled by the {@link #tick() tick()} method.
	 */
	private static final int TOTAL_ATT_COOLDOWN = (int) (20 * 3.75);
	private static final Map<Offsets, List<Vec3>> OFFSETS;
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
	public MGTurretEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.MG_BULLET, ModItems.MG_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_MG_SHOOT);
		this.setHealSound(ModSoundEvents.TURRET_REPAIR_METAL);

		this.addHealables(healables)
			.addEffectSource(effectSource)
		;
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	protected void registerGoals() {
		// Goal instances
		this.attackGoal = new ProjectileAttackGoal(this, 0, TOTAL_ATT_COOLDOWN, this.getMaxAttackRange(), this.getMinAttackRange());

		// Set the standard goals
		super.registerGoals();
	}

	@Override
	protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.@NonNull Builder builder) {
		// Initialize standard data trackers
		super.defineSynchedData(builder);
	}

	public static Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(25);
		TurretEntity.setTurretMaxRange(20 + ModEntities.MG_TURRET.getDimensions().eyeHeight());

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

		super.shootBurst(5, 5, velocityData);
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource source) {
		return ModSoundEvents.TURRET_MG_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_MG_DESTROYED;
	}

	@Override
	public int getMinHeadXRot() {
		return -28;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.MG_TURRET);
	}

	@Override
	public SoundEvent getEntityRemoveSound() {
		return ModSoundEvents.TURRET_REMOVED_METAL;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //

	/**
	 * {@inheritDoc}
	 * @see {@code MGTurretAnimation#ANIM_MG_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1.5F * 20);
	}

	protected List<Vec3> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.BARREL);
	}

	public TurretProjectileVelocity getProjectileVelocityData(LivingEntity target) {
		float dist = (float) this.position()
			.distanceTo(target.position());

		return TurretProjectileVelocity
			.init(this)
			.setLaunchAngle(dist * 0.125f)
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

	// //////////// //
	// OVERRIDABLES //
	// //////////// //

	@Environment(EnvType.CLIENT)
	@Override
	protected void updateAnimations() {
		// Calls all the previous animation logics first before handling
		// particle logic
		super.updateAnimations();

		// Set variables that will be used for logic
		boolean isShooting = this.getTrackedShooting();
		boolean stillShooting = this.getTrackedUseBurst();

		// Handles the Flash particle for when the MG shoots
		if (isShooting || stillShooting) {
			Vec3 barrelPos = this.getRelativePos(
				this.getCurrentBarrel(false)
					.add(0, 0, 0.05)
			);

			this.level()
				.addParticle(
					ModParticles.SUSPENDED_SPARKS,
					barrelPos.x(), barrelPos.y(), barrelPos.z(),
					0, 0, 0
				);
		}
	}

	@Override
	protected int getSetupAnimDuration() {
		return (int) (3.75F * 20);
	}

	@Override
	protected int getTeardownAnimDuration() {
		return (int) (3.75F * 20);
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
				new Vec3(0.0, 0.0, 0.5)
			)
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
