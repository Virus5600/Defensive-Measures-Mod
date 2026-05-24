package com.virus5600.defensive_measures.entity.turrets.interfaces;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.network.clientbound.sounds.TurretLoopSoundPacket;

/**
 * An interface that can be implemented by turret entities that have a shooting loopSound which should
 * loop while shooting.
 * <br><br>
 * This interface provides a default method to handle the logic of starting and stopping the
 * shooting loopSound loop based on the turret's shooting state and whether it was shooting in the
 * previous tick. Implementing this interface allows for easy addition of loopable shooting sounds
 * to any turret entity without needing to duplicate the loopSound management logic in each turret class.
 * <br><br>
 * To use this interface, simply implement it in your turret entity class and provide an implementation
 * for the {@link #wasShootingLastTick()} method to track the shooting state across ticks. Then,
 * call the {@link #playShootSound(TurretEntity)} method in your turret's {@code tick} method to
 * handle the loopSound playback.
 *
 * @see TurretEntity
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface LoopableShootingSound {
	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	/**
	 * Sets the shooting state for the last tick. This is called automatically by the
	 * {@link #playShootSound(TurretEntity)} to set the local boolean variable in the turret class,
	 * allowing easier implementation of tracking whether the turret was shooting last tick or not.
	 */
	void setShootingLastTick(boolean isShooting);

	/**
	 * Determines if this is shooting on the previous tick. Used to identify whether to start or
	 * stop the shooting loopSound loop. This is necessary to prevent the loopSound from restarting every
	 * tick while shooting, which would cause it to loopSound very choppy and unnatural.
	 */
	boolean wasShootingLastTick();

	/**
	 * Gets the loopSound event to play when the turret starts shooting. This is typically a one-shot
	 * loopSound that plays once at the start of the shooting action, such as a firing loopSound or a
	 * charge-up loopSound. This loopSound is played using the normal {@code world.playSound} method and
	 * does not loop.
	 *
	 * @return The loopSound event to play when the turret starts shooting.
	 *
	 * @see #getShootLoopSound()
	 * @see #getShootEndSound()
	 * @see #playShootSound(TurretEntity)
	 */
	SoundEvent getShootStartSound();

	/**
	 * Gets the loopSound event to play in a loop while the turret is shooting. This loopSound is typically
	 * a continuous loopSound that represents the ongoing shooting action, such as a machine gun firing
	 * or a flamethrower spewing fire. This loopSound is played using a custom loopSound instance that loops
	 * while the turret is shooting, and it is managed separately from the start and end sounds to
	 * allow for smooth transitions and proper handling of the shooting state.
	 *
	 * @return The loopSound event to play in a loop while the turret is shooting.
	 *
	 * @see #getShootStartSound()
	 * @see #getShootEndSound()
	 * @see #playShootSound(TurretEntity)
	 */
	SoundEvent getShootLoopSound();

	/**
	 * Gets the loopSound event to play when the turret stops shooting. This is typically a one-shot
	 * loopSound that plays once at the end of the shooting action, such as a release loopSound or a
	 * cooldown loopSound. This loopSound is played using the normal {@code world.playSound} method and
	 * does not loop.
	 *
	 * @return The loopSound event to play when the turret stops shooting.
	 *
	 * @see #getShootStartSound()
	 * @see #getShootLoopSound()
	 * @see #playShootSound(TurretEntity)
	 */
	SoundEvent getShootEndSound();

	// /////////////// //
	// DEFAULT METHODS //
	// /////////////// //

	default SoundCategory getSoundCategory() {
		return SoundCategory.NEUTRAL;
	}

	/**
	 * Plays the shooting loopSound for the turret. This should be called every tick in the turret's
	 * tick method, and it will handle whether to start or stop the loopSound loop based on the turret's
	 * shooting state and the {@link #wasShootingLastTick()} method.
	 *
	 * @param turret The turret entity for which to play the shooting loopSound.
	 */
	default void playShootSound(TurretEntity turret) {
		World world = turret.getEntityWorld();

		if (world instanceof ServerWorld) {
			boolean isShootingNow = turret.getTrackedLockedButNotAttacking() || turret.getTrackedShooting();

			// Starts playing the loopSound loop if the turret is shooting now but wasn't shooting in the last tick
			if (isShootingNow) {
				if (turret.getTarget() != null) {
					this.setShootingLastTick(true);

					for (ServerPlayerEntity player : PlayerLookup.tracking(turret)) {
						ServerPlayNetworking.send(
							player,
							new TurretLoopSoundPacket(
								turret.getId(),
								true,
								this.getShootStartSound(),
								this.getShootLoopSound(),
								this.getShootEndSound(),
								this.getSoundCategory()
							)
						);
					}
				}
			}
			// If the turret is not shooting now but was shooting in the last tick, stop the loopSound loop
			else {
				if (this.wasShootingLastTick()) {
					this.setShootingLastTick(false);

					// Send the STOP packet to every player tracking this turret
					for (ServerPlayerEntity player : PlayerLookup.tracking(turret)) {
						ServerPlayNetworking.send(
							player,
							new TurretLoopSoundPacket(
								turret.getId(),
								false,
								this.getShootStartSound(),
								this.getShootLoopSound(),
								this.getShootEndSound(),
								this.getSoundCategory()
							)
						);
					}
				}
			}
		}
	}
}
