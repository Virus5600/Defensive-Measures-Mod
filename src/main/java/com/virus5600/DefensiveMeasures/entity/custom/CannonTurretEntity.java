package com.virus5600.DefensiveMeasures.entity.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.entity.TurretMaterial;
import com.virus5600.DefensiveMeasures.entity.ai.goal.TargetOtherTeamGoal;
import com.virus5600.DefensiveMeasures.entity.projectile.CannonballEntity;
import com.virus5600.DefensiveMeasures.item.ModItems;
import com.virus5600.DefensiveMeasures.particle.ModParticles;
import com.virus5600.DefensiveMeasures.sound.ModSoundEvents;

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
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
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

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class CannonTurretEntity extends TurretEntity implements IAnimatable, RangedAttackMob, Itemable {
	private static final int TOTAL_ATTACK_COOLDOWN = 20 * 5;
	private static final TrackedData<Boolean> FUSE_LIT;
	/**
	 * Contains all the items that can heal this entity.
	 */
	private static Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity.
	 */
	private static Map<Item, List<Object[]>> effectSource;
	private AnimationFactory factory = new AnimationFactory(this);
	@Nullable
	private LivingEntity currentTarget = null;
	private double attCooldown = TOTAL_ATTACK_COOLDOWN;

	// CONSTRUCTORS //
	public CannonTurretEntity(final EntityType<? extends GolemEntity> entityType, final World world) {
		super(entityType, world, TurretMaterial.METAL, CannonballEntity.class);
		this.setShootSound(ModSoundEvents.TURRET_CANNON_SHOOT);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
	}

	// METHODS //
	// PRIVATE
	private <E extends IAnimatable> PlayState idlePredicate(final AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.setup", true));
		return PlayState.CONTINUE;
	}

	private <E extends IAnimatable> PlayState lookAtTargetPredicate(final AnimationEvent<E> event) {
		if (this.hasTarget()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.look_at_target", true));
			return PlayState.CONTINUE;
		}

		return this.idlePredicate(event);
	}

	private boolean animPlayed = false;
	private <E extends IAnimatable> PlayState deathPredicate(final AnimationEvent<E> event) {
		if (!this.isAlive() && !animPlayed) {
			animPlayed = true;
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.death", true));
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	private <E extends IAnimatable> PlayState firingSequencePredicate(final AnimationEvent<E> event) {
		Vec3d fusePos = this.getRelativePos(0, 0, -0.5);
		Vec3d barrelPos = this.getRelativePos(
			MathHelper.nextDouble(this.random, -0.5, 0.5),
			MathHelper.nextDouble(this.random, -0.75, 0.25),
			1
		);

		if (this.hasTarget() && this.isShooting()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.shoot"));
			event.getController().markNeedsReload();

			if (!this.getShootingFXDone()) {
				int count = MathHelper.nextInt(this.random, 10, 25);
				double vx = (this.getPos(TARGET_POS_X) - this.getPos(X)) / 10;
				double vy = (this.getPos(TARGET_POS_Y) - this.getPos(Y));
				double vz = (this.getPos(TARGET_POS_Z) - this.getPos(Z)) / 10;
				double variance = Math.sqrt(vx * vx + vz * vz) * 0.5;

				for (int i = 0; i < count; i++) {
					vx = vx == 0 ? MathHelper.nextDouble(this.random, -0.25, 0.25) : MathHelper.nextDouble(this.random, vx - 0.125, vx + 0.125);
					vy = MathHelper.nextDouble(this.random, vy, vy + 0.5);
					vz = vz == 0 ? MathHelper.nextDouble(this.random, -0.25, 0.25) : MathHelper.nextDouble(this.random, vz - 0.125, vz + 0.125);

					this.world.addParticle(
						ModParticles.CANNON_FLASH,
						true,
						barrelPos.x,
						barrelPos.y,
						barrelPos.z,
						vx * MathHelper.nextDouble(this.random, 1.5, 1.75),
						vy * variance * MathHelper.nextDouble(this.random, -0.5, 0.5),
						vz * MathHelper.nextDouble(this.random, 1.5, 1.75)
					);
				}
			}

		}

		if (this.getFuseLit()) {
			this.world.addParticle(
				ModParticles.CANNON_FUSE,
				true,
				fusePos.x,
				fusePos.y,
				fusePos.z,
				0,
				0,
				0
			);
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.fuse", true));
		}

		return PlayState.CONTINUE;
	}

	// PROTECTED
	@Override
	protected void initGoals() {
		// Goals
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 0, TOTAL_ATTACK_COOLDOWN, 16.625F));
		this.goalSelector.add(2, new LookAtEntityGoal(this, MobEntity.class, 8.0F, 0.02F, true));
		this.goalSelector.add(8, new LookAroundGoal(this));

		// Targets
		this.targetSelector.add(1, new ActiveTargetGoal<MobEntity>(this, MobEntity.class, 10, true, false, (entity) -> {
			return entity instanceof Monster;
		}));
		this.targetSelector.add(3, new TargetOtherTeamGoal(this));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(FUSE_LIT, false);
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(final DamageSource source) {
		return ModSoundEvents.TURRET_CANNON_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_CANNON_DESTROYED;
	}

	protected void setFuseLit(final boolean lit) {
		this.dataTracker.set(FUSE_LIT, lit);
	}

	protected boolean getFuseLit() {
		return this.dataTracker.get(FUSE_LIT);
	}

	// PUBLIC
	public static DefaultAttributeContainer.Builder setAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, Float.MAX_VALUE);
	}

	@Override
	public void registerControllers(final AnimationData data) {
		data.addAnimationController(new AnimationController<IAnimatable>(this, "idle", 20, this::idlePredicate));
		data.addAnimationController(new AnimationController<IAnimatable>(this, "look_at_target", 20, this::lookAtTargetPredicate));
		data.addAnimationController(new AnimationController<IAnimatable>(this, "death", 0, this::deathPredicate));
		data.addAnimationController(new AnimationController<IAnimatable>(this, "firing_sequence", 0, this::firingSequencePredicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}

	@Override
	public ItemStack getEntityItem() {
		ItemStack stack = new ItemStack(ModItems.CANNON_TURRET, 1);
		return stack;
	}

	@Override
	public void attack(final LivingEntity target, final float pullProgress) {
		if (target == null) {
			this.setShooting(false);
			return;
		}

		this.setShooting(true);

		try {
			double vx = (target.getX() - this.getX()) * 1.0625;
			double vy = target.getBodyY(2 / 3) - this.getY() + 0.25;
			double vz = (target.getZ() - this.getZ()) * 1.0625;
			double variance = Math.sqrt(vx * vx + vz * vz);
			float divergence = 0 + this.world.getDifficulty().getId() * 2;
			ProjectileEntity projectile = (ProjectileEntity) new CannonballEntity(ModEntities.CANNONBALL, this, vx, vy, vz, world);

			projectile.setVelocity(vx, vy + variance * 0.1f, vz, 1.5f, divergence);
			projectile.setPos(this.getX(), this.getY() + 0.5, this.getZ());

			this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
			this.world.spawnEntity(projectile);
		}
		catch (IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tick() {
		super.tick();

		this.setYaw(0);
		this.setBodyYaw(0);

		if (!this.world.isClient()) {
			// FIRING ANIMATING RELATED
			this.setTrackedYaw(this.getHeadYaw());
			this.setTrackedPitch(this.getPitch());
			this.setPos(X, this.getX());
			this.setPos(Y, this.getY() + 0.5);
			this.setPos(Z, this.getZ());

			if (this.attCooldown >= 99) {
				this.setShootingFXDone(false);
			}
			else {
				this.setShootingFXDone(true);
			}

			if (this.attCooldown < 20) {
				this.setFuseLit(false);
			}
			else if (this.attCooldown <= 80 && this.attCooldown >= 10) {
				this.setFuseLit(true);
			}

			if (this.hasTarget()) {
				this.setPos(TARGET_POS_X, this.getTarget().getX());
				this.setPos(TARGET_POS_Y, this.getTarget().getBodyY(1 / 2));
				this.setPos(TARGET_POS_Z, this.getTarget().getZ());

				--this.attCooldown;

				if (this.attCooldown <= 0) {
					this.attCooldown = 20 * 5;
				}
				else if (this.attCooldown <= 95) {
					this.setShooting(false);
				}
			}
			else {
				if (this.attCooldown != TOTAL_ATTACK_COOLDOWN) {
					this.setFuseLit(false);
					this.attCooldown = TOTAL_ATTACK_COOLDOWN;
				}
			}
		}
	}

	@Override
	public ActionResult interactMob(final PlayerEntity player, final Hand hand) {
		return Itemable.tryItem(player, hand, this, ModItems.TURRET_REMOVER, ModItems.CANNON_TURRET).orElse(super.interactMob(player, hand));
	}

	static {
		FUSE_LIT = DataTracker.registerData(CannonTurretEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

		healables = new HashMap<Item, Float>() {
			private static final long serialVersionUID = 1L;
			{
				for (Item item : TurretEntity.PLANKS) {
					put(item, 1f); }
				put(Items.IRON_NUGGET, 1f);
				put(Items.IRON_INGOT, 10f);
				put(Items.IRON_BLOCK, 100f);
			}
		};

		effectSource = new HashMap<Item, List<Object[]>>() {
			private static final long serialVersionUID = 1L;
			{
				put(Items.IRON_BLOCK, new ArrayList<Object[]>() {
					private static final long serialVersionUID = 1L;
					{
						add(new Object[] {StatusEffects.ABSORPTION, 60, 2});
					}
				});
			}
		};
	}
}
