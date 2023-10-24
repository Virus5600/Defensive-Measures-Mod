package com.virus5600.DefensiveMeasures.entity.projectile;

import java.util.List;

import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent.SpawnEventC2SPacket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AntiAirProjectileEntity extends CannonballEntity implements IAnimatable {
	private LivingEntity shooter;
	private Entity hitEntity;
	private AnimationFactory factory = new AnimationFactory(this);

	/// CONSTRUCTORS ///
	public AntiAirProjectileEntity(final EntityType<? extends CannonballEntity> entityType, final World world) {
        super((EntityType<? extends CannonballEntity>) entityType, world);
    }

	public AntiAirProjectileEntity(final World world, final LivingEntity owner) {
		this(ModEntities.ANTI_AIR_PROJECTILE, world);
	}

	public AntiAirProjectileEntity(final EntityType<? extends CannonballEntity> type, final LivingEntity owner, final double directionX, final double directionY, final double directionZ, final World world) {
		super(type, owner, directionX, directionY, directionZ, world);
	}

	/// METHODS ///
	// PRIVATE
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(
			new AnimationBuilder().addAnimation("animation.anti_air_projectile.move")
		);

		return PlayState.CONTINUE;
	}

	// PROTECTED
	@Environment(EnvType.CLIENT)
	@Override
	protected ParticleEffect getParticleParameters() {
		return ParticleTypes.FLAME;
	}

    @Override
    protected void onBlockHit(final BlockHitResult blockHitResult) {
    	super.onBlockHit(blockHitResult);

    	if (!this.world.isClient()) {
    		this.doDamage();
    		this.discard();
    	}

    	this.setSound(this.getHitSound());
    }

    @Override
    protected void onEntityHit(final EntityHitResult entityHitResult) {
    	if (!this.world.isClient) {
    		this.hitEntity = entityHitResult.getEntity();
			this.doDamage();
			this.discard();
		}
    }

    @Override
    protected void onCollision(final HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte) 3);
			this.doDamage();
			this.discard();
		}
	}

    // PUBLIC
    @Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(
			new AnimationController<IAnimatable>(this, "move", 0, this::predicate)
		);
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

    @Override
    @Environment(EnvType.CLIENT)
	public void handleStatus(final byte status) {
		if (status == 3) {
			ParticleEffect particleEffect = this.getParticleParameters();

			for (int i = 0; i < 8; ++i) {
				this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

    @Override
    public void doDamage() {
    	float q = 1.5F;
		int xp = MathHelper.floor(this.getX() - (double) q - 1.0D);
		int xm = MathHelper.floor(this.getX() + (double) q + 1.0D);
		int yp = MathHelper.floor(this.getY() - (double) q - 1.0D);
		int ym = MathHelper.floor(this.getY() + (double) q + 1.0D);
		int zp = MathHelper.floor(this.getZ() - (double) q - 1.0D);
		int zm = MathHelper.floor(this.getZ() + (double) q + 1.0D);

		Explosion explosion = this.world.createExplosion(
			this,
			this.getX(),
			this.getBodyY(0.0625),
			this.getZ(),
			0F,
			false,
			Explosion.DestructionType.NONE
		);

		// If this projectile is alive for more equal or more than a second...
		int age = this.age;
		if (age <= 20) {
			// ...get all the entities in a 1.5 block radius...
			List<Entity> list = this.world.getOtherEntities(
				this,
				new Box((double) xp, (double) yp, (double) zp, (double) xm, (double) ym, (double) zm)
			);

			// ...then damage them all
			for (int x = 0; x < list.size(); ++x) {
				Entity entity = (Entity) list.get(x);

				if (entity instanceof LivingEntity) {
					if (this.shooter == null)
						entity.damage(DamageSource.explosion(explosion), 15);
					else
						entity.damage(DamageSource.player((PlayerEntity) this.shooter), 15);
				}
			}
		}
		// Otherwise, just damage the entity it hits.
		else {
			if (this.hitEntity != null)
				this.hitEntity.damage(DamageSource.player((PlayerEntity) this.shooter), 15);
		}

		this.discard();
    }

    @Override
    public void tick() {
    	super.tick();

    	// If this projectile is still alive after 5 seconds, make it explode.
    	if (this.isAlive()) {
    		if (this.age >= 100) {
    			this.doDamage();
    		}
    	}
    }

    public Packet<?> createSpawnPacket() {
		return SpawnEventC2SPacket.send(this);
	}
}
