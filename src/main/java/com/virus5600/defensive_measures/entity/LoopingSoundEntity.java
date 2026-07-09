package com.virus5600.defensive_measures.entity;

/**
 * An interface for entities that can play looping sounds. Implementing this interface allows the
 * entity to manage its sound state, such as starting and stopping the sound based on certain
 * conditions.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface LoopingSoundEntity {
	boolean isPlaying();
}
