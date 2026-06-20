package com.virus5600.defensive_measures.entity.turrets.tier0;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
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
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Pellet Turret entity.
 * <br><br>
 * A Pellet Turret is a basic wooden turret that shoots pellets at enemies. It has a short range
 * and deals a small amount of damage while providing a high fire rate with low damage. It can be
 * used to strategically to bait enemies into a location, letting the turret be a sacrificial
 * pawn to group enemies together.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 20</li>
 *     <li><b>Base Damage:</b> 2.0</li>
 *     <li><b>Base Pierce Level:</b> 0</li>
 *     <li><b>Attack Cooldown:</b> 2 seconds</li>
 *     <li><b>Attack Range:</b> 10 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> ±30°</li>
 *     <li><b>Armor:</b> 0</li>
 *     <li><b>Armor Toughness:</b> 0</li>
 * </ul>
 *
 * @see TurretEntity
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class PelletTurretEntity extends TurretEntity {
	private static final EntityDataAccessor<Integer> ATTACK_COOLDOWN;

	/**
	 * Defines how many seconds the ballista should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 2.0 seconds <b>(20 ticks times 2.0 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = (int) (20 * 2.0);
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
	public PelletTurretEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world, TurretMaterial.WOOD, ModEntities.MG_BULLET, ModItems.PELLET_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_PELLET_SHOOT);

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

		builder.define(ATTACK_COOLDOWN, TOTAL_ATT_COOLDOWN)
		;
	}

	public static Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(20);
		TurretEntity.setTurretMaxRange(10 + ModEntities.BALLISTA_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes();
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
	public @NonNull InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
		Item usedItem = player.getItemInHand(hand).getItem();

		if (this.isHealableItem(usedItem)) {
			this.setHealSound(HEAL_SOUNDS.get(usedItem));
		}

		return super.mobInteract(player, hand);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			this.updateAnimations();
		}
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override
	public int getMaxHeadXRot() {
		return 30;
	}

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource source) {
		return ModSoundEvents.TURRET_PELLET_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_PELLET_DESTROYED;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.PELLET_TURRET);
	}

	@Override
	public SoundEvent getEntityRemoveSound() {
		return ModSoundEvents.TURRET_REMOVED_WOOD;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //

	/**
	 * {@inheritDoc}
	 * @see {@code PelletTurretAnimation#ANIM_PELLET_TURRET_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1.0F * 20);
	}

	protected List<Vec3> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.TUBE);
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
		int attCooldown = TOTAL_ATT_COOLDOWN;
		MobEffectInstance effectInstance = this.getEffect(MobEffects.HASTE);

		if (effectInstance != null) {
			double level = effectInstance.getAmplifier() + 1;
			double denominator = level > 2 ? 256 : 10;

			attCooldown = (int) (attCooldown * (1 - (level / denominator)));
		}

		attCooldown = Math.max(attCooldown, (int) (0.5 * 20));

		// Updates the attack goal's attack interval, allowing the haste effect to properly speed
		// up the turret's fire rate
		if (
			!this.level().isClientSide()
				&& this.attackGoal != null
				&& this.attackGoal.getMaxIntervalTicks() != attCooldown
		) {
			this.attackGoal.setMaxIntervalTicks(attCooldown);
			this.attackGoal.setMinIntervalTicks(attCooldown);
			this.entityData.set(ATTACK_COOLDOWN, attCooldown);
		}

		return attCooldown;
	}

	// //////////// //
	// OVERRIDABLES //
	// //////////// //

	@Override
	protected int getSetupAnimDuration() {
		return (int) (1.75F * 20);
	}

	@Override
	protected int getTeardownAnimDuration() {
		return (int) (1.75F * 20);
	}

	// /////////////////// //
	// LOCAL CLASSES/ENUMS //
	// /////////////////// //
	public enum Offsets {
		TUBE
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		ATTACK_COOLDOWN = SynchedEntityData.defineId(PelletTurretEntity.class, EntityDataSerializers.INT);

		DAMAGE = new double[] {
			2,
			3.75,
			5
		};

		PIERCE_LEVELS = new byte[] {
			0,
			0,
			1
		};

		OFFSETS = Map.of(
			Offsets.TUBE, List.of(
				new Vec3(0, 0, 0.5)
			)
		);

		// HEAL SOUNDS
		HEAL_SOUNDS = new HashMap<>();
		HEAL_SOUNDS.put(Items.STICK, ModSoundEvents.TURRET_REPAIR_WOOD);
		HEAL_SOUNDS.put(Items.LEATHER, ModSoundEvents.TURRET_REPAIR_LEATHER);

		final List<Item> WOODS = new ArrayList<>(TurretEntity.PLANKS.stream().toList());
		WOODS.addAll(TurretEntity.LOGS);
		WOODS.forEach(item -> HEAL_SOUNDS.put(item, ModSoundEvents.TURRET_REPAIR_WOOD));

		healables = new HashMap<>() {
			{
				put(Items.STICK, 1.0f);
				put(Items.LEATHER, 3.0f);

				TurretEntity.PLANKS.forEach(item -> put(item, 5.0f));
				TurretEntity.LOGS.forEach(item -> put(item, 25.0f));
			}
		};

		effectSource = new HashMap<>() {
			{
				put(Items.LEATHER, List.<Object[]>of(
					new Object[] {MobEffects.HASTE, 30, 2}
				));

				for (Item item : TurretEntity.LOGS) {
					put(item, List.<Object[]>of(
						new Object[] {MobEffects.ABSORPTION, 60, 2}
					));
				}
			}
		};
	}
}
