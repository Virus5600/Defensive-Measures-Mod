package com.virus5600.DefensiveMeasures.entity.projectile;

import com.virus5600.DefensiveMeasures.DefensiveMeasuresClient;
import com.virus5600.DefensiveMeasures.entity.ModEntities;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
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

public class BallistaArrowEntity extends PersistentProjectileEntity implements IAnimatable {
	private AnimationFactory factory = new AnimationFactory(this);
	private double damage = 2.0;
	
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
        super.onHit(target);
    }
    
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        DamageSource damageSource;
        Entity entity2;
        
        Entity entity = entityHitResult.getEntity();
        float f = (float)this.getVelocity().length();
        int i = MathHelper.ceil(MathHelper.clamp((double)f * this.damage, 0.0, 2.147483647E9));
        if (this.isCritical()) {
            long l = this.random.nextInt(i / 2 + 2);
            i = (int)Math.min(l + (long)i, Integer.MAX_VALUE);
        }
        if ((entity2 = this.getOwner()) == null) {
            damageSource = DamageSource.arrow(this, this);
        } else {
            damageSource = DamageSource.arrow(this, entity2);
            if (entity2 instanceof LivingEntity) {
                ((LivingEntity)entity2).onAttacking(entity);
            }
        }
        boolean bl = entity.getType() == EntityType.ENDERMAN;
        int j = entity.getFireTicks();
        if (this.isOnFire() && !bl) {
            entity.setOnFireFor(5);
        }
        if (entity.damage(damageSource, i)) {
            if (bl) {
                return;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (!this.world.isClient && this.getPierceLevel() <= 0) {
                    livingEntity.setStuckArrowCount(livingEntity.getStuckArrowCount() + 1);
                }
                if (!this.world.isClient && entity2 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity, entity2);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
                }
                this.onHit(livingEntity);
                if (entity2 != null && livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                }
            }
            this.playSound(this.getHitSound(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
        } else {
            entity.setFireTicks(j);
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.setYaw(this.getYaw() + 180.0f);
            this.prevYaw += 180.0f;
            if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1f);
                }
                this.discard();
            }
        }
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
		return DefensiveMeasuresClient.EntityPacket.createPacket(this);
	}
}