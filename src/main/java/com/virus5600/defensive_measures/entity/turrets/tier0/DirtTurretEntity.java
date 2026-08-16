package com.virus5600.defensive_measures.entity.turrets.tier0;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
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
import com.virus5600.defensive_measures.entity.turrets.interfaces.UsesBlockProjectile;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Represents the Dirt Turret entity
 * <br><br>
 * A joke turret suggested by one of the players who played the mod. While it might be a joke
 * turret, it is still a turret that can be used to defend against enemies. It has a very low
 * health and deals very low damage, but it can be used to distract enemies and buy time for other
 * turrets to deal damage. It can also be used to block enemy projectiles and protect other turrets.
 * <br><br>
 * Additionally, while it has low damage, the dirt it "shoots" gives a Blindness effect for a short
 * while to anyone hit by the dirt. This can be used to blind enemies and make them easier to kill.
 * However, the Blindness effect is not very long, so it is not very effective against enemies with
 * high health or armor.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 10</li>
 *     <li><b>Base Damage:</b> 1.0</li>
 *     <li><b>Base Pierce Level:</b> 0</li>
 *     <li><b>Attack Cooldown:</b> 2.5 seconds</li>
 *     <li><b>Attack Range:</b> 8 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> ±7°</li>
 *     <li><b>Armor:</b> 2</li>
 *     <li><b>Armor Toughness:</b> 2</li>
 * </ul>
 *
 * @see TurretEntity
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class DirtTurretEntity extends TurretEntity implements UsesBlockProjectile {
	/**
	 * Defines how many seconds the ballista should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 2.5 seconds <b>(20 ticks times 2.5 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = (int) (20 * 2.5);
	private static final Map<DirtTurretEntity.Offsets, List<Vec3>> OFFSETS;
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
	public DirtTurretEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world, TurretMaterial.DIRT, ModEntities.SPECTRAL_ARROW, ModItems.DIRT_TURRET);

		this.addHealables(healables)
			.addEffectSource(effectSource)
			.setShootSound(ModSoundEvents.TURRET_DIRT_SHOOT)
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

	public static AttributeSupplier.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(10);
		TurretEntity.setTurretMaxRange(8 + ModEntities.BALLISTA_TURRET.getDimensions().eyeHeight());

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
		return 7;
	}

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource source) {
		return ModSoundEvents.TURRET_DIRT_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_DIRT_DESTROYED;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.DIRT_TURRET);
	}

	@Override
	public SoundEvent getEntityRemoveSound() {
		return ModSoundEvents.TURRET_REMOVED_DIRT;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //

	/**
	 * {@inheritDoc}
	 * @see {@code DirtTurretAnimation#ANIM_DIRT_TURRET_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1.0F * 20);
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
		BARREL
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		DAMAGE = new double[] {
			1,
			2.5,
			5
		};

		PIERCE_LEVELS = new byte[] {
			0,
			0,
			1
		};

		OFFSETS = Map.of(
			Offsets.BARREL, List.of(
				new Vec3(0, 0, 0.5)
			)
		);

		// HEAL SOUNDS
		HEAL_SOUNDS = Map.of(
			Items.GRASS_BLOCK, ModSoundEvents.TURRET_REPAIR_DIRT,
			Items.DIRT, ModSoundEvents.TURRET_REPAIR_DIRT
		);

		healables = Map.of(
			Items.GRASS_BLOCK, 2.5f,
			Items.DIRT, 5.0f
		);

		effectSource = Map.of(
			Items.DIRT, List.<Object[]>of(
				new Object[] {MobEffects.ABSORPTION, 30, 2}
			)
		);
	}
}
