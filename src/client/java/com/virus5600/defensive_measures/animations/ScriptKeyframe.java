package com.virus5600.defensive_measures.animations;

import com.virus5600.defensive_measures._util.MathUtil;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.math.Vec3d;

import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a keyframe in an animation sequence that executes a specified script at a given time
 * and position. The script is defined as a {@link KeyframeAction}, which is a functional interface
 * that takes an animation state, a render state, and a position as input parameters and performs a
 * specific action when executed. This class allows for the creation of keyframes that can trigger
 * custom behavior during an animation, such modifying entity state or creating explosions on death.
 *
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 * @see KeyframeAction
 * @since 1.1.0-beta
 */
public record ScriptKeyframe(double getTime, KeyframeAction script, Vec3d pos) implements Keyframe {
	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Creates a keyframe that executes the given script at the specified time, using the default
	 * position (0, 0, 0) for script execution.<br>
	 * <br>
	 * This constructor provides a convenient way to create a ScriptKeyframe without needing to
	 * specify a position, as it will default to the origin in 3D space. The script will be
	 * executed at the specified time during the animation, and any actions defined in the
	 * {@link KeyframeAction} will be performed at the default position.
	 *
	 * @param time   The time at which the script should be executed.
	 * @param script The action to be executed when the keyframe is applied.
	 */
	public ScriptKeyframe(double time, KeyframeAction script) {
		this(time, script, Vec3d.ZERO);
	}

	/**
	 * Creates a keyframe that executes the given script at the specified time and position.<br>
	 * <br>
	 * This constructor allows for the creation of a ScriptKeyframe with a specific position in 3D
	 * space where the script should be executed. The provided position is a global (world)
	 * position, and the script will be executed at that location when the keyframe is applied
	 * during the animation. This is useful for scenarios where the script's effects need to occur
	 * at a specific point in the game world, such as creating an explosion at the turret's
	 * location upon death.
	 *
	 * @param getTime   The time at which the script should be executed.
	 * @param script The action to be executed when the keyframe is applied.
	 * @param pos    The position in 3D space where the script should be executed.
	 */
	public ScriptKeyframe {
	}

	// ///////// //
	// FACTORIES //
	// ///////// //

	/**
	 * Creates a keyframe that executes the given script at the specified time, using the default
	 * position (0, 0, 0) for script execution.<br>
	 * <br>
	 * This factory method provides a convenient way to create a ScriptKeyframe without needing to
	 * specify a position, as it will default to the origin in 3D space. The script will be
	 * executed at the specified time during the animation, and any actions defined in the
	 * {@link KeyframeAction} will be performed at the default position.
	 *
	 * @param time   The time at which the script should be executed.
	 * @param script The action to be executed when the keyframe is applied.
	 * @return A new instance of ScriptKeyframe with the specified time and script, using the default position (0, 0, 0) for script execution.
	 */
	public static ScriptKeyframe of(double time, KeyframeAction script) {
		return new ScriptKeyframe(time, script);
	}

	/**
	 * Creates a keyframe that executes the given script at the specified time and position.<br>
	 * <br>
	 * This factory method allows for the creation of a ScriptKeyframe with a specific position in
	 * 3D space where the script should be executed. The provided position is a global (world)
	 * position, and the script will be executed at that location when the keyframe is applied
	 * during the animation. This is useful for scenarios where the script's effects need to occur
	 * at a specific point in the game world, such as creating an explosion at the turret's
	 * location upon death.
	 *
	 * @param time   The time at which the script should be executed.
	 * @param script The action to be executed when the keyframe is applied.
	 * @param pos    The position in 3D space where the script should be executed.
	 * @return A new instance of ScriptKeyframe with the specified time, script, and position for script execution.
	 */
	public static ScriptKeyframe of(double time, KeyframeAction script, Vec3d pos) {
		return new ScriptKeyframe(time, script, pos);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	public int getTimeMS() {
		return (int) (this.getTime() * 1000);
	}

	@Nullable
	public KeyframeAction getScript() {
		return this.script;
	}


	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //


	@Override
	public void apply(AnimationState animState, EntityRenderState s) {
		if (this.script != null) {
			if (s instanceof BaseTurretRenderState state) {
				Vec3d globalPos = MathUtil.getRelativePos(
					state.eyePos, this.pos(),
					state.relativeHeadYaw, state.pitch
				);

				this.script.execute(
					animState,
					state,
					globalPos
				);
			}
		}
	}

	// COMPARABLE METHODS //
	@Override
	public int compareTo(Keyframe other) {
		return Double.compare(
			this.getTime(),
			other.getTime()
		);
	}
}
