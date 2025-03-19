package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.virus5600.defensive_measures.entity.ModEntities;

public class MGBulletEntity extends TurretProjectileEntity {
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	////////////////////
	/// CONSTRUCTORS ///
	////////////////////
	public MGBulletEntity(
		EntityType<? extends TurretProjectileEntity> entityType,
		World world
	) {
		super(entityType, world);
	}

	public MGBulletEntity(World world, LivingEntity owner) {
		this(ModEntities.MG_BULLET, world);
		this.setOwner(owner);
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

	///////////////
	/// METHODS ///
	///////////////
	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);

		builder.add(PIERCE_LEVEL, (byte) 5);
	}

	protected final void setPierceLevel(byte pierceLevel) {
		this.dataTracker.set(PIERCE_LEVEL, pierceLevel);
	}

	public byte getMaxPierceLevel() {
		return 5;
	}

	@Override
	protected ItemStack asItemStack() {
		return null;
	}

	////////////////////////////////
	/// INTERFACE IMPLEMENTATION ///
	////////////////////////////////

	// GeoEntity //
	@Override
	public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) { }

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}
}
