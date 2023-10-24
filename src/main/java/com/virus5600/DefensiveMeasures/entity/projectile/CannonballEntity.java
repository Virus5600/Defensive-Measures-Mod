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
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class CannonballEntity extends ExplosiveProjectileEntity implements IAnimatable {
	private LivingEntity shooter;
	private AnimationFactory factory = new AnimationFactory(this);

	public SoundEvent hitSound = this.getHitSound();

	/// CONSTRUCTORS ///
	public CannonballEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
		super((EntityType<? extends ExplosiveProjectileEntity>)entityType, world);
		this.setFireTicks(0);
		this.setOnFire(false);
		this.setNoGravity(false);
		super.setNoGravity(false);
	}

	public CannonballEntity(World world, LivingEntity owner) {
        this(ModEntities.CANNONBALL, world);
        this.setOwner(owner);
        this.setOnFire(false);
        this.setNoGravity(false);
        super.setNoGravity(false);
    }

	public CannonballEntity(EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, double directionX, double directionY, double directionZ, World world) {
		super(type, owner, directionX, directionY, directionZ, world);
		this.setOnFire(false);
        this.setNoGravity(false);
        super.setNoGravity(false);
	}

	/// METHODS ///
	// PROTECTED
	@Environment(EnvType.CLIENT)
	protected ParticleEffect getParticleParameters() {
		return ParticleTypes.CLOUD;
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		if (!this.world.isClient) {
			this.doDamage();
			this.world.createExplosion(
				this,
				this.getX(),
				this.getBodyY(0.0625),
				this.getZ(),
				1.25F,
				false,
				Explosion.DestructionType.NONE
			);
			this.remove(Entity.RemovalReason.DISCARDED);
		}
		this.setSound(SoundEvents.ENTITY_GENERIC_EXPLODE);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (!this.world.isClient) {
			this.doDamage();
			this.world.createExplosion(
				this,
				this.getX(),
				this.getBodyY(0.0625),
				this.getZ(),
				1.25F,
				false,
				Explosion.DestructionType.NONE
			);
			this.remove(Entity.RemovalReason.DISCARDED);
		}
	}

	@Override
	protected boolean isBurning() {
        return false;
    }

	@Override
	protected ParticleEffect getParticleType() {
        return ParticleTypes.CLOUD;
    }

	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte)3);
			this.doDamage();
			this.world.createExplosion(
				this,
				this.getX(),
				this.getBodyY(0.0625),
				this.getZ(),
				1.25F,
				false,
				Explosion.DestructionType.NONE
			);
			this.remove(Entity.RemovalReason.DISCARDED);
		}
	}

	protected SoundEvent getHitSound() {
		return SoundEvents.ENTITY_GENERIC_EXPLODE;
	}

	// PUBLIC
	@Environment(EnvType.CLIENT)
	public void handleStatus(byte status) {
		if (status == 3) {
			ParticleEffect particleEffect = this.getParticleParameters();

			for(int i = 0; i < 8; ++i) {
				this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	public void doDamage() {
		float q = 4.0F;
		int k = MathHelper.floor(this.getX() - (double) q - 1.0D);
		int l = MathHelper.floor(this.getX() + (double) q + 1.0D);
		int t = MathHelper.floor(this.getY() - (double) q - 1.0D);
		int u = MathHelper.floor(this.getY() + (double) q + 1.0D);
		int v = MathHelper.floor(this.getZ() - (double) q - 1.0D);
		int w = MathHelper.floor(this.getZ() + (double) q + 1.0D);
		Vec3d vec3d = new Vec3d(this.getX(), this.getY(), this.getZ());
		List<Entity> list = this.world.getOtherEntities(
			this,
			new Box((double) k, (double) t, (double) v, (double) l, (double) u, (double) w)
		);

		for (int x = 0; x < list.size(); ++x) {
			Entity entity = (Entity) list.get(x);
			double y = (double) (MathHelper.sqrt((float) entity.squaredDistanceTo(vec3d)) / q);

			if (y <= 1.0D) {
				Explosion explosion = this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625D), this.getZ(), 2.5F, Explosion.DestructionType.NONE);
				if (entity instanceof LivingEntity) {
					if (this.shooter == null)
						entity.damage(DamageSource.explosion(explosion), 15);
					else
						entity.damage(DamageSource.player((PlayerEntity) this.shooter), 15);
				}
			}
		}
	}

	public void setSound(SoundEvent soundIn) {
		this.hitSound = soundIn;
	}

	@Override
	public void registerControllers(AnimationData data) {
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public void tick() {
		super.tick();

		double d = 0.08;
		if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
        }
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return SpawnEventC2SPacket.send(this);
	}
}
