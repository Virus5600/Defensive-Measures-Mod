package com.virus5600.defensive_measures.block;

import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures._util.interfaces.ModExplosives;

/**
 * An interface for blocks that explodes. Implementing this interface allows the block to define
 * its explosion properties, such as effective radius, maximum damage radius, damage reduction, and
 * base damage. It also provides methods to create explosions with specific parameters.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface ExplosiveBlock extends ModExplosives {
	/**
	 * Determines the explosion source type of this explosive block. This will determine the
	 * destruction type of the explosion and whether the explosion can destroy blocks or not based
	 * on the gamerule.
	 * <br><br>
	 * By default, this method returns {@link Level.ExplosionInteraction#BLOCK}.
	 *
	 * @return The explosion source type of this explosive block.
	 */
	default Level.ExplosionInteraction getExplosionSourceType() {
		return Level.ExplosionInteraction.BLOCK;
	}
}
