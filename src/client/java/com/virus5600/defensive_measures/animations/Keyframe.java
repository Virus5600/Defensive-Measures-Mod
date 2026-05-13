package com.virus5600.defensive_measures.animations;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.AnimationState;

import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

/**
 * A keyframe is a point in time during an animation where the state of the animation is defined.
 * It contains information about the time of the keyframe and the state of the animation at that
 * time. Keyframes are used to create smooth animations by interpolating between them.<br>
 * <br>
 * However, this {@code Keyframe} class is just a complementary interface for the {@link FXKeyframe},
 * which are then used in the {@code setAngles} method via the {@code additionalDeathAnimProcedures}
 * and {@code additionalShootAnimProcedures} called inside the
 * {@link BaseTurretModel#setAngles(BaseTurretRenderState) setAngles} method. As such, this
 * interface is not meant to be implemented by any class other than any keyframe related procedures.
 *
 * @see FXKeyframe
 * @see BaseTurretModel#setAngles(BaseTurretRenderState)
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public interface Keyframe extends Comparable<Keyframe>{
	/**
	 * Gets the time of this keyframe in seconds.
	 *
	 * @return {@code double} the time of this keyframe in seconds
	 */
	double getTime();

	/**
	 * Applies the keyframe to the entity via the passed {@link AnimationState} and {@link EntityRenderState}.
	 *
	 * @param animState The {@link AnimationState} of the animation to which this keyframe belongs.
	 * @param state The {@link EntityRenderState} of the entity to which this keyframe is being applied.
	 */
	void apply(AnimationState animState, EntityRenderState state);

	/**
	 * Returns the time of this keyframe in milliseconds.
	 *
	 * @return {@code long} the time of this keyframe in milliseconds
	 *
	 * @see #getTime()
	 */
	int getTimeMS();
}
