package com.virus5600.DefensiveMeasures.entity.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.virus5600.DefensiveMeasures.entity.TurretMaterial;
import com.virus5600.DefensiveMeasures.entity.ai.goal.TargetOtherTeamGoal;
import com.virus5600.DefensiveMeasures.entity.projectile.BallistaArrowEntity;
import com.virus5600.DefensiveMeasures.item.ModItems;
import com.virus5600.DefensiveMeasures.sound.ModSoundEvents;
import com.virus5600.DefensiveMeasures.util.Vec3dUtil;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BallistaTurretEntity extends TurretEntity implements IAnimatable {
	private static final int totalAttCooldown = (int) (20 * 2.5);
	/**
	 * Contains all the items that can heal this entity.
	 */
	private static Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity
	 */
	private static Map<Item, List<Object[]>> effectSource;
	private AnimationFactory factory = new AnimationFactory(this);
	@Nullable
	private LivingEntity currentTarget = null;
	private double attCooldown = totalAttCooldown;

	// CONSTRUCTORS //
	public BallistaTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.WOOD, BallistaArrowEntity.class);
		this.setShootSound(ModSoundEvents.TURRET_BALLISTA_SHOOT);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
	}

	// METHODS //
	// PRIVATE
	private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ballista.setup", true));
		return PlayState.CONTINUE;
	}


	private <E extends IAnimatable> PlayState lookAtTargetPredicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ballista.look_at_target", true));
		return PlayState.CONTINUE;
	}

	private boolean animPlayed = false;
	private <E extends IAnimatable> PlayState deathPredicate(AnimationEvent<E> event) {
		if (!this.isAlive() && !animPlayed) {
			animPlayed = true;
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ballista.death", true));
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	private <E extends IAnimatable> PlayState firingSequencePredicate(AnimationEvent<E> event) {
		if (this.hasTarget() && this.isShooting()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ballista.shoot"));
			event.getController().markNeedsReload();
			this.setShooting(false);
		}

		return PlayState.CONTINUE;
	}

	// PROTECTED
	@Override
	protected void initGoals() {
		// Goals
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 0, totalAttCooldown, 16.8125F));
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

	// PUBLIC
	public static DefaultAttributeContainer.Builder setAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 25)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0f)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 999999f);
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<IAnimatable>(this, "idle", 20, this::idlePredicate));
		data.addAnimationController(new AnimationController<IAnimatable>(this, "look_at_target", 20, this::lookAtTargetPredicate));
		data.addAnimationController(new AnimationController<IAnimatable>(this, "death", 0, this::deathPredicate));
		data.addAnimationController(new AnimationController<IAnimatable>(this, "firing_sequence", 0, this::firingSequencePredicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	public ItemStack getEntityItem() {
		ItemStack stack = new ItemStack(ModItems.BALLISTA, 1);
		return stack;
	}

	@Override
	public void attack(final LivingEntity target, final float pullProgress) {
		if (this.isAlive()) {
			this.setShooting(true);

			if (target == null) {
				this.setShooting(false);
				return;
			}

			try {
				double vx = (target.getX() - this.getX()) * 1.0625;
				double vy = target.getBodyY(2 / 3) - this.getY() + 0.25;
				double vz = (target.getZ() - this.getZ()) * 1.0625;
				double variance = Math.sqrt(vx * vx + vz * vz);
				float divergence = 0 + this.world.getDifficulty().getId() * 2;
				ProjectileEntity projectile = (ProjectileEntity) new BallistaArrowEntity(world, this);

				projectile.setVelocity(vx, vy + variance * 0.2f, vz, 1.5f, divergence);
				projectile.setPos(this.getX(), this.getY() + 0.8125, this.getZ());

				this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
				this.world.spawnEntity(projectile);
			}
			catch (IllegalArgumentException | SecurityException e) {
				e.printStackTrace();
			}
		}
		else {
			this.setShooting(false);
			return;
		}

		try {
			double vx = (target.getX() - this.getX()) * 1.0625;
			double vy = target.getBodyY(2/3) - this.getY() + 0.25;
			double vz = (target.getZ() - this.getZ()) * 1.0625;
			double variance = Math.sqrt(vx * vx + vz * vz);
			float divergence = 0 + this.world.getDifficulty().getId() * 2;
			ProjectileEntity projectile = (ProjectileEntity) new BallistaArrowEntity(world, this);

			projectile.setVelocity(vx, vy + variance * 0.2f, vz, 1.5f, divergence);
			projectile.setPos(this.getX(), this.getY() + 0.8125, this.getZ());

			this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
			this.world.spawnEntity(projectile);
		} catch (IllegalArgumentException | SecurityException e) {
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

			if (this.hasTarget()) {
				this.setPos(TARGET_POS_X, this.getTarget().getX());
				this.setPos(TARGET_POS_Y, this.getTarget().getBodyY(1/2));
				this.setPos(TARGET_POS_Z, this.getTarget().getZ());

				--this.attCooldown;

				if (this.attCooldown <= 0)
					this.attCooldown = 20 * 2.5;
				else if (this.attCooldown <= 45)
					this.setShooting(false);
			}
			else {
				if (this.attCooldown != totalAttCooldown) {
					this.attCooldown = totalAttCooldown;
				}
			}
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		return Itemable.tryItem(player, hand, this, ModItems.TURRET_REMOVER, ModItems.BALLISTA).orElse(super.interactMob(player, hand));
	}

	static {
		healables = new HashMap<Item, Float>() {
			private static final long serialVersionUID = 1L;
			{
				put(Items.STICK, 1f);
				for (Item item : TurretEntity.PLANKS)
					put(item, 3f);
				put(Items.STRING, 1f);
				for (Item item : TurretEntity.LOGS)
					put(item, 25f);
			}
		};

		effectSource = new HashMap<Item, List<Object[]>>() {
			private static final long serialVersionUID = 1L;
			{
				for (Item item : TurretEntity.LOGS)
					put(item, new ArrayList<Object[]>() {
						private static final long serialVersionUID = 1L;
						{
							add(new Object[] {StatusEffects.ABSORPTION, 60, 2});
						}
					});
			}
		};
	}
}