package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.entity.turrets.FlameTurretEntity;

/**
 * The "projectile" used by {@link FlameTurretEntity}.
 * <br><br>
 * Represents the fire that the Flame Turret throws (as it is a flamethrower) which lingers on the
 * ground for a short period of time, damaging entities that are in contact with it. The damage is
 * applied every second and the fire can be re-applied every second as well, allowing the fire to
 * last for a long time if the target stays in contact with it. The fire also grows in size over
 * time, allowing it to cover a larger area and thus, damage more targets as well.
 * <br><br>
 * The fire also has a maximum radius and duration, allowing it to not last indefinitely and thus,
 * not be overpowered... though, it does 15 damage every second to its target so it is still
 * dangerous even with the limitations. The fire also has a re-application delay of 1 second,
 * allowing it to be re-applied every second, which means that if the target stays in contact with
 * the fire, they're cooked (in both literal and figurative sense) and will be taking damage every
 * second until the fire disappears or the target moves away from the fire.
 *
 * @see AreaCloudEntity
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class FlammableAerosolEntity extends AreaCloudEntity {
	/**
	 * Determines how long the entity will be set on fire. In this case, the default duration is
	 * 2.5 seconds ({@code 50 / 20} ticks).
	 */
	protected float fireDuration = 50;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public FlammableAerosolEntity(EntityType<? extends AreaCloudEntity> entityType, World world) {
		super(entityType, world);

		// Sets the base attribute of this area cloud...
		this.setParticleType(ParticleTypes.FLAME);
		this.setTargetRadius(2);						// Diameter of 4 meters; d = 2r
		this.setTargetAge(20);							// Lasts for 1 second; age is in ticks, so 20 ticks times 1 second
		this.setRadiusGrowth((float) (2 / 5 / 20));		// Grows to 2 blocks in 5 seconds; growth = (finalRadius - initialRadius) / (growthTime * ticksPerSecond)
		this.setReApplicationDelay(20);					// Re-applies every second
		this.setDamage(15);								// Deals 7.5 hearts of damage every second

		this.setCloudAction((cloud, target) -> {
			if (target.getEntityWorld() instanceof ServerWorld serverWorld) {
				target.setOnFireFor(this.fireDuration);

				DamageSource src = ModDamageSources.create(
					serverWorld,
					ModDamageTypes.THROWN_FLAME
				);

				target.damage(
					serverWorld, src,
					(float) this.getDamage()
				);
			}
		});
	}
}
