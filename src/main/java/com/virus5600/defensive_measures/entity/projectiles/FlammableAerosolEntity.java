package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.entity.turrets.tier2.FlameTurretEntity;

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
	protected static final ParticleEffect DEFAULT_PARTICLE = ParticleTypes.FLAME;
	protected static final int DEFAULT_FIRE_DURATION = 50;

	/**
	 * Determines how long the entity will be set on fire. In this case, the default duration is
	 * 2.5 seconds ({@code 50 / 20} ticks).
	 */
	protected int fireDuration = DEFAULT_FIRE_DURATION;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public FlammableAerosolEntity(EntityType<? extends AreaCloudEntity> entityType, World world) {
		super(entityType, world);

		// Sets the base attribute of this area cloud...
		// ALWAYS GETS CALLED JUST IN CASE. Attributes can be overriden by simply using the
		// setters after creating the entity.
		setDefaultAttr(this);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //
	public static void setDefaultAttr(FlammableAerosolEntity cloud) {
		cloud.setParticleType(DEFAULT_PARTICLE);
		cloud.setRadius(0.5f);
		cloud.setTargetRadius(2f);						// Diameter of 4 meters; d = 2r
		cloud.setTargetAge(30);							// Lasts for 1.5 seconds; age is in ticks, so 20 ticks times 1.5 second
		cloud.setRadiusGrowth((2.0f / 1.5f / 20.0f));	// Grows to 2 blocks in 5 seconds; growth = (finalRadius - initialRadius) / (growthTime * ticksPerSecond)
		cloud.setReApplicationDelay(10);				// Re-applies every 0.5 seconds
		cloud.setDamage(15);							// Deals 7.5 hearts of damage every second

		// Sets the action of this cloud to damaging the entity and setting it on fire for a short
		// period of time...
		cloud.setCloudAction((cloudEntity, target) -> {
			if (target.getEntityWorld() instanceof ServerWorld serverWorld &&
			cloudEntity instanceof FlammableAerosolEntity ce) {
				target.setOnFireFor(ce.getFireDuration() / 20f);

				DamageSource src = ModDamageSources.create(
					serverWorld,
					ModDamageTypes.THROWN_FLAME,
					cloudEntity, cloudEntity.getOwner()
				);

				target.damage(
					serverWorld, src,
					(float) ce.getDamage()
				);
			}
		});
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	@Override
	protected ParticleEffect getDefaultParticle() {
		return DEFAULT_PARTICLE;
	}

	/**
	 * Sets the duration of the fire that this entity applies to its targets (in ticks). The
	 * default duration is 2.5 seconds.
	 *
	 * @param duration the duration of the fire in ticks
	 *
	 * @see #DEFAULT_FIRE_DURATION
	 */
	public void setFireDuration(int duration) {
		this.fireDuration = duration;
	}

	public int getFireDuration() {
		return this.fireDuration;
	}
}
