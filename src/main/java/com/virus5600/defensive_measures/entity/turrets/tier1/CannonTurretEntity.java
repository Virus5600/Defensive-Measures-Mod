package com.virus5600.defensive_measures.entity.turrets.tier1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
 *     <li><b>Attack Range:</b> 3-24 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> -20 to 22.5°</li>
 *     <li><b>Armor:</b> 3</li>
 *     <li><b>Armor Toughness:</b> 2</li>
 * </ul>
 *
 * @see TurretEntity
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class CannonTurretEntity extends TurretEntity {
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 5 seconds <b>(20 ticks times 5 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20 * 5;
	private static final Map<Offsets, List<Vec3>> OFFSETS;
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
	public CannonTurretEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.CANNONBALL, ModItems.CANNON_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_CANNON_SHOOT);
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
	protected void defineSynchedData(@NonNull Builder builder) {
		// Initialize standard data trackers
		super.defineSynchedData(builder);
	}

	public static @NotNull net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(50);
		TurretEntity.setTurretMaxRange(24 + ModEntities.CANNON_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes()
			.add(Attributes.ARMOR, 3)
			.add(Attributes.ARMOR_TOUGHNESS, 2);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void performRangedAttack(@NonNull LivingEntity target, float pullProgress) {
		TurretProjectileVelocity velocityData = this.getProjectileVelocityData(target);

		velocityData.setUncertainty(
			velocityData.getUncertainty() *
				0.75f
		);

		super.shootAt(velocityData);
	}

	@Override
	public @NonNull InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
		Item usedItem = player.getItemInHand(hand).getItem();

		if (this.isHealableItem(usedItem) && this.getHealSound() != ModSoundEvents.TURRET_REPAIR_WOOD) {
			this.setHealSound(HEAL_SOUNDS.get(usedItem));
		}

		return super.mobInteract(player, hand);
	}

	@Override
	public void tick() {
		super.tick();

		// Client Side
		if (this.level().isClientSide()) {
			this.updateAnimations();
		}
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override
	public int getMaxHeadXRot() {
		return 23;
	}

	@Override
	public int getMinHeadXRot() {
		return -20;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(@NonNull DamageSource source) {
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

	/**
	 * Sets the minimum attack range of the Cannon Turret to 3 blocks.
	 *
	 * @return The minimum attack range of the Cannon Turret, which is 3 blocks.
	 */
	@Override
	public float getMinAttackRange() {
		return 3f;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //

	/**
	 * {@inheritDoc}
	 * @see {@code CannonTurretAnimation#ANIM_CANNON_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1F * 20);
	}

	protected List<Vec3> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.BARREL);
	}

	public TurretProjectileVelocity getProjectileVelocityData(LivingEntity target) {
		float dist = (float) position()
			.distanceTo(target.position());

		return TurretProjectileVelocity
			.init(this)
			.setLaunchAngle(dist * 0.1625f)
			.setSpeed(1.375f)
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
		return Optional.of(-15f);
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
			Vec3 fusePos = this.getRelativePos(
				OFFSETS.get(Offsets.FUSE).getFirst()
			);

			this.level()
				.addParticle(
					ModParticles.CANNON_FUSE,
					fusePos.x(), fusePos.y(), fusePos.z(),
					0, 0.225, -0.5
				);
		}

		// Handles the Flash particle for when the cannon shoots
		if (isShooting) {
			Vec3 barrelPos = this.getRelativePos(this.getCurrentBarrel(false)),
				velocityModifier = this.getRelativePos(0, 0, 1.5).subtract(this.getEyePosition());

			this.level()
				.addParticle(
					ModParticles.CANNON_FLASH,
					barrelPos.x(), barrelPos.y(), barrelPos.z(),
					velocityModifier.x(), velocityModifier.y(), velocityModifier.z()
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
				new Vec3(0, 0, 0.875)
			),
			Offsets.FUSE, List.of(
				new Vec3(0, 0.25, -0.55)
			)
		);

		HEAL_SOUNDS = Map.of(
			Items.IRON_NUGGET, ModSoundEvents.TURRET_REPAIR_METAL,
			Items.IRON_INGOT, ModSoundEvents.TURRET_REPAIR_METAL,
			Items.IRON_BLOCK, ModSoundEvents.TURRET_REPAIR_METAL
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
