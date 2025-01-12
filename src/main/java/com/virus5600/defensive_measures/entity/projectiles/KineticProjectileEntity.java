package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.EntityType;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

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

	/**
	 * {@inheritDoc}
	 * <hr>
	 * <h2>{@link KineticProjectileEntity}</h2>
	 * <br><br>
	 * The method in {@code KineticProjectileEntity} reduces the speed of the arrow
	 * along with the pierce level based on the amount of armor points the entity has.
	 * <br><br>
	 * In this implementation, the projectile will have the following behavior
	 * (assuming the projectile is affected by armor points):
	 * <table>
	 * 	<tr>
	 * 		<th>Armor Points</th>
	 * 		<th>Pierce Level Reduction</th>
	 * 		<th>Velocity Reduction</th>
	 * 		<th>Base Damage Reduction</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>Heavy Armor</td>
	 * 		<td>2</td>
	 * 		<td>50%</td>
	 * 		<td>50%</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>Light Armor</td>
	 * 		<td>1</td>
	 * 		<td>25%</td>
	 * 		<td>20%</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>No Armor</td>
	 * 		<td>N/A</td>
	 * 		<td>12.5%</td>
	 * 		<td>5%</td>
	 * 	</tr>
	 * </table>
	 *
	 * @param entityHitResult {@inheritDoc EntityHitResult}
	 */
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);

		if (this.armorAffectsPiercing()) {
			// Reduces the speed of the arrow when it hits an entity
			Entity entity = entityHitResult.getEntity();
			// For reference, max armor points is 30
			int armor = entity instanceof LivingEntity livingEntity ? livingEntity.getArmor() : 0;
			double dmgReduction = 0.05;
			boolean hasHeavyArmor = armor > 15,
				hasLightArmor = armor <= 15 && armor > 0;

			if (entity instanceof LivingEntity livingEntity) {
				double targetH = livingEntity.getHeight(),
					arrowH = this.getHeight(),
					variance = arrowH * 0.125; // 12.5% of the arrow's height

				double arrowMin = arrowH - variance,
					arrowMax = arrowH + variance,
					reducedVelocity = 0.125;

				// Reduce velocity and pierce level based on the side of the entity hit
				if (this.getPierceLevel() > 0) {
					// If the arrow is smaller than the target or has heavy armor, reduce the pierce level by 2, velocity by 50%, and base damage by 50%
					if (targetH > arrowMax || hasHeavyArmor) {
						this.setPierceLevel((byte) (this.getPierceLevel() - 2));
						reducedVelocity = 0.5;
						dmgReduction = 0.5;
					}
					// If the arrow is almost the same size as the target or has a light armor, reduce the pierce level by 1, velocity by 25%, and base damage by 20%
					else if ((targetH < arrowMax && arrowMin < targetH) || hasLightArmor) {
						this.setPierceLevel((byte) (this.getPierceLevel() - 1));
						reducedVelocity = 0.25;
						dmgReduction = 0.2;
					}
					// Otherwise, just reduce the velocity by 12.5% and base damage by 5% without reducing the pierce level
				}

				// Apply reduced velocity
				this.addVelocity(
					this.getVelocity()
						.multiply(reducedVelocity)
						.negate()
				);

				// Apply reduced damage
				this.setDamage(this.getDamage() * (1 - dmgReduction));
			}

			this.addVelocity(
				this.getVelocity()
					.negate()
					.multiply(0.1)
			);
		}
	}

	// /////////////////////////////// //
	// GETTERS, SETTERS, AND QUESTIONS //
	// /////////////////////////////// //

	// PUBLIC
	/**
	 * An overridable method that determines whether an armor point affects the
	 * piercing of the projectile.
	 * <br><br>
	 * By default, this method returns {@code true}.
	 *
	 * @return {@code true} if the armor point affects the piercing of the projectile,
	 * {@code false} otherwise.
	 */
	public boolean armorAffectsPiercing() {
		return true;
	}
}
