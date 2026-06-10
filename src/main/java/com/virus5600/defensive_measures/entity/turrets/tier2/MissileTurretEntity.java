package com.virus5600.defensive_measures.entity.turrets.tier2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import com.virus5600.defensive_measures.entity.projectiles.MicroMissileEntity;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.entity.turrets.interfaces.UsesMissile;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Represents the Missile Turret entity.
 * <br><br>
 * The Missile Turret is a modern armament that can fit a lot of roles: Anti-Air, Anti-Ground, Area
 * Denial, Point-Defence, etc. This is mostly thanks to its large range and smart munition.
 * Furthermore, it comes in various sizes, allowing it to fit in a lot of machine, from small crafts
 * such as attack helicopters to large naval vessels like an aircraft carrier.
 * <br><br>
 * In this implementation, the Missile Turret has a range of 64 block radius; a chunk short of {@link
 * AATurretEntity AA Turret}'s range, allowing it to target problematic entities such as Ghasts and
 * Phantoms in a limited capacity with its rockets (in level 1) or missiles (level 2+). It deals a
 * small AoE damage, dealing a small splash damage to entities near where it hits which could be the
 * target or in an area when it misses or got blocked while pursuing its target.
 * <br><br>
 * The Missile Turret fires a burst of three (3) {@link MicroMissileEntity Micro Missiles} (at
 * level 1; 6 at level 2; 12 at level 3) in quick succession before having to reload for 5 seconds.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 75</li>
 *     <li><b>Base Damage:</b> 7.5</li>
 *     <li><b>Base Pierce Level:</b> 1</li>
 *     <li><b>Attack Cooldown:</b> 0.33 seconds per missile / 5 seconds per burst</li>
 *     <li><b>Attack Range:</b> 64 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> ±12°</li>
 *     <li><b>Armor:</b> 2</li>
 *     <li><b>Armor Toughness:</b> 1</li>
 * </ul>
 *
 * @see TurretEntity
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MissileTurretEntity extends TurretEntity implements UsesMissile {
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 5 second <b>(20 ticks times 5 second)</b>.
	 * <br><br>
	 * Though, this cooldown is for its burst attack. The missile turret will, however, shoot 3
	 * micro missiles per burst with a 0.33 seconds cooldown between each missile. This part is not
	 * included in the cooldown attribute and will be handled by the {@link #tick() tick()} method.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20 * 5;
	private static final Map<Offsets, List<Vec3>> OFFSETS;
	private static final double[] DAMAGE;
	private static final byte[] PIERCE_LEVELS;

	private static final double[] MISSILE_SPEED;
	private static final double[] MISSILE_TURN_RATE;

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
	public MissileTurretEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.MICRO_MISSILE, ModItems.MISSILE_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_MISSILE_SHOOT);
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
	protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
		// Initialize standard data trackers
		super.defineSynchedData(builder);
	}

	public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(75);
		TurretEntity.setTurretMaxRange(64 + ModEntities.MISSILE_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes()
			.add(Attributes.ARMOR, 2)
			.add(Attributes.ARMOR_TOUGHNESS, 1);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //
	@Override
	protected <P extends Projectile> void onProjectileCreateCallback(P projectile) {
		MicroMissileEntity missile = (MicroMissileEntity) projectile;

		missile.setRemainingFuel(this.getMissileFuel());
	}

	@Override
	public void performRangedAttack(@NonNull LivingEntity target, float pullProgress) {
		TurretProjectileVelocity velocityData = this.getProjectileVelocityData(target);

		super.shootBurst(3, (20 / 3), velocityData);
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource source) {
		return ModSoundEvents.TURRET_MISSILE_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_MISSILE_DESTROYED;
	}

	@Override
	public int getMinHeadXRot() {
		return -12;
	}

	@Override
	public int getMaxHeadXRot() {
		return 12;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.MISSILE_TURRET);
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
	 * @see {@code MissileTurretAnimation#ANIM_MISSILE_TURRET_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1.5F * 20);
	}

	protected List<Vec3> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.BARREL);
	}

	public TurretProjectileVelocity getProjectileVelocityData(LivingEntity target) {
		Vec3 dir = MathUtil.getTargetDirection(this, target);
		Vec3 vel = dir.scale(this.getMissileSpeed());

		return TurretProjectileVelocity
			.init(this)
			.setLaunchAngle(0.1f)
			.setVelocity(vel.x(), vel.y(), vel.z());
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

	// UsesMissile //

	/**
	 * {@inheritDoc}
	 * <hr/>
	 * For the {@link MissileTurretEntity Missile Turret}, the missile fuel is 75% of its maximum
	 * range.
	 *
	 * @return The distance in blocks that this missile can travel while its engine is firing.
	 */
	public int getMissileFuel() {
		return (int) (this.getMaxAttackRange() * 0.75);
	}

	/**
	 * {@inheritDoc}
	 * <hr/>
	 * For the {@link MissileTurretEntity Missile Turret}, the missile speed is determined by its
	 * level:
	 * <ul>
	 *     <li><b>Level 1:</b> 20 m/s</li>
	 *     <li><b>Level 2:</b> 25 m/s</li>
	 *     <li><b>Level 3:</b> 30 m/s</li>
	 * </ul>
	 *
	 * @return The speed in m/s (meters per second or blocks per second) that the missile will move.
	 */
	public double getMissileSpeed() {
		return MISSILE_SPEED[this.getTrackedLevel() - 1];
	}

	/**
	 * {@inheritDoc}
	 * <hr/>
	 * For the {@link MissileTurretEntity Missile Turret}, the missile turn rate is determined by
	 * its level:
	 * <ul>
	 *     <li><b>Level 1:</b> 30°/s</li>
	 *     <li><b>Level 2:</b> 45°/s</li>
	 *     <li><b>Level 3:</b> 60°/s</li>
	 * </ul>
	 *
	 * @return
	 */
	public double getMissileTurnRate() {
		return MISSILE_TURN_RATE[this.getTrackedLevel() - 1];
	}

	// //////////// //
	// OVERRIDABLES //
	// //////////// //

	@Environment(EnvType.CLIENT)
	@Override
	protected void updateAnimations() {
		super.updateAnimations();
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
			7.5,
			10.0,
			15.0
		};

		PIERCE_LEVELS = new byte[] {
			1,
			1,
			2
		};

		MISSILE_SPEED = new double[] {
			20,
			25,
			30
		};

		MISSILE_TURN_RATE = new double[] {
			30,
			45,
			60
		};

		OFFSETS = Map.of(
			Offsets.BARREL, List.of(
				new Vec3(0, 0.1875, 0.625),
				new Vec3(0, 0, 0.625),
				new Vec3(0, -0.1875, 0.625)
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
