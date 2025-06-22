package com.virus5600.defensive_measures.entity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
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

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

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
 *     <li><b>Y Firing Arc:</b> ±25°</li>
 * </ul>
 *
 * @see TurretEntity
 * @see GeoEntity
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class BallistaTurretEntity extends TurretEntity implements GeoEntity {
	/**
	 * Defines how many seconds the ballista should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 2.5 seconds <b>(20 ticks times 2.5 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = (int) (20 * 2.5);
	private static final Map<String, RawAnimation> ANIMATIONS;
	private static final Map<Offsets, List<Vec3d>> OFFSETS;
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

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
	private boolean animPlayed = false;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public BallistaTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.WOOD, ModEntities.BALLISTA_ARROW, ModItems.BALLISTA_TURRET);

		this.setShootSound(ModSoundEvents.TURRET_BALLISTA_SHOOT);
		this.setHealSound(ModSoundEvents.TURRET_REPAIR_WOOD);
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
	protected void initDataTracker(DataTracker.Builder builder) {
		// Initialize standard data trackers
		super.initDataTracker(builder);
	}

	public static Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(25);
		TurretEntity.setTurretMaxRange(16 + ModEntities.BALLISTA_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes();
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		float dist = (float) this.getPos().distanceTo(target.getPos());

		TurretProjectileVelocity velocityData = TurretProjectileVelocity.init(this)
			.setVelocity(target)
			.setUpwardVelocityMultiplier(dist * 0.125f);

		super.shootAt(velocityData);

		this.triggerAnim("Attack", "shoot");
	}

	@Override
	public void onRemove(Entity.RemovalReason reason) {
		if (this.isDead()) {
			this.shoot(
				TurretEntity.TurretProjectileVelocity
					.init(this)
					.setDirectionalVelocity(0.5f)
			);
		}

		super.onRemove(reason);
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
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override
	public int getMaxLookPitchChange() {
		return 25;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ModSoundEvents.TURRET_BALLISTA_HURT;
	}

	@Nullable
	@Override
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

	// ///////////////////// //
	// ANIMATION CONTROLLERS //
	// ///////////////////// //

	private  PlayState deathController(final AnimationTest<BallistaTurretEntity> state) {
		if (!this.isAlive() && !this.animPlayed) {
			this.animPlayed = true;
			state.setAnimation(ANIMATIONS.get("Death"));
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	private PlayState idleController(final AnimationTest<BallistaTurretEntity> state) {
		return state
			.setAndContinue(ANIMATIONS.get("Idle"));
	}

	private PlayState shootController(final AnimationTest<BallistaTurretEntity> state) {
		return PlayState.STOP;
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	// GeoEntity //
	@Override
	public void registerControllers(final ControllerRegistrar controllers) {
		controllers
			.add(
				new AnimationController<>("Death", 10, this::deathController),
				new AnimationController<>("Idle", 10, this::idleController),
				new AnimationController<>("Attack", 10, this::shootController)
					.triggerableAnim("shoot", ANIMATIONS.get("Shoot"))
			);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //
	protected List<Vec3d> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.BOLT_HOLDER);
	}

	public double getProjectileDamage() {
		return DAMAGE[this.getLevel() - 1];
	}

	public byte getProjectilePierceLevel() {
		return PIERCE_LEVELS[this.getLevel() - 1];
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
				new Vec3d(0, 0, 0.875)
			)
		);

		ANIMATIONS = Map.of(
			"Death", RawAnimation.begin()
				.thenPlayAndHold("animation.ballista.death"),
			"Idle", RawAnimation.begin()
				.thenLoop("animation.ballista.setup"),
			"Shoot", RawAnimation.begin()
				.thenPlay("animation.ballista.shoot")
		);

		HEAL_SOUNDS = new HashMap<>() {
			{
				put(Items.STICK, ModSoundEvents.TURRET_REPAIR_WOOD);
				put(Items.STRING, ModSoundEvents.TURRET_REPAIR_BOW);

				final List<Item> WOODS = new ArrayList<>(TurretEntity.PLANKS.stream().toList());
				WOODS.addAll(TurretEntity.LOGS);
				WOODS.forEach(item -> put(item, ModSoundEvents.TURRET_REPAIR_WOOD));
			}
		};

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
					put(item, new ArrayList<>() {
						{
							add(new Object[] {StatusEffects.ABSORPTION, 60, 2});
						}
					});
				}
			}
		};
	}
}
