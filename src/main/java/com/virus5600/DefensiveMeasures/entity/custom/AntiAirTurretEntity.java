package com.virus5600.DefensiveMeasures.entity.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.virus5600.DefensiveMeasures.entity.TurretMaterial;
import com.virus5600.DefensiveMeasures.entity.ai.goal.PrioritizeAirTargetGoal;
import com.virus5600.DefensiveMeasures.entity.ai.goal.TargetOtherTeamGoal;
import com.virus5600.DefensiveMeasures.entity.projectile.AntiAirProjectileEntity;
import com.virus5600.DefensiveMeasures.item.ModItems;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class AntiAirTurretEntity extends TurretEntity implements IAnimatable, RangedAttackMob, Itemable {
	/**
	 * Identifies how long the entity should wait before firing once more.
	 * The formula use in this is "<code>T * S</code>", whereas <code>T</code>
	 * represents the ticks (constant at 20) and <code>S</code> as real-time seconds.
	 */
	private static final int TOTAL_ATTACK_COOLDOWN = (int) (20 * 2.5);
	/**
	 * Contains all the items that can heal this entity.
	 */
	private static Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity.
	 */
	private static Map<Item, List<Object[]>> effectSource;
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);
	@Nullable
	private LivingEntity currentTarget = null;
	private Vec3d barrelPos = getRelativePos(0, -.175, .5);
	private double attCooldown = TOTAL_ATTACK_COOLDOWN;
	private int projectileShootCooldown = 1;
	private int getProjectilesFired = 0;
	private boolean playAnimation = true;
	private static final TrackedData<Boolean> SHOULD_SKIP_ATTACK;

	// CONSTRUCTORS //
	public AntiAirTurretEntity(final EntityType<? extends GolemEntity> entityType, final World world) {
		super(entityType, world, TurretMaterial.METAL, AntiAirTurretEntity.class);
		this.setShootSound(ModSoundEvents.TURRET_ANTI_AIR_SHOOT);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
	}

	// TODO: Continue fixing the AA Turret

	// METHODS //
	// PRIVATE
	private <E extends IAnimatable> PlayState idlePredicate(final AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.anti_air_turret.setup", EDefaultLoopTypes.LOOP));
		return PlayState.CONTINUE;
	}

	private <E extends IAnimatable> PlayState lookAtTargetPredicate(final AnimationEvent<E> event) {
		if (this.hasTarget()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.anti_air_turret.look_at_target", EDefaultLoopTypes.LOOP));
			return PlayState.CONTINUE;
		}

		return this.idlePredicate(event);
	}

	private boolean animPlayed = false;
	private <E extends IAnimatable> PlayState deathPredicate(final AnimationEvent<E> event) {
		if (!this.isAlive() && !animPlayed) {
			animPlayed = true;
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.anti_air_turret.death"));
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	private <E extends IAnimatable> PlayState firingSequencePredicate(final AnimationEvent<E> event) {
		if (this.hasTarget() && this.isShooting()) {
			if (this.playAnimation) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.anti_air_turret.shoot"));
				event.getController().markNeedsReload();
				this.playAnimation = false;
			}

			if (!this.getShouldSkipAtt()) {
				this.barrelPos = this.getRelativePos(0, -.175, .5);
				this.world.addParticle(
					ParticleTypes.LAVA,
					true,
					this.barrelPos.x,
					this.barrelPos.y,
					this.barrelPos.z,
					MathHelper.nextDouble(this.random, -0.0001, 0.0001),
					MathHelper.nextDouble(this.random, -0.0001, 0.0001),
					MathHelper.nextDouble(this.random, -0.0001, 0.0001)
				);
			}
		}
		else {
			this.playAnimation = true;
		}

		return PlayState.CONTINUE;
	}

	// PROTECTED
	@Override
	protected void initGoals() {
		// Goals
		this.goalSelector.add(1, new PrioritizeAirTargetGoal(this, 0, TOTAL_ATTACK_COOLDOWN, 64.8125f));
		this.goalSelector.add(2, new ProjectileAttackGoal(this, 0, TOTAL_ATTACK_COOLDOWN, 64.8125f));
		this.goalSelector.add(3, new LookAtEntityGoal(this, MobEntity.class, 8.0F, 0.02F, true));
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
		this.dataTracker.startTracking(SHOULD_SKIP_ATTACK, false);
	}

	protected void setShouldSkipAtt(final boolean decision) {
		this.dataTracker.set(SHOULD_SKIP_ATTACK, decision);
	}

	protected boolean getShouldSkipAtt() {
		return this.dataTracker.get(SHOULD_SKIP_ATTACK);
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(final DamageSource source) {
		return ModSoundEvents.TURRET_MG_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_MG_DESTROYED;
	}

	// PUBLIC
	public static DefaultAttributeContainer.Builder setAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 25)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0f)
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
		return this.factory;
	}

	public ItemStack getEntityItem() {
		ItemStack stack = new ItemStack(ModItems.MG_TURRET, 1);
		return stack;
	}

	@Override
	public void attack(final LivingEntity target, final float pullProgress) {
		if (target == null) {
			this.setShooting(false);
			return;
		}

		this.setShooting(true);
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
				this.setPos(TARGET_POS_Y, this.getTarget().getBodyY(1 / 2));
				this.setPos(TARGET_POS_Z, this.getTarget().getZ());

				--this.attCooldown;

				if (this.attCooldown <= 0) this.attCooldown = 20 * 2.5;

				try {
					if (this.getProjectilesFired < 10 && this.isShooting()) {
						if (!this.getShouldSkipAtt() && this.projectileShootCooldown == 0) {
							double vx = (this.getTarget().getX() - this.getX()) * 1.0625;
							double vy = this.getTarget().getBodyY(2 / 3) - this.getY() + 0.25;
							double vz = (this.getTarget().getZ() - this.getZ()) * 1.0625;

							double variance = Math.sqrt(vx * vx + vz * vz);
							float divergence = 0 + this.world.getDifficulty().getId() * 2;

							ProjectileEntity projectile = (ProjectileEntity) new AntiAirProjectileEntity(world, this);
							this.barrelPos = this.getRelativePos(0, -.175, .5);

							projectile.setVelocity(vx, vy + variance * 0.2f, vz, 1.5f, divergence + 0.25f);
							projectile.setPos(this.barrelPos.x, this.barrelPos.y, this.barrelPos.z);
							this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));

							this.world.spawnEntity(projectile);
							this.getProjectilesFired++;
							this.projectileShootCooldown = 1;
							this.setShouldSkipAtt(true);
						}
						else {
							this.projectileShootCooldown--;

							if (this.projectileShootCooldown == 0) this.setShouldSkipAtt(false);
						}
					}
					else {
						this.getProjectilesFired = 0;
						this.setShooting(false);
					}
				}
				catch (IllegalArgumentException | SecurityException e) {
					e.printStackTrace();
				}
			}
			else {
				if (this.attCooldown != TOTAL_ATTACK_COOLDOWN) {
					this.attCooldown = TOTAL_ATTACK_COOLDOWN;
				}
			}
		}
	}

	@Override
	public ActionResult interactMob(final PlayerEntity player, final Hand hand) {
		return Itemable.tryItem(player, hand, this, ModItems.TURRET_REMOVER, ModItems.MG_TURRET).orElse(super.interactMob(player, hand));
	}

	static {
		SHOULD_SKIP_ATTACK = DataTracker.registerData(AntiAirTurretEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

		healables = new HashMap<Item, Float>() {
			private static final long serialVersionUID = 1L;
			{
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
