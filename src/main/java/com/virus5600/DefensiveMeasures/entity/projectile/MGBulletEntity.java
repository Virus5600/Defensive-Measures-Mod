package com.virus5600.DefensiveMeasures.entity.projectile;

import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent.SpawnEventC2SPacket;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MGBulletEntity extends PersistentProjectileEntity implements IAnimatable {
	private AnimationFactory factory = new AnimationFactory(this);

	/// CONSTRUCTORS ///
	public MGBulletEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super((EntityType<? extends PersistentProjectileEntity>)entityType, world);
        this.setPierceLevel((byte) 2);
        this.setDamage(5.0);
    }

    public MGBulletEntity(World world, double x, double y, double z) {
        super(ModEntities.MG_BULLET, x, y, z, world);
        this.setPierceLevel((byte) 2);
        this.setDamage(5.0);
    }

    public MGBulletEntity(World world, LivingEntity owner) {
        super(ModEntities.MG_BULLET, owner, world);
        this.setPierceLevel((byte) 2);
        this.setDamage(5.0);
    }

	/// METHODS ///
    // PRIVATE
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mg_bullet.idle"));
		return PlayState.CONTINUE;
	}

    // PROTECTED
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    protected void onHit(LivingEntity target) {
    	if (target.getType().getDimensions().width > 1.125) {
    		this.setPierceLevel((byte) 0);
    	}

        super.onHit(target);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    	if (entityHitResult.getEntity().getType().getDimensions().width > 1.125) {
    		this.setPierceLevel((byte) 0);
    	}

    	super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
    	super.onBlockHit(blockHitResult);

    	for (int i = 0; i < ((Math.random() * (10 - 5)) + 5); i++) {
	    	this.world.addParticle(
				new BlockStateParticleEffect(ParticleTypes.BLOCK, this.world.getBlockState(blockHitResult.getBlockPos())),
				true,
				this.getPos().getX(),
				this.getPos().getY(),
				this.getPos().getZ(),
				MathHelper.nextDouble(this.random, -0.01, 0.01),
				MathHelper.nextDouble(this.random, 0.1, 0.25),
				MathHelper.nextDouble(this.random, -0.01, 0.01)
			);
    	}

    	this.discard();
    }

    // PUBLIC
    @Override
    public void tick() {
        super.tick();
    }

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
