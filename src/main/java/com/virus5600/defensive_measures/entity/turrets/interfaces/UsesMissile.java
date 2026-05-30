package com.virus5600.defensive_measures.entity.turrets.interfaces;

import com.virus5600.defensive_measures.entity.projectiles.MicroMissileEntity;

/**
 * An interface that provides a default method for entities that uses {@link MicroMissileEntity micro
 * missiles or subclasses of micro missiles} as their projectile.
 * <br><br>
 * This interface provides {@link #getMissileFuel()}, a method that identifies how far the missile's
 * engine will fire, allowing it to correct its path.
 *
 * @see MicroMissileEntity#getMissileFuel()
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface UsesMissile {
	int MISSILE_FUEL = 32;
	double MISSILE_SPEED = 20;
	double MISSILE_TURN_RATE = 30;

	/**
	 * Identifies how far the missile's engine will fire, allowing it to correct its path towards
	 * its target. By default, its fuel range is 32 blocks.
	 * @return 32 blocks
	 */
	default int getMissileFuel() {
		return MISSILE_FUEL;
	}

	/**
	 * Defines the speed in m/s (meters per second or blocks per seond). This will
	 * be used to identify how fast the missile will move.
	 *
	 * @return 20 m/s
	 */
	default double getMissileSpeed() {
		return MISSILE_SPEED;
	}

	/**
	 * Defines the turn rate in degrees per second. This will be used to identify how fast the
	 * missile will turn towards its target. By default, the turn rate is 30 degrees per second,
	 * allowing it to have a decent tracking ability while still being able to miss its target if
	 * the target is moving fast enough or if the missile is too far from the target.
	 *
	 * @return 30° per second
	 */
	default double getMissileTurnRate() {
		return MISSILE_TURN_RATE;
	}
}
