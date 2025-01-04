package com.virus5600.defensive_measures.entity.projectiles;

import com.virus5600.defensive_measures.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.virus5600.defensive_measures.entity.ModEntities;

import java.util.Map;

public class BallistaArrowEntity extends PersistentProjectileEntity implements GeoEntity {
	private static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(BallistaArrowEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final Map<String, RawAnimation> ANIMATIONS;

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	////////////////////
	/// CONSTRUCTORS ///
	////////////////////
	public BallistaArrowEntity(
		EntityType<? extends PersistentProjectileEntity> entityType,
		World world
	) {
		super(entityType, world);

	}

	public BallistaArrowEntity(World world, LivingEntity owner) {
		this(ModEntities.BALLISTA_ARROW, world);
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

		builder.add(PIERCE_LEVEL, (byte) 5);
	}

	protected void setPierceLevel(byte pierceLevel) {
		this.dataTracker.set(PIERCE_LEVEL, pierceLevel);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);

		// Reduces the speed of the arrow when it hits an entity
		Entity entity = entityHitResult.getEntity();
		// For reference, max armor points is 30
		int armor = entity instanceof LivingEntity livingEntity ? livingEntity.getArmor() : 0;
		boolean hasHeavyArmor = armor > 15,
			hasLightArmor = armor <= 15 && armor > 0;

		if (entity instanceof LivingEntity livingEntity) {
			double targetH = livingEntity.getHeight(),
				arrowH = this.getHeight(),
				variance = arrowH * 0.125; // 12.5% of the arrow's height

			double arrowMin = arrowH - variance,
				arrowMax = arrowH + variance,
				reducedVelocity = 0.125;

			// Reduce velocity and pierce level based on the side of the entity hit
			if (this.getPierceLevel() > 0) {
				// If the arrow is smaller than the target or has heavy armor, reduce the pierce level by 2 and velocity by 50%
				if (targetH > arrowMax || hasHeavyArmor) {
					this.setPierceLevel((byte) (this.getPierceLevel() - 2));
					reducedVelocity = 0.5;
				}
				// If the arrow is almost the same size as the target or has a light armor, reduce the pierce level by 1 and velocity by 25%
				else if ((targetH < arrowMax && arrowMin < targetH) || hasLightArmor) {
					this.setPierceLevel((byte) (this.getPierceLevel() - 1));
					reducedVelocity = 0.25;
				}
				// Otherwise, just reduce the velocity by 12.5% without reducing the pierce level
			}

			// Apply reduced velocity
			this.addVelocity(
				this.getVelocity()
					.multiply(reducedVelocity)
					.negate()
			);
		}

		this.addVelocity(
			this.getVelocity()
				.negate()
				.multiply(0.1)
		);
	}

	@Override
	protected ItemStack asItemStack() {
		return null;
	}

	@Override
	public byte getPierceLevel() {
		return this.dataTracker.get(PIERCE_LEVEL);
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

	// PersistentProjectileEntity //
	@Override
	protected ItemStack getDefaultItemStack() {
		return new ItemStack(Items.ARROW);
	}

	// GeoEntity //
	@Override
	public void registerControllers(final ControllerRegistrar controllers) {
		controllers.add(
			new AnimationController<>(this, "Idle", this::idleController),
			new AnimationController<>(this, "OnAir", this::onAirController)
		);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
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
