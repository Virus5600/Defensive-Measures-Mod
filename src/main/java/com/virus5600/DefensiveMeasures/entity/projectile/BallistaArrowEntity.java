package com.virus5600.DefensiveMeasures.entity.projectile;

import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent.SpawnEventC2SPacket;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BallistaArrowEntity extends PersistentProjectileEntity implements IAnimatable {
	private AnimationFactory factory = new AnimationFactory(this);

	/// CONSTRUCTORS ///
	public BallistaArrowEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super((EntityType<? extends PersistentProjectileEntity>)entityType, world);
        this.setPierceLevel((byte) 5);
    }

    public BallistaArrowEntity(World world, double x, double y, double z) {
        super(ModEntities.BALLISTA_ARROW, x, y, z, world);
        this.setPierceLevel((byte) 5);
    }

    public BallistaArrowEntity(World world, LivingEntity owner) {
        super(ModEntities.BALLISTA_ARROW, owner, world);
        this.setPierceLevel((byte) 5);
    }

	/// METHODS ///
    // PRIVATE
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ballista_arrow.idle"));
		return PlayState.CONTINUE;
	}

    // PROTECTED
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    protected void onHit(LivingEntity target) {
    	this.setPierceLevel((byte) 5);
        super.onHit(target);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    	this.setPierceLevel((byte) 5);
    	super.onEntityHit(entityHitResult);
    }

    // PUBLIC
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<IAnimatable>(this, "idle", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	protected ItemStack asItemStack() {
		return null;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return SpawnEventC2SPacket.send(this);
	}
}