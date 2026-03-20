package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.entity.ModEntities;

import java.util.Map;

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
 * @version 1.0.0
 */
public class BallistaArrowEntity extends KineticProjectileEntity {
	private static final Map<String, Integer> ANIMATIONS;


	// ////////////// //
	//  CONSTRUCTORS  //
	// ////////////// //
	public BallistaArrowEntity(
		EntityType<? extends TurretProjectileEntity> entityType,
		World world
	) {
		super(entityType, world);

		this.setDamage(4);
	}

	public BallistaArrowEntity(World world, LivingEntity owner) {
		this(ModEntities.BALLISTA_ARROW, world);

		this.setPierceLevel(this.getMaxPierceLevel());
		this.setOwner(owner);
	}

	public BallistaArrowEntity(
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

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		ANIMATIONS = Map.of(
			"Idle", 0,
			"OnAir", 0
		);
	}
}
