package com.virus5600.defensive_measures.entity.turrets.tier1;

import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Ballista Turret entity.
 * <br><br>
 * A Ballista Turret is a wooden turret that shoots bolts at enemies. It has a long range and deals
 * a fair amount of damage while providing a good amount of pierce level and fire rate.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 25</li>
 *     <li><b>Base Damage:</b> 4.0</li>
 *     <li><b>Base Pierce Level:</b> 5</li>
 *     <li><b>Attack Cooldown:</b> 2.5 seconds</li>
 *     <li><b>Attack Range:</b> 16 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> ±20°</li>
 *     <li><b>Armor:</b> 0</li>
 *     <li><b>Armor Toughness:</b> 0</li>
 * </ul>
 *
 * @see TurretEntity
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BallistaTurretEntity extends TurretEntity {
	/**
	 * Defines how many seconds the ballista should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 2.5 seconds <b>(20 ticks times 2.5 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = (int) (20 * 2.5);
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
	public BallistaTurretEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world, TurretMaterial.WOOD, ModEntities.BALLISTA_BOLT, ModItems.BALLISTA_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_BALLISTA_SHOOT);

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
	protected void defineSynchedData(Builder builder) {
		// Initialize standard data trackers
		super.defineSynchedData(builder);
	}

	@NotNull
	public static AttributeSupplier.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(25);
		TurretEntity.setTurretMaxRange(16 + ModEntities.BALLISTA_TURRET.getDimensions().eyeHeight());

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
	public void onRemoval(Entity.@NonNull RemovalReason reason) {
		// Shoot the animation projectile as a real projectile when the turret is destroyed
		if (this.isDeadOrDying()) {
			this.shoot(
				TurretEntity.TurretProjectileVelocity
					.init(this)
					.setDirectionalVelocity(0.5f)
			);
		}

		super.onRemoval(reason);
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
		return 20;
	}

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource source) {
		return ModSoundEvents.TURRET_BALLISTA_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_BALLISTA_DESTROYED;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.BALLISTA_TURRET);
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
	 * @see {@code BallistaTurretAnimation#ANIM_BALLISTA_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1.5F * 20);
	}

	protected List<Vec3> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.BOLT_HOLDER);
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

	@Override
	protected int getSetupAnimDuration() {
		return (int) (2.0F * 20);
	}

	@Override
	protected int getTeardownAnimDuration() {
		return (int) (2.0F * 20);
	}

	// /////////////////// //
	// LOCAL CLASSES/ENUMS //
	// /////////////////// //
	public enum Offsets {
		BOLT_HOLDER
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		DAMAGE = new double[] {
			3.5,
			7.0,
			12.0
		};

		PIERCE_LEVELS = new byte[] {
			5,
			7,
			10
		};

		OFFSETS = Map.of(
			Offsets.BOLT_HOLDER, List.of(
				new Vec3(0, 0, 0.875)
			)
		);

		// HEAL SOUNDS
		HEAL_SOUNDS = new HashMap<>();
		HEAL_SOUNDS.put(Items.STICK, ModSoundEvents.TURRET_REPAIR_WOOD);
		HEAL_SOUNDS.put(Items.STRING, ModSoundEvents.TURRET_REPAIR_BOW);

		final List<Item> WOODS = new ArrayList<>(TurretEntity.PLANKS.stream().toList());
		WOODS.addAll(TurretEntity.LOGS);
		WOODS.forEach(item -> HEAL_SOUNDS.put(item, ModSoundEvents.TURRET_REPAIR_WOOD));

		healables = new HashMap<>() {
			{
				put(Items.STICK, 1.0f);
				put(Items.STRING, 1.0f);

				TurretEntity.PLANKS.forEach(item -> put(item, 3.0f));
				TurretEntity.LOGS.forEach(item -> put(item, 25.0f));
			}
		};

		effectSource = new HashMap<>() {
			{
				for (Item item : TurretEntity.LOGS) {
					put(item, List.<Object[]>of(
						new Object[] {MobEffects.ABSORPTION, 60, 2}
					));
				}
			}
		};
	}
}
