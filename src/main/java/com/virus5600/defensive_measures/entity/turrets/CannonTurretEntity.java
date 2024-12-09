package com.virus5600.defensive_measures.entity.turrets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.TargetOtherTeamGoal;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CannonTurretEntity extends TurretEntity implements GeoEntity, RangedAttackMob, Itemable {
	/**
	 * Defines how many seconds the cannon should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 5 seconds <b>(20 ticks times 5 seconds)</b>.
	 */
	private static final int totalAttCooldown = 20 * 5;
	private static final TrackedData<Boolean> FUSE_LIT;
	/**
	 * Contains all the items that can heal this entity.
	 */
	private static Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity
	 */
	private static Map<Item, List<Object[]>> effectSource;

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
	/**
	 * Defines the current target of this Cannon.
	 */
	@Nullable
	private LivingEntity currentTarget = null;
	private double attCooldown = totalAttCooldown;
	private boolean animPlayed = false;

	//////////////////
	// CONSTRUCTORS //
	//////////////////
	public CannonTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.METAL, ArrowEntity.class);
		this.setShootSound(ModSoundEvents.TURRET_CANNON_SHOOT);
		this.setHealSound(ModSoundEvents.TURRET_REPAIR_METAL);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
	}

	//////////////////
	// INITIALIZERS //
	//////////////////
	@Override
	protected void initGoals() {
		// Goals
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 0, totalAttCooldown, 16.625F));
		this.goalSelector.add(2, new LookAtEntityGoal(this, MobEntity.class, 8.0F, 0.02F, true));
		this.goalSelector.add(8, new LookAroundGoal(this));

		// Targets
		this.targetSelector.add(1, new ActiveTargetGoal<MobEntity>(this, MobEntity.class, 10, true, false, (entity) -> {
			return entity instanceof Monster;
		}));
		this.targetSelector.add(3, new TargetOtherTeamGoal(this));
	}

	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);

		builder.add(FUSE_LIT, false);
	}

	public static DefaultAttributeContainer.Builder setAttributes() {
		return TurretEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
	}

	/////////////////////
	// PROCESS METHODS //
	/////////////////////

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		if (target == null) {
			if (this.isShooting())
				this.setShooting(false);
			return;
		}

		this.setShooting(true);

		try {
			double vx = (target.getX() - this.getX()) * 1.0625;
			double vy = target.getBodyY((double) 2 / 3) - this.getY() + 0.25;
			double vz = (target.getZ() - this.getZ()) * 1.0625;
			double variance = Math.sqrt(vx * vx + vz * vz);
			float divergence = this.getWorld().getDifficulty().getId() * 2;
//			ProjectileEntity projectile = (ProjectileEntity) new CannonballEntity(ModEntities.CANNONBALL, this, vx, vy, vz, this.getWorld());
			ProjectileEntity projectile = (ProjectileEntity) new ArrowEntity(EntityType.ARROW, this.getWorld());

			projectile.setVelocity(vx, vy + variance * 0.1f, vz, 1.5f, divergence);
			projectile.setPos(this.getX(), this.getY() + 0.5, this.getZ());

			this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
			this.getWorld().spawnEntity(projectile);
			this.triggerAnim("Firing Sequence", "Shoot");
		} catch (IllegalArgumentException | SecurityException e) {
			e.printStackTrace();

			DefensiveMeasures.LOGGER.error("");
			DefensiveMeasures.LOGGER.error("	 " + DefensiveMeasures.MOD_ID.toUpperCase() + " ERROR OCCURRED	 ");
			DefensiveMeasures.LOGGER.error("===== ERROR MSG START =====");
			DefensiveMeasures.LOGGER.error("LOCALIZED ERROR MESSAGE:");
			DefensiveMeasures.LOGGER.error(e.getLocalizedMessage());
			DefensiveMeasures.LOGGER.error("");
			DefensiveMeasures.LOGGER.error("ERROR MESSAGE:");
			DefensiveMeasures.LOGGER.error(e.getMessage());
			DefensiveMeasures.LOGGER.error("===== ERROR MSG END =====");
			DefensiveMeasures.LOGGER.error("");
		}
	}

	@Override
	public void tick() {
		super.tick();

		this.setYaw(0);
		this.setBodyYaw(0);

		if (this.isShooting() && this.hasTarget()) {
			if (!this.getShootingFXDone()) {
				this.triggerAnim("Firing Sequence", "firing_sequence");
			}
		}
	}

	// TODO: Move code to TurretEntity then add a new protected variable where the item counterpart of the turret will be defined.
	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		return Itemable.tryItem(player, hand, this, ModItems.TURRET_REMOVER, ModItems.CANNON_TURRET)
			.orElse(super.interactMob(player, hand));
	}

	//////////////////////////////////////
	// QUESTION METHODS (True or False) //
	//////////////////////////////////////

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////

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

	protected boolean isFuseLit() {
		return this.dataTracker.get(FUSE_LIT);
	}
	protected void setFuseLit(boolean lit) {
		this.dataTracker.set(FUSE_LIT, lit);
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.CANNON_TURRET);
	}

	///////////////////////////
	// ANIMATION CONTROLLERS //
	///////////////////////////

	private <E extends CannonTurretEntity>PlayState deathController(final AnimationState<E> event) {
		if (!this.isAlive() && !animPlayed) {
			animPlayed = true;
			event.setAnimation(
				RawAnimation
					.begin()
					.thenPlayAndHold("animation.cannon_turret.death")
			);
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	private <E extends CannonTurretEntity>PlayState idleController(final AnimationState<E> event) {
		return event
			.setAndContinue(
				RawAnimation
					.begin()
					.thenLoop("animation.cannon_turret.look_at_target")
					.thenLoop("animation.cannon_turret.setup")
			);
	}

	// TODO: Fix particle key-framing
	private <E extends CannonTurretEntity>PlayState firingSequenceController(final AnimationState<E> event) {
		Vec3d fusePos = this.getRelativePos(0, 0, 0),
			barrelPos = this.getRelativePos(0, 0, 0);

		String shootAnim = "animation.cannon_turret.shoot",
			chargeAnim = "animation.cannon_turret.fuse";

		// Shooting sequence
		event.getController()
			.setParticleKeyframeHandler((state) -> {
				String locator = state.getKeyframeData().getLocator(),
					effectName = state.getKeyframeData().getEffect(),
					currentState = "fuse";

				System.out.println("Locator: " + locator + " | Effect: " + effectName);
				if (this.hasTarget() && this.isShooting()) {
					if (!this.getShootingFXDone()) currentState = "firingSequence";
					else currentState = "shoot";
				}

				state.getController()
					.setAnimation(
						RawAnimation
							.begin()
							.thenPlay(currentState.equals("fuse") ? chargeAnim : shootAnim)
					);
			});

		return PlayState.CONTINUE;
	}

	///////////////////////////////
	// INTERFACE IMPLEMENTATIONS //
	///////////////////////////////

	// GeoEntity //
	@Override
	public void registerControllers(final ControllerRegistrar controllers) {
		controllers
			.add(
				new AnimationController<>(this, "Death", this::deathController),
				new AnimationController<>(this, "Idle", this::idleController),
				new AnimationController<>(this, "Firing Sequence", this::firingSequenceController)
					.triggerableAnim("Charge", RawAnimation.begin().thenPlay("animation.cannon_turret.fuse"))
					.triggerableAnim("Shoot", RawAnimation.begin().thenPlay("animation.cannon_turret.shoot"))
			);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}

	///////////////////
	// LOCAL CLASSES //
	///////////////////

	///////////////////////
	// STATIC INITIALIZE //
	///////////////////////

	static {
		FUSE_LIT = DataTracker.registerData(CannonTurretEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

		healables = new HashMap<Item, Float>() {
			{
				for (Item item : TurretEntity.PLANKS)
					put(item, 1.0f);
				put(Items.IRON_NUGGET, 1f);
				put(Items.IRON_INGOT, 10f);
				put(Items.IRON_BLOCK, 100f);
			}
		};

		effectSource = new HashMap<Item, List<Object[]>>() {
			{
				put(Items.IRON_BLOCK, new ArrayList<Object[]>() {
					{
						add(new Object[] {StatusEffects.ABSORPTION, 60, 2});
					}
				});
			}
		};
	}
}
