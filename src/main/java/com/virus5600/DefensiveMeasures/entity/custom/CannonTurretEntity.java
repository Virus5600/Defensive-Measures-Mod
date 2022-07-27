package com.virus5600.DefensiveMeasures.entity.custom;

import org.jetbrains.annotations.Nullable;

import com.virus5600.DefensiveMeasures.entity.ai.goal.ShootProjectileGoal;
import com.virus5600.DefensiveMeasures.entity.ai.goal.TargetOtherTeamGoal;
import com.virus5600.DefensiveMeasures.item.ModItems;

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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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

public class CannonTurretEntity extends TurretEntity implements IAnimatable, RangedAttackMob, Itemable {
	
	private AnimationFactory factory = new AnimationFactory(this);
	
	public static DefaultAttributeContainer.Builder setAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0f)
			.add(EntityAttributes.GENERIC_ATTACK_SPEED, 5f)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0f)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 999999f);
	}

	// CONSTRUCTORS //
	public CannonTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}
	
	// METHODS //
	// PROTECTED
	@Override
	protected void initGoals() {
		// Goals
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 0, 100, 16.0F));
		this.goalSelector.add(2, new LookAtEntityGoal(this, HostileEntity.class, 8.0F, 0.02F, true));
		this.goalSelector.add(4, new ShootProjectileGoal(this, ArrowEntity.class));
		this.goalSelector.add(8, new LookAroundGoal(this));
		
		// Targets
		this.targetSelector.add(1, new ActiveTargetGoal<MobEntity>(this, MobEntity.class, 10, true, false, (entity) -> {
			return entity instanceof Monster;
		}));
		this.targetSelector.add(3, new TargetOtherTeamGoal(this));
	}
	
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SHULKER_HURT;
	}

	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHULKER_DEATH;
	}
	
	// PRIVATE
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.cannon_turret.setup", true));
		return PlayState.CONTINUE;
	}
	
	// PUBLIC
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<IAnimatable>(this, "controller", 0, this::predicate));
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
	}
	
	@Override
	public void tick() {
		super.tick();	
	}
	
	@Override
    public ActionResult interactMob(PlayerEntity player2, Hand hand) {
//		player2.getActiveItem().setDamage(player.getActiveItem().getDamage() + 1);
		player2.getActiveItem().damage(1, player2, player -> player.sendToolBreakStatus(hand));
		
		return Itemable.tryItem(player2, hand, this, ModItems.TURRET_REMOVER, ModItems.CANNON_TURRET).orElse(super.interactMob(player2, hand));
    }
}