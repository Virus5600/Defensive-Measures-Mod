package com.virus5600.defensive_measures.entity.projectiles.interfaces;

import net.minecraft.world.entity.LivingEntity;

import com.virus5600.defensive_measures.entity.projectiles.AreaCloudEntity;

/**
 * A functional interface representing an action to be performed by an {@link AreaCloudEntity} on a
 * target {@link LivingEntity}.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see AreaCloudEntity
 */
@FunctionalInterface
public interface CloudAction {
	/**
	 * Executes the action on the target entity.
	 *
	 * @param cloud the {@link AreaCloudEntity} performing the action
	 * @param target the {@link LivingEntity} being targeted by the action
	 */
	void execute(AreaCloudEntity cloud, LivingEntity target);
}
