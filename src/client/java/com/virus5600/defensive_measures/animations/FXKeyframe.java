package com.virus5600.defensive_measures.animations;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import org.jetbrains.annotations.Nullable;

/**
 * A keyframe specially designed to run particle and sound event keyframes. This class was born out
 * of spite due to the lack of support for particle and sound keyframes, making life miserable when
 * implementing effects for animations.
 *
 * @param getTime  The time (in seconds) when this keyframe will take effect.
 * @param particle The particle it will emit when the keyframe is executed.
 * @param sound    The sound event it will play when the keyframe is executed.
 * @param pos      The position at where this keyframe will emit the particle and/or play the sound event.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public record FXKeyframe(double getTime, ParticleOptions particle, SoundEvent sound,
                         Vec3 pos) implements Keyframe {
	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Create a particle keyframe for the specified time (in seconds) and particle effect. This
	 * sets the position to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative ) of the target entity by default.
	 *
	 * @param time     The specified time in seconds when the particle effect should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 */
	public FXKeyframe(double time, ParticleOptions particle) {
		this(time, particle, Vec3.ZERO);
	}

	/**
	 * Create a sound keyframe for the specified time (in seconds) and sound event. This
	 * sets the position to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative ) of the target entity by default.
	 *
	 * @param time  The specified time in seconds when the sound event should be played.
	 * @param sound The sound event to be played at the specified time.
	 */
	public FXKeyframe(double time, SoundEvent sound) {
		this(time, sound, Vec3.ZERO);
	}

	/**
	 * Create a particle and sound keyframe for the specified time (in seconds), particle effect,
	 * and sound event. This sets the position to the origin {@code (0, 0, 0)} (or
	 * {@code (0.5, 0.5, 0.5)} in block position relative ) of the target entity by default.
	 *
	 * @param time     The specified time in seconds when the particle effect and sound event should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param sound    The sound event to be played at the specified time.
	 */
	public FXKeyframe(double time, ParticleOptions particle, SoundEvent sound) {
		this(time, particle, sound, Vec3.ZERO);
	}

	/**
	 * Create a particle keyframe for the specified time (in seconds), particle effect, and position.
	 * The position is relative to the target entity, and the particle effect will be applied at the
	 * specified position when the keyframe is reached during the animation. If the position is not
	 * specified, it defaults to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative) of the target entity.
	 *
	 * @param time     The specified time in seconds when the particle effect should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param pos      The position relative to the target entity where the particle effect should be applied.
	 */
	public FXKeyframe(double time, ParticleOptions particle, Vec3 pos) {
		this(time, particle, null, pos);
	}

	/**
	 * Create a sound keyframe for the specified time (in seconds), sound event, and position.
	 * The position is relative to the target entity, and the particle effect will be applied at the
	 * specified position when the keyframe is reached during the animation. If the position is not
	 * specified, it defaults to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative) of the target entity.
	 *
	 * @param time  The specified time in seconds when the sound event should be applied.
	 * @param sound The sound event to be played at the specified time.
	 * @param pos   The position relative to the target entity where the sound event should be applied.
	 */
	public FXKeyframe(double time, SoundEvent sound, Vec3 pos) {
		this(time, null, sound, pos);
	}

	/**
	 * Create a particle and sound keyframe for the specified time (in seconds), particle effect,
	 * sound event, and position. The position is relative to the target entity, and the particle
	 * effect and sound event will be applied at the specified position when the keyframe is
	 * reached during the animation. If the position is not specified, it defaults to the origin
	 * {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block position relative) of the target
	 * entity.
	 *
	 * @param getTime     The specified time in seconds when the particle effect and sound event should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param sound    The sound event to be played at the specified time.
	 * @param pos      The position relative to the target entity where the particle effect and sound event should be applied.
	 */
	public FXKeyframe {
	}

	// ///////// //
	// FACTORIES //
	// ///////// //

	/**
	 * Create a particle keyframe for the specified time (in seconds) and particle effect. This
	 * sets the position to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative ) of the target entity by default.
	 *
	 * @param time     The specified time in seconds when the particle effect should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, ParticleOptions particle) {
		return of(time, particle, Vec3.ZERO);
	}

	/**
	 * Create a particle keyframe for the specified time (in seconds), particle effect, and position.
	 * The position is relative to the target entity, and the particle effect will be applied at the
	 * specified position when the keyframe is reached during the animation. If the position is not
	 * specified, it defaults to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative) of the target entity.
	 *
	 * @param time     The specified time in seconds when the particle effect should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param pos      The position relative to the target entity where the particle effect should be applied.
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, ParticleOptions particle, Vec3 pos) {
		return new FXKeyframe(time, particle, pos);
	}

	/**
	 * Create a sound keyframe for the specified time (in seconds) and sound event. This
	 * sets the position to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative ) of the target entity by default.
	 *
	 * @param time  The specified time in seconds when the sound event should be applied.
	 * @param sound The sound event to be played at the specified time.
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, SoundEvent sound) {
		return new FXKeyframe(time, null, sound, Vec3.ZERO);
	}

	/**
	 * Create a sound keyframe for the specified time (in seconds), sound event, and position. The
	 * position is relative to the target entity, and the particle effect will be applied at the
	 * specified position when the keyframe is reached during the animation. If the position is not
	 * specified, it defaults to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative) of the target entity.
	 *
	 * @param time  The specified time in seconds when the sound event should be applied.
	 * @param sound The sound event to be played at the specified time.
	 * @param pos   The position relative to the target entity where the sound event should be applied
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, SoundEvent sound, Vec3 pos) {
		return new FXKeyframe(time, sound, pos);
	}

	/**
	 * Create a particle and sound keyframe for the specified time (in seconds), particle effect,
	 * and sound event. This sets the position to the origin {@code (0, 0, 0)} (or
	 * {@code (0.5, 0.5, 0.5)} in block position relative ) of the target entity by default.
	 *
	 * @param time     The specified time in seconds when the particle effect and sound event should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param sound    The sound event to be played at the specified time.
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, ParticleOptions particle, SoundEvent sound) {
		return new FXKeyframe(time, particle, sound, Vec3.ZERO);
	}

	/**
	 * Create a particle and sound keyframe for the specified time (in seconds), particle effect,
	 * sound event, and position. The position is relative to the target entity, and the particle
	 * effect and sound event will be applied at the specified position when the keyframe is
	 * reached during the animation. If the position is not specified, it defaults to the origin
	 * {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block position relative) of the target
	 * entity.
	 *
	 * @param time     The specified time in seconds when the particle effect and sound event should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param sound    The sound event to be played at the specified time.
	 * @param pos      The position relative to the target entity where the particle effect and sound event should be applied.
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, ParticleOptions particle, SoundEvent sound, Vec3 pos) {
		return new FXKeyframe(time, particle, sound, pos);
	}

	// /////// //
	// METHODS //
	// /////// //
	public int getTimeMS() {
		return (int) (this.getTime() * 1000);
	}

	@Override
	@Nullable
	public ParticleOptions particle() {
		return this.particle;
	}

	@Override
	@Nullable
	public SoundEvent sound() {
		return this.sound;
	}


	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //


	@Override
	public void apply(AnimationState animState, EntityRenderState s) {
		if (s instanceof BaseTurretRenderState state) {
			Vec3 origin = state.eyePos;
			ClientLevel world = Minecraft.getInstance().level;

			if (world != null) {
				Vec3 pos = MathUtil.getRelativePos(
					origin, this.pos(),
					state.yRot, state.xRot
				);

				// Handles particle frames
				if (this.particle() != null) {
					world.addParticle(
						this.particle(),
						pos.x(), pos.y(), pos.z(),
						0, 0, 0
					);
				}

				// Handles sound frames
				if (this.sound() != null) {
					world.playLocalSound(
						pos.x(), pos.y(), pos.z(),
						this.sound(), SoundSource.NEUTRAL,
						1, 1, true
					);
				}
			}
		}
	}
}
