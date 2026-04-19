package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.entity.ModEntities;

/**
 * The projectile used by {@link com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity Ballista Turret}.
 *
 * <p>Represents a ballista arrow projectile entity that pierces through multiple entities.</p>
 * <p>
 *     The ballista arrow is a {@link KineticProjectileEntity kinetic projectile}
 *     that is fired from a {@link com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity ballista turret}.
 * </p>
 * <p>
 *     This projectile can pierce through multiple entities, dealing 4 hearts of damage to each
 *     entity it hits. The arrow can pierce through a maximum of 5 entities before it is destroyed.
 *     And everytime it pierces through an entity, it will reduce its pierce level its speed
 *     depending on the armor of the entity it hit.
 * </p>
 * <p>
 * 		To see how the reduction mechanics work, check {@link TurretProjectileEntity#onEntityHit(EntityHitResult)} method.
 * </p>
 *
 * @see KineticProjectileEntity
 * @see TurretProjectileEntity
 * @see TurretProjectileEntity#onEntityHit(EntityHitResult)
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.1
 */
public class BallistaBoltEntity extends KineticProjectileEntity {
	// ////////////// //
	//  CONSTRUCTORS  //
	// ////////////// //
	public BallistaBoltEntity(
		EntityType<? extends TurretProjectileEntity> entityType,
		World world
	) {
		super(entityType, world);

		this.setPierceLevel(this.getMaxPierceLevel());
		this.setDamage(4);
	}

	public BallistaBoltEntity(World world, LivingEntity owner) {
		this(ModEntities.BALLISTA_BOLT, world);

		this.setOwner(owner);
	}

	public BallistaBoltEntity(
		LivingEntity owner,
		double directionX,
		double directionY,
		double directionZ,
		World world
	) {
		this(world, owner);
		this.setVelocity(directionX, directionY, directionZ);
	}

	// ///////// //
	//  METHODS  //
	// ///////// //

	// PUBLIC
	@Override
	public byte getMaxPierceLevel() {
		return 5;
	}
}
