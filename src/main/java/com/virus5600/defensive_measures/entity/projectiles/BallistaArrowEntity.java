package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.world.World;

import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import com.virus5600.defensive_measures.entity.ModEntities;

import java.util.Map;

/**
 * BallistaArrowEntity
 *
 * <p>Represents a ballista arrow projectile entity that pierces through multiple entities.</p>
 * <p>
 *     The ballista arrow is a {@link KineticProjectileEntity kinetic projectile}
 *     that is fired from a {@link com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity ballista turret}.
 * </p>
 * <p>
 *     This projectile can pierce through multiple entities, dealing 4 hearts of damage to each
 *     entity it hits. The arrow can pierce through a maximum of 5 entities before it is destroyed.
 *     And everytime it pierces through an entity, it will reduce its pierce level its speed
 *     depending on the armor of the entity it hit.
 * </p>
 * To see how the reduction mechanics work, check {@link KineticProjectileEntity}.
 */
public class BallistaArrowEntity extends KineticProjectileEntity {
	private static final Map<String, RawAnimation> ANIMATIONS;

	// ////////////// //
	//  CONSTRUCTORS  //
	// ////////////// //
	public BallistaArrowEntity(
		EntityType<? extends TurretProjectileEntity> entityType,
		World world
	) {
		super(entityType, world);

		this.setDamage(4);
	}

	public BallistaArrowEntity(World world, LivingEntity owner) {
		this(ModEntities.BALLISTA_ARROW, world);

		this.setPierceLevel(this.getMaxPierceLevel());
		this.setOwner(owner);
	}

	public BallistaArrowEntity(
		LivingEntity owner,
		double directionX,
		double directionY,
		double directionZ,
		World world
	) {
		this(world, owner);
		this.setVelocity(directionX, directionY, directionZ);
	}

	///////////////
	/// METHODS ///
	///////////////
	// PROTECTED
	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);
	}

	@Override
	public byte getMaxPierceLevel() {
		return 5;
	}

	///////////////////////////
	// ANIMATION CONTROLLERS //
	///////////////////////////

	private <E extends BallistaArrowEntity>PlayState idleController(final AnimationState<E> event) {
		return event.setAndContinue(ANIMATIONS.get("Idle"));
	}

	private <E extends BallistaArrowEntity>PlayState onAirController(final AnimationState<E> event) {
		if (this.isInGround() || this.isOnGround()) {
			return PlayState.STOP;
		}

		return event.setAndContinue(ANIMATIONS.get("OnAir"));
	}

	/////////////////////////////////
	/// INTERFACE IMPLEMENTATIONS ///
	/////////////////////////////////

	// GeoEntity //
	@Override
	public void registerControllers(final ControllerRegistrar controllers) {
		controllers.add(
			new AnimationController<>(this, "Idle", this::idleController),
			new AnimationController<>(this, "OnAir", this::onAirController)
		);
	}

	///////////////////////
	// STATIC INITIALIZE //
	///////////////////////

	static {
		ANIMATIONS = Map.of(
			"Idle", RawAnimation.begin()
				.thenPlay("animation.ballista_arrow.idle"),
			"OnAir", RawAnimation.begin()
				.thenPlay("animation.ballista_arrow.move")
		);
	}
}
