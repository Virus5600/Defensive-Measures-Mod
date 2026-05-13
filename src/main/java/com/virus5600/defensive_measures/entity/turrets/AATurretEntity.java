package com.virus5600.defensive_measures.entity.turrets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.registry.tag.ModEntityTypeTags;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * flying 64 blocks or more away from the ground. It deals an AoE damage capable of downing flying
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
 *     <li><b>Attack Range:</b> 96 blocks</li>
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
 * @version 1.0.0
 */
public class AATurretEntity extends TurretEntity {
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 10 seconds <b>(20 ticks times 10 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = 20 * 10;
	private static final Vec3d HINGE_POS;
	private static final List<Vec3d> BARRELS;
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
	public AATurretEntity(EntityType<? extends TurretEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.METAL, ModEntities.FLAK_PROJECTILE, ModItems.AA_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_ANTI_AIR_SHOOT);
		this.setHealSound(ModSoundEvents.TURRET_REPAIR_METAL);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
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
		return this.attackGoal.isWithinRotationLimit(target) &&
			target.getType().isIn(ModEntityTypeTags.FLYING_HOSTILES);
	}

	@Override
	protected void initDataTracker(Builder builder) {
		// Initialize standard data trackers
		super.initDataTracker(builder);
	}

	public static @NotNull DefaultAttributeContainer.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(100);
		TurretEntity.setTurretMaxRange(96 + ModEntities.AA_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes()
			.add(EntityAttributes.ARMOR, 2)
			.add(EntityAttributes.ARMOR_TOUGHNESS, 1);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		TurretProjectileVelocity velocityData = TurretProjectileVelocity.init(this)
			.setPower(5f)
			.setUpwardVelocityMultiplier(0.1f)
			.setVelocity(target);

		super.shootAt(velocityData);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		Item usedItem = player.getStackInHand(hand).getItem();

		if (this.isHealableItem(usedItem) && this.getHealSound() != ModSoundEvents.TURRET_REPAIR_WOOD) {
			this.setHealSound(HEAL_SOUNDS.get(usedItem));
		}

		return super.interactMob(player, hand);
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override @Nullable
	protected SoundEvent getHurtSound(DamageSource src) {
		return ModSoundEvents.TURRET_ANTI_AIR_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_ANTI_AIR_DESTROYED;
	}

	@Override
	public int getMinLookPitchChange() {
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

		// Set variables that will be used for logic
		boolean isShooting = this.getTrackedShooting();

		// Handles the Flash particle for when the AA shoots
		if (isShooting) {
			Vec3d barrelPos = this.getRelativePos(this.getCurrentBarrel(false)),
				velMod = this.getRelativePos(0, 0, 1.5).subtract(this.getEyePos());

			this.getEntityWorld()
				.addParticleClient(
					ModParticles.CANNON_FLASH,
					barrelPos.getX(), barrelPos.getY(), barrelPos.getZ(),
					velMod.getX(), velMod.getY(), velMod.getZ()
				);
		}
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

		HINGE_POS = new Vec3d(0, 0.0625, -0.59375);

		BARRELS = List.of(
			new Vec3d(0, 0, 2.5)
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
				new Object[] { StatusEffects.RESISTANCE, 60, 2 }
			)
		);
	}
}
