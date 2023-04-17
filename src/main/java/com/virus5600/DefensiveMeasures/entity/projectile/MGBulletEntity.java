package com.virus5600.DefensiveMeasures.entity.projectile;

import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent.SpawnEventC2SPacket;
import com.virus5600.DefensiveMeasures.sound.ModSoundEvents;

import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
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
import software.bernie.geckolib3.util.GeckoLibUtil;

public class MGBulletEntity extends PersistentProjectileEntity implements IAnimatable {
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	/// CONSTRUCTORS ///
	public MGBulletEntity(final EntityType<? extends PersistentProjectileEntity> entityType, final World world) {
        super((EntityType<? extends PersistentProjectileEntity>) entityType, world);
        this.setDamage(5.0);
        this.setSound(ModSoundEvents.BULLET_IMPACT_DIRT);
    }

    public MGBulletEntity(final World world, final double x, final double y, final double z) {
        super(ModEntities.MG_BULLET, x, y, z, world);
        this.setDamage(5.0);
        this.setSound(ModSoundEvents.BULLET_IMPACT_DIRT);
    }

    public MGBulletEntity(final World world, final LivingEntity owner) {
        super(ModEntities.MG_BULLET, owner, world);
        this.setDamage(5.0);
        this.setSound(ModSoundEvents.BULLET_IMPACT_DIRT);
    }

	/// METHODS ///
    // PRIVATE
    private <E extends IAnimatable> PlayState predicate(final AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.mg_bullet.idle"));
		return PlayState.CONTINUE;
	}

    // PROTECTED
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    protected void onEntityHit(final EntityHitResult entityHitResult) {
    	if (entityHitResult.getEntity().getType().getDimensions().width > 1.125) {
    		this.setSound(ModSoundEvents.BULLET_IMPACT_DIRT);
    	}

    	super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockHit(final BlockHitResult blockHitResult) {
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

    	// Identifies what block was hit
    	SoundEvent soundToPlay;
    	Material mat = world.getBlockState(blockHitResult.getBlockPos()).getMaterial();

    	if (mat.equals(Material.METAL)) {
    		soundToPlay = ModSoundEvents.BULLET_IMPACT_METAL;
    	}
    	else if (mat.equals(Material.STONE)) {
    		soundToPlay = ModSoundEvents.BULLET_IMPACT_STONE;
    	}
    	else if (mat.equals(Material.WOOD)) {
    		soundToPlay = ModSoundEvents.BULLET_IMPACT_WOOD;
    	}
    	else {
    		soundToPlay = ModSoundEvents.BULLET_IMPACT_DIRT;
    	}

    	this.setSound(soundToPlay);
    }

    // PUBLIC
    @Override
    public void tick() {
        super.tick();
    }

	@Override
	public void registerControllers(final AnimationData data) {
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
