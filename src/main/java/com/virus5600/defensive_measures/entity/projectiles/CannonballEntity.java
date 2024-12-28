package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.virus5600.defensive_measures.entity.ModEntities;

public class CannonballEntity extends ExplosiveProjectileEntity implements GeoEntity {
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
	private LivingEntity shooter;

	////////////////////
	/// CONSTRUCTORS ///
	////////////////////
	public CannonballEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
		super(entityType, world);
		this.setFireTicks(0);
		this.setOnFire(false);
		this.setNoGravity(false);
	}

	public CannonballEntity(World world, LivingEntity owner) {
		this(ModEntities.CANNONBALL, world);
		this.setOwner(owner);
	}

	public CannonballEntity(LivingEntity owner, double directionX, double directionY, double directionZ, World world) {
		this(world, owner);
		this.setVelocity(directionX, directionY, directionZ);
	}

	///////////////
	/// METHODS ///
	///////////////
	// PROTECTED
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);

		if (!this.getWorld().isClient) {
			this.doDamage();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (!this.getWorld().isClient) {
			this.doDamage();
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

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) {
			this.doDamage();
		}
	}

	protected RegistryEntry.Reference<SoundEvent> getHitSound() {
		return SoundEvents.ENTITY_GENERIC_EXPLODE;
	}

	// PUBLIC
	private double radius = 0;
	public void doDamage() {
		// Create damage source
		DamageSource dmgSrc = this.getDamageSources().explosion(
			this,
			this.getOwner() == null ? this : this.getOwner()
		);

		// Create explosion
		this.getWorld()
			.createExplosion(
				this,
				dmgSrc,
				new ExplosionBehavior(),
				this.getX(),
				this.getBodyY(0.0625),
				this.getZ(),
				1.25F,
				false,
				World.ExplosionSourceType.NONE
			);

		// Damage entities within a 4 block radius
		double maxEffectiveDmgRad = 2;
		double maxDmgRad = 4;
		double dmgReduction = 3.75;
		double baseDmg = 10;

		for (radius = 0; radius < maxDmgRad; radius += 0.25) {
			if (radius < maxEffectiveDmgRad) radius += 0.75;

			double x1 = MathHelper.floor(this.getX() - radius - 1.0D);
			double x2 = MathHelper.floor(this.getX() + radius + 1.0D);
			double y1 = MathHelper.floor(this.getY() - radius - 1.0D);
			double y2 = MathHelper.floor(this.getY() + radius + 1.0D);
			double z1 = MathHelper.floor(this.getZ() - radius - 1.0D);
			double z3 = MathHelper.floor(this.getZ() + radius + 1.0D);

			this.getWorld()
				.getOtherEntities(
					this,
					new Box(x1, y1, z1, x2, y2, z3)
				)
				.forEach(entity -> {
					float dmg = (float) baseDmg;

					if (radius > maxEffectiveDmgRad) {
						dmg -= (float) (dmgReduction * (radius - maxEffectiveDmgRad));
					}

					if (entity instanceof LivingEntity) {
						entity.damage(
							(ServerWorld) this.getWorld(),
							dmgSrc,
							dmg
						);
					}
				});
		}

		this.discard();
	}

	@Override
	public void tick() {
		super.tick();

		double acceleration = 0.08;
		if (!this.hasNoGravity()) {
			this.setVelocity(
				this.getVelocity()
					.add(
						0,
						-acceleration / 4.0,
						0
					)
			);
		}
	}

	/////////////////////////////////
	/// INTERFACE IMPLEMENTATIONS ///
	/////////////////////////////////

	// GeoEntity //
	@Override
	public void registerControllers(final ControllerRegistrar controllers) {
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}
}
