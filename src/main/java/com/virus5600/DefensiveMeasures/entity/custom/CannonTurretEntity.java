package com.virus5600.DefensiveMeasures.entity.custom;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.TurretMaterial;
import com.virus5600.DefensiveMeasures.entity.ai.goal.TargetOtherTeamGoal;
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
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
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

@SuppressWarnings("serial")
public class CannonTurretEntity extends TurretEntity implements IAnimatable, RangedAttackMob, Itemable {
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
	private int attCooldown = 20*5;

	// CONSTRUCTORS //
	public CannonTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.METAL);
		this.setShootSound(ModSoundEvents.TURRET_CANNON_SHOOT);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
	}
	
	// METHODS //
	// PRIVATE
	private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.setup", true));
		return PlayState.CONTINUE;
	}
	
	private <E extends IAnimatable> PlayState lookAtTargetPredicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.look_at_target", true));
		return PlayState.CONTINUE;
	}
	
	private boolean animPlayed = false;
	private <E extends IAnimatable> PlayState deathPredicate(AnimationEvent<E> event) {
		if (!this.isAlive() && !animPlayed) {
			animPlayed = true;
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.death", true));
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}
	
	private <E extends IAnimatable> PlayState firingSequencePredicate(AnimationEvent<E> event) {
		if (this.hasTarget()) {
			if (this.isShooting()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.shoot"));
			}
			else {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.fuse", true));
			}
		}
		return PlayState.CONTINUE;
	}
	
	// PROTECTED
	@Override
	protected void initGoals() {
		// Goals
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 0, 100, 16.5F));
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
		return ModSoundEvents.TURRET_CANNON_HURT;
	}
	
	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_CANNON_DESTROYED;
	}
	
	// PUBLIC
	public static DefaultAttributeContainer.Builder setAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0f)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 999999f);
	}
	
	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<IAnimatable> firingSequenceController = new AnimationController<IAnimatable>(this, "firing_sequence", 0, this::firingSequencePredicate);
		
		data.addAnimationController(new AnimationController<IAnimatable>(this, "idle", 0, this::idlePredicate));
		data.addAnimationController(new AnimationController<IAnimatable>(this, "look_at_target", 0, this::lookAtTargetPredicate));
		data.addAnimationController(new AnimationController<IAnimatable>(this, "death", 0, this::deathPredicate));
		data.addAnimationController(firingSequenceController);
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
	public void attack(LivingEntity target, float pullProgress) {
		if (!this.isShooting()) {
			this.setShooting(true);
			this.attCooldown = 20*5;
		}
		else
			if (target == null)
				this.setShooting(false);
		
		try {
			ProjectileEntity projectile = (ProjectileEntity) this.projectile.getConstructors()[0].newInstance(world, this);
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			double vx = target.getX() - this.getX();
			double vy = target.getBodyY(1/2) - projectile.getY();
			double vz = target.getZ() - this.getZ();
			double variance = Math.sqrt(vx * vx + vz * vz);
			float divergence = 0 + this.world.getDifficulty().getId() * 2;
			
			projectile.setVelocity(vx, vy + variance * 0.2f, vz, 2.5f, divergence);
			projectile.setPos(projectile.getX(), this.getY() + 0.5, projectile.getZ());
			
			this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
			this.world.addParticle(ParticleTypes.FLAME, true, x, y, z, vx, vy, vz);
			this.world.spawnEntity(projectile);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void tick() {
		this.setYaw(0);
		this.setBodyYaw(0);
		
		if (!this.world.isClient() && this.hasTarget()) {
//			DefensiveMeasures.sendEmptyChat();
//			DefensiveMeasures.sendChat("[CTE.tick.attCooldown]: " + this.attCooldown);
			if (this.attCooldown <= 90 && this.attCooldown >= 5) {
//				DefensiveMeasures.sendChat("[CTE.tick.if]: " + this.attCooldown);
				this.world.addImportantParticle(ParticleTypes.FLAME, true, this.getX(), this.getY() + 1, this.getZ(), 0, 0, 0);
			}
			
			--this.attCooldown;
			
			if (this.attCooldown <= 0) {
				this.attCooldown = 20*5;
			}
			else if (this.attCooldown <= (20 * 0.52) && this.attCooldown > 0)
				this.setShooting(false);
		}
		super.tick();
	}
	
	@Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
		return Itemable.tryItem(player, hand, this, ModItems.TURRET_REMOVER, ModItems.CANNON_TURRET).orElse(super.interactMob(player, hand));
    }
	
	static {
		healables = new HashMap<Item, Float>() {
			private static final long serialVersionUID = 2495529354501122106L;
			{
				for (Item item : TurretEntity.PLANKS)
					put(item, 1f);
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