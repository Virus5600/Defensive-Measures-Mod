package com.virus5600.defensive_measures.animations;

import net.minecraft.entity.AnimationState;
import net.minecraft.util.math.Vec3d;

import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

/**
 * A functional interface representing an action to be executed at a specific keyframe during an
 * animation sequence. This interface is designed to allow for the execution of custom behavior,
 * such as spawning particles, playing sounds, or applying effects, at specific points in an
 * animation. The execute method takes in the current animation state, the render state of the
 * turret, and the global position where the action should be executed.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@FunctionalInterface
public interface KeyframeAction {
	/**
	 * Executes the action defined by this KeyframeAction at the specified animation state, render
	 * state, and position.<br>
	 * The provided position is a global (world) position and not the offsets provided when creating
	 * a {@link ScriptKeyframe} instance.
	 *
	 * @param animState	The current state of the animation, which may include information such as the current frame, elapsed time, and other relevant data related to the animation's progress.
	 * @param state		The current render state of the turret, which may include information such as the turret's level, idle animation state, and other relevant data related to the turret's rendering.
	 * @param pos		The global (world) position where the action should be executed. This position is not the offsets provided when creating a ScriptKeyframe instance, but rather the actual position in the game world where the action should take place.
	 */
	void execute(AnimationState animState, BaseTurretRenderState state, Vec3d pos);
}
