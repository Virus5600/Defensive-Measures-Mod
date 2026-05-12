package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.entity.ModEntities;

/**
 * The projectile used by {@link com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity Cannon Turret}.
 *
 * <p>Represents a cannonball projectile entity that explodes on impact.</p>
 * <p>
 *     The cannonball is an {@link ExplosiveProjectileEntity explosive projectile}
 *     that is fired from a {@link com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity cannon turret}.
 * </p>
 * <p>
 *     This projectile is set to explode upon impact, causing 10 hearts of damage to entities
 *     within a 2.5 block radius, which is also known as effective range. Within the radius of 2.5
 *     blocks and 5 blocks, entities will receive a reduced amount of damage based on the distance
 *     from the center of the explosion.
 * </p>
 * <p>The damage dealt every 0.25 blocks after the 2.5 is as follows:</p>
 * <ul>
 *     <li><b>2.50 blocks:</b> 10.0 HP</li>
 *     <li><b>2.75 blocks:</b> ~8.88 HP</li>
 *     <li><b>3.00 blocks:</b> ~7.80 HP</li>
 *     <li><b>3.25 blocks:</b> ~6.73 HP</li>
 *     <li><b>3.50 blocks:</b> ~5.70 HP</li>
 *     <li><b>3.75 blocks:</b> ~4.69 HP</li>
 *     <li><b>4.00 blocks:</b> ~3.70 HP</li>
 *     <li><b>4.25 blocks:</b> ~2.74 HP</li>
 *     <li><b>4.50 blocks:</b> ~1.81 HP</li>
 *     <li><b>4.75 blocks:</b> ~0.89 HP</li>
 *     <li><b>5.00 blocks:</b> 0.0 HP</li>
 * </ul>
 * <hr>
 * <p>To get the same values as the ones above, the following formula was used:</p>
 * <pre><code>
 * normalizedRadius = (radius - effectiveRadius) / (maxDmgRadius - effectiveRadius)
 * formula = (e^(-dmgReduction * normalizedRadius) - e^(-dmgReduction)) / (1 - e^(-dmgReduction))
 * dmg = baseDmg * formula
 * </code></pre>
 *
 * <p>And if you want to test it, you can use the Desmos graph created by me:</p>
 * <a href="https://www.desmos.com/calculator/pdm27kw9oe">Reduction Damage Graph</a>
 *
 * @see ExplosiveProjectileEntity
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class CannonballEntity extends ExplosiveProjectileEntity {
	// ////////////// //
	//  CONSTRUCTORS  //
	// ////////////// //
	public CannonballEntity(
		EntityType<? extends ExplosiveProjectileEntity> entityType,
		World world
	) {
		super(entityType, world);
		this.setFireTicks(0);
		this.setOnFire(false);
		this.setNoGravity(false);

		this.setDamage(10);
	}

	public CannonballEntity(LivingEntity owner, World world) {
		this(ModEntities.CANNONBALL, world);
		this.setOwner(owner);
	}

	public CannonballEntity(
		LivingEntity owner,
		double directionX,
		double directionY,
		double directionZ,
		World world
	) {
		this(owner, world);
		this.setVelocity(directionX, directionY, directionZ);
	}

	// ///////// //
	//  METHODS  //
	// ///////// //

	// PROTECTED
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);

		if (!this.getEntityWorld().isClient()) {
			this.doDamage();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (!this.getEntityWorld().isClient()) {
			this.doDamage();
		}
	}

	@Override
	protected boolean isBurning() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @return The particle type for the cannonball's trail, which is a cloud particle.
	 */
	@Override
	protected ParticleEffect getTrailParticleType() {
		return ParticleTypes.CLOUD;
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (!this.getEntityWorld().isClient()) {
			this.doDamage();
		}

		super.onCollision(hitResult);
	}

	@Override
	protected double getGravity() {
		return 0.0625;
	}

	// PUBLIC
	@Override
	public SoundEvent getHitSound() {
		return SoundEvents.ENTITY_GENERIC_EXPLODE.value();
	}

	@Override
	public void tick() {
		super.tick();
	}

	// ExplosiveProjectileEntity //
	@Override
	public double getEffectiveRadius() {
		return 2.5;
	}

	@Override
	public double getMaxDamageRadius() {
		return 5;
	}

	@Override
	public double getDamageReduction() {
		return 0.25;
	}

	// TurretProjectileEntity //
	@Override
	public byte getMaxPierceLevel() {
		return 0;
	}
}
