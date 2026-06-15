package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.turrets.tier1.BallistaTurretEntity;

/**
 * The projectile used by {@link BallistaTurretEntity Ballista Turret}.
 *
 * <p>Represents a ballista arrow projectile entity that pierces through multiple entities.</p>
 * <p>
 *     The ballista arrow is a {@link KineticProjectileEntity kinetic projectile}
 *     that is fired from a {@link BallistaTurretEntity ballista turret}.
 * </p>
 * <p>
 *     This projectile can pierce through multiple entities, dealing 4 hearts of damage to each
 *     entity it hits. The arrow can pierce through a maximum of 5 entities before it is destroyed.
 *     And everytime it pierces through an entity, it will reduce its pierce level its speed
 *     depending on the armor of the entity it hit.
 * </p>
 * <p>
 * 		To see how the reduction mechanics work, check {@link TurretProjectileEntity#onHitEntity(EntityHitResult)} method.
 * </p>
 *
 * @see KineticProjectileEntity
 * @see TurretProjectileEntity
 * @see TurretProjectileEntity#onHitEntity(EntityHitResult)
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BallistaBoltEntity extends KineticProjectileEntity {
	// ////////////// //
	//  CONSTRUCTORS  //
	// ////////////// //
	public BallistaBoltEntity(
            EntityType<? extends TurretProjectileEntity> entityType,
            Level world
	) {
		super(entityType, world);

		this.setPierceLevel(this.getMaxPierceLevel());
		this.setDamage(4);
	}

	public BallistaBoltEntity(Level world, LivingEntity owner) {
		this(ModEntities.BALLISTA_BOLT, world);

		this.setOwner(owner);
	}

	public BallistaBoltEntity(
            LivingEntity owner,
            double directionX,
            double directionY,
            double directionZ,
            Level world
	) {
		this(world, owner);
		this.setDeltaMovement(directionX, directionY, directionZ);
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
