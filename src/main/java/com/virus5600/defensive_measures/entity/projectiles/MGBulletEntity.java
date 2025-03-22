package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
import com.virus5600.defensive_measures.util.BlockUtil;
import com.virus5600.defensive_measures.util.BlockUtil.BlockCategory;

import java.util.Map;

/**
 * The projectile used by {@link com.virus5600.defensive_measures.entity.turrets.MGTurretEntity MG Turret}.
 *
 * <p>Represents a machine gun bullet projectile entity that pierces through multiple unarmored entities.</p>
 * <p>
 *     The machine gun bullet is a {@link KineticProjectileEntity kinetic projectile}
 *     that is fired from a {@link com.virus5600.defensive_measures.entity.turrets.MGTurretEntity MG turret}.
 * </p>
 * <p>
 *     This projectile can pierce through multiple unarmored entities, dealing 5 hearts of damage to each
 *     entity it hits. The bullet can pierce through a maximum of 5 entities before it is destroyed.
 *     And everytime it pierces through an entity, it will reduce its pierce level its speed
 *     depending on the armor of the entity it hit.
 * </p>
 * <p>
 *     The bullet will be destroyed upon hitting a block like a true fast moving kinetic
 *     projectile would do.
 * </p>
 *
 * @see KineticProjectileEntity
 * @see TurretProjectileEntity
 * @see TurretProjectileEntity#onEntityHit(EntityHitResult)
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class MGBulletEntity extends KineticProjectileEntity {
	private static final Map<String, RawAnimation> ANIMATIONS;

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	// ////////////// //
	//  CONSTRUCTORS  //
	// ////////////// //
	public MGBulletEntity(
		EntityType<? extends TurretProjectileEntity> entityType,
		World world
	) {
		super(entityType, world);
	}

	public MGBulletEntity(World world, LivingEntity owner) {
		this(ModEntities.MG_BULLET, world);
		this.setOwner(owner);

		this.setPierceLevel((byte) 5);
		this.setDamage(5);
	}

	public MGBulletEntity(
		LivingEntity owner,
		double velocityX,
		double velocityY,
		double velocityZ,
		World world
	) {
		this(world, owner);
		this.setVelocity(velocityX, velocityY, velocityZ);
	}

	// ///////// //
	//  METHODS  //
	// ///////// //

	// PROTECTED
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);

		SoundEvent sound = ModSoundEvents.BULLET_IMPACT_FLESH;
		if (entityHitResult.getEntity() instanceof IronGolemEntity) {
			sound = ModSoundEvents.BULLET_IMPACT_METAL;
		}

		this.setSound(sound);
	}
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		this.setSound(ModSoundEvents.BULLET_IMPACT_DIRT);
		Block block = this.getWorld()
			.getBlockState(blockHitResult.getBlockPos())
			.getBlock();

		switch (BlockUtil.getBlockCategory(block)) {
			case BlockCategory.GLASS -> this.setSound(ModSoundEvents.BULLET_IMPACT_GLASS);
			case BlockCategory.GRAINY -> this.setSound(ModSoundEvents.BULLET_IMPACT_GRAINY);
			case BlockCategory.METAL -> this.setSound(ModSoundEvents.BULLET_IMPACT_METAL);
			case BlockCategory.STONE -> this.setSound(ModSoundEvents.BULLET_IMPACT_STONE);
			case BlockCategory.WOOD -> this.setSound(ModSoundEvents.BULLET_IMPACT_WOOD);
			// Default also includes BlockCategory.DIRT, BlockCategory.GREENERY, BlockCategory.OTHERS
			default -> this.setSound(ModSoundEvents.BULLET_IMPACT_DIRT);
		}

		this.discard();
	}

	@Override
	protected final void setPierceLevel(byte pierceLevel) {
		this.dataTracker.set(PIERCE_LEVEL, pierceLevel);
	}

	// PUBLIC
	public SoundEvent getHitSound() {
		return this.getSound();
	}

	public byte getMaxPierceLevel() {
		return 5;
	}

	// ///////////////////// //
	// ANIMATION CONTROLLERS //
	// ///////////////////// //

	private <E extends MGBulletEntity> PlayState idleController(final AnimationState<E> event) {
		return event.setAndContinue(ANIMATIONS.get("Idle"));
	}

	// ////////////////////////// //
	//  INTERFACE IMPLEMENTATION  //
	// ////////////////////////// //

	// GeoEntity //
	@Override
	public void registerControllers(final ControllerRegistrar controllers) {
		controllers.add(
			new AnimationController<>(this, "Idle", this::idleController)
		);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		ANIMATIONS = Map.of(
			"Idle", RawAnimation.begin()
				.thenPlay("animation.mg_bullet.idle")
		);
	}
}
