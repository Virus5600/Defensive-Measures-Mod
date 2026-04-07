package com.virus5600.defensive_measures.entity.turrets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
 * @see TurretEntity
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.1.0
 */
public class CannonTurretEntity extends TurretEntity {
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 5 seconds <b>(20 ticks times 5 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20 * 5;
	private static final Map<Offsets, List<Vec3d>> OFFSETS;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private static final Map<Item, SoundEvent> HEAL_SOUNDS;
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
	public CannonTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
//		super(entityType, world, TurretMaterial.METAL, ModEntities.CANNONBALL, ModItems.CANNON_TURRET);
		super(entityType, world, TurretMaterial.METAL, ModItems.CANNON_TURRET);

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

	public static @NotNull DefaultAttributeContainer.Builder setAttributes() {
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
		float dist = (float) this.getTrackedPosition()
			.getPos()
			.distanceTo(target.getTrackedPosition().getPos());

		TurretProjectileVelocity velocityData = TurretProjectileVelocity.init(this)
			.setVelocity(target)
			.setUpwardVelocityMultiplier(dist * 0.1625f);

		super.shootAt(velocityData);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		Item usedItem = player.getStackInHand(hand).getItem();

		if (this.isHealableItem(usedItem) && this.getHealSound() != ModSoundEvents.TURRET_REPAIR_WOOD) {
			this.setHealSound(HEAL_SOUNDS.get(usedItem));
		}

		return super.interactMob(player, hand);
	}

	@Override
	public void tick() {
		super.tick();

		// Client Side
		if (this.getEntityWorld().isClient()) {
			this.updateAnimations();
		}
		// Server Side
		else {
			int updateCountdownTicks = this.attackGoal.getUpdateCountdownTicks(),
				afterAttackTick = 5,
				beforeAttackTick = TOTAL_ATT_COOLDOWN - afterAttackTick;
			boolean isCharging = updateCountdownTicks > afterAttackTick && updateCountdownTicks < beforeAttackTick;

			this.setTrackedLockedButNotAttacking(isCharging);
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
	public SoundEvent getHealSound() {
		return ModSoundEvents.TURRET_REPAIR_METAL;
	}

	@Override
	public float getMinAttackRange() {
		return 3f;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //
	protected List<Vec3d> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.BARREL);
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

		// Set variables that will be used for the logic
		boolean isLockedButNotAttacking = this.getTrackedLockedButNotAttacking();
		boolean isShooting = this.getTrackedShooting();

		// Handles Fuse particle for when locked but not yet shooting
		if (!isShooting && isLockedButNotAttacking) {
			Vec3d fusePos = this.getRelativePos(
				OFFSETS.get(Offsets.FUSE).getFirst()
			);

			this.getEntityWorld()
				.addParticleClient(
					ModParticles.CANNON_FUSE,
					fusePos.getX(), fusePos.getY(), fusePos.getZ(),
					0, 0.225, -0.5
				);
		}

		// Handles the Flash particle for when the cannon shoots
		if (isShooting) {
			Vec3d barrelPos = this.getRelativePos(this.getCurrentBarrel(false)),
				velocityModifier = this.getRelativePos(0, 0, 1.5).subtract(this.getEyePos());

			this.getEntityWorld()
				.addParticleClient(
					ModParticles.CANNON_FLASH,
					barrelPos.getX(), barrelPos.getY(), barrelPos.getZ(),
					velocityModifier.getX(), velocityModifier.getY(), velocityModifier.getZ()
				);
		}
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

		HEAL_SOUNDS = new HashMap<>() {
			{
				put(Items.IRON_NUGGET, ModSoundEvents.TURRET_REPAIR_METAL);
				put(Items.IRON_INGOT, ModSoundEvents.TURRET_REPAIR_METAL);
				put(Items.IRON_BLOCK, ModSoundEvents.TURRET_REPAIR_METAL);
			}
		};

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
