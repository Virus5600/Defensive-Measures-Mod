package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * {@code KineticProjectileEntity} is an abstract class that extends {@link TurretProjectileEntity}.
 * This class is used to represent a projectile entity that uses kinetic energy to deal damage
 * to entities, such as arrows, bullets, and other similar projectiles.
 * <br><br>
 * By default, the kinetic projectile entity has the following behavior:
 * <ul>
 *     <li>Armor points affect the piercing of the projectile.</li>
 *     <li>Armor points affect the speed of the projectile.</li>
 *     <li>Armor points affect the base damage dealt by the projectile.</li>
 *     <li>Base damage is set to 4.</li>
 * </ul>
 */
public abstract class KineticProjectileEntity extends TurretProjectileEntity {

	// ///////////// //
	// CONSTRUCTORS //
	// ///////////// //
	public KineticProjectileEntity(EntityType<? extends TurretProjectileEntity> entityType, World world) {
		super(entityType, world);
		this.setDamage(4);
		this.setPierceLevel(this.getMaxPierceLevel());
		this.setSpeedAffectsDamage(false);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //
	// PROTECTED
	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	// PUBLIC
	@Override
	public boolean armorAffectsPiercing() {
		return true;
	}

	@Override
	public boolean armorAffectsVelocity() {
		return true;
	}

	@Override
	public boolean armorAffectsDamage() {
		return true;
	}
}
