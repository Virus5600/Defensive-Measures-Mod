package com.virus5600.defensive_measures._helper;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import org.jspecify.annotations.Nullable;

/**
 * Contains all utility methods that can be used for the {@link ParticleTypes Particle Types} class
 * such as spawning particles in a specific pattern or shape. This is to make it easier to spawn
 * particles in a specific way without having to write the same code over and over again.
 *
 * @see ParticleTypes ParticleTypes
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/virus5600">virus5600</a>
 */
public final class ParticleHelper {

	/**
	 * Spawns a ring of particles around the given position with the given radius. The number of
	 * points in the ring is determined by the radius, with a minimum of 16 and a maximum of 150
	 * points.
	 * <br><br>
	 * Additionally, a sound effect can be played at each particle position if a {@link SoundEvent}
	 * is provided. If the sound effect is {@code null}, no sound will be played.
	 *
	 * @param level    The server level to spawn the particles in.
	 * @param particle The particle type to spawn.
	 * @param x        The x-coordinate of the center of the ring.
	 * @param y        The y-coordinate of the center of the ring.
	 * @param z        The z-coordinate of the center of the ring.
	 * @param radius   The radius of the ring.
	 * @param soundFx  The sound effect to play when the particles are spawned. Can be null.
	 */
	public static <T extends ParticleOptions> void spawnRing(
		ServerLevel level, T particle,
		double x, double y, double z,
		double radius, @Nullable SoundEvent soundFx
	) {
		int points = (int) Math.clamp(radius * 1.25, 16, 500);

		for (int p = 0; p < points; p++) {
			double a = 2 * Math.PI * p / points;
			double xPos = x + Math.cos(a) * radius;
			double yPos = y + Mth.nextDouble(level.getRandom(), -1, 5);
			double zPos = z + Math.sin(a) * radius;

			level.sendParticles(
				particle,
				xPos, yPos, zPos,
				1, 0, 0, 0, 0
			);

			if (soundFx != null) {
				level.playSound(
					null,
					xPos, yPos, zPos,
					soundFx, SoundSource.NEUTRAL
				);
			}
		}
	}
}
