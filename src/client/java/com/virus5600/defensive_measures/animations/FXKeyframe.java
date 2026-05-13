package com.virus5600.defensive_measures.animations;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AnimationState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import org.jetbrains.annotations.Nullable;

public class FXKeyframe implements Keyframe {
	protected final double time;
	protected final ParticleEffect particle;
	protected final SoundEvent sound;
	protected final Vec3d pos;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Create a particle keyframe for the specified time (in seconds) and particle effect. This
	 * sets the position to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative ) of the target entity by default.
	 *
	 * @param time The specified time in seconds when the particle effect should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 */
	public FXKeyframe(double time, ParticleEffect particle) {
		this(time, particle, Vec3d.ZERO);
	}

	/**
	 * Create a sound keyframe for the specified time (in seconds) and sound event. This
	 * sets the position to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative ) of the target entity by default.
	 *
	 * @param time The specified time in seconds when the sound event should be played.
	 * @param sound The sound event to be played at the specified time.
	 */
	public FXKeyframe(double time, SoundEvent sound) {
		this(time, sound, Vec3d.ZERO);
	}

	/**
	 * Create a particle and sound keyframe for the specified time (in seconds), particle effect,
	 * and sound event. This sets the position to the origin {@code (0, 0, 0)} (or
	 * {@code (0.5, 0.5, 0.5)} in block position relative ) of the target entity by default.
	 *
	 * @param time The specified time in seconds when the particle effect and sound event should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param sound The sound event to be played at the specified time.
	 */
	public FXKeyframe(double time, ParticleEffect particle, SoundEvent sound) {
		this(time, particle, sound, Vec3d.ZERO);
	}

	/**
	 * Create a particle keyframe for the specified time (in seconds), particle effect, and position.
	 * The position is relative to the target entity, and the particle effect will be applied at the
	 * specified position when the keyframe is reached during the animation. If the position is not
	 * specified, it defaults to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative) of the target entity.
	 *
	 * @param time The specified time in seconds when the particle effect should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param pos The position relative to the target entity where the particle effect should be applied.
	 */
	public FXKeyframe(double time, ParticleEffect particle, Vec3d pos) {
		this(time, particle, null, pos);
	}

	/**
	 * Create a sound keyframe for the specified time (in seconds), sound event, and position.
	 * The position is relative to the target entity, and the particle effect will be applied at the
	 * specified position when the keyframe is reached during the animation. If the position is not
	 * specified, it defaults to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative) of the target entity.
	 *
	 * @param time The specified time in seconds when the sound event should be applied.
	 * @param sound The sound event to be played at the specified time.
	 * @param pos The position relative to the target entity where the sound event should be applied.
	 */
	public FXKeyframe(double time, SoundEvent sound, Vec3d pos) {
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
	 * @param time The specified time in seconds when the particle effect and sound event should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param sound The sound event to be played at the specified time.
	 * @param pos The position relative to the target entity where the particle effect and sound event should be applied.
	 */
	public FXKeyframe(double time, ParticleEffect particle, SoundEvent sound, Vec3d pos) {
		this.time = time;
		this.particle = particle;
		this.sound = sound;
		this.pos = pos;
	}

	// ///////// //
	// FACTORIES //
	// ///////// //

	/**
	 * Create a particle keyframe for the specified time (in seconds) and particle effect. This
	 * sets the position to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative ) of the target entity by default.
	 *
	 * @param time The specified time in seconds when the particle effect should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 *
	 * @return A new instance of {@code FXKeyframe}.
	 *
	 * @see #FXKeyframe(double, ParticleEffect)
	 * @see #FXKeyframe(double, ParticleEffect)
	 * @see #of(double, ParticleEffect)
	 */
	public static FXKeyframe of(double time, ParticleEffect particle) {
		return of(time, particle, Vec3d.ZERO);
	}

	/**
	 * Create a particle keyframe for the specified time (in seconds), particle effect, and position.
	 * The position is relative to the target entity, and the particle effect will be applied at the
	 * specified position when the keyframe is reached during the animation. If the position is not
	 * specified, it defaults to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative) of the target entity.
	 *
	 * @param time The specified time in seconds when the particle effect should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param pos The position relative to the target entity where the particle effect should be applied.
	 *
	 * @return A new instance of {@code FXKeyframe}.
	 *
	 * @see #FXKeyframe(double, ParticleEffect)
	 * @see #FXKeyframe(double, ParticleEffect)
	 * @see #of(double, ParticleEffect)
	 */
	public static FXKeyframe of(double time, ParticleEffect particle, Vec3d pos) {
		return new FXKeyframe(time, particle, pos);
	}

	/**
	 * Create a sound keyframe for the specified time (in seconds) and sound event. This
	 * sets the position to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative ) of the target entity by default.
	 *
	 * @param time The specified time in seconds when the sound event should be applied.
	 * @param sound The sound event to be played at the specified time.
	 *
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, SoundEvent sound) {
		return new FXKeyframe(time, null, sound, Vec3d.ZERO);
	}

	/**
	 * Create a sound keyframe for the specified time (in seconds), sound event, and position. The
	 * position is relative to the target entity, and the particle effect will be applied at the
	 * specified position when the keyframe is reached during the animation. If the position is not
	 * specified, it defaults to the origin {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block
	 * position relative) of the target entity.
	 *
	 * @param time The specified time in seconds when the sound event should be applied.
	 * @param sound The sound event to be played at the specified time.
	 * @param pos The position relative to the target entity where the sound event should be applied
	 *
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, SoundEvent sound, Vec3d pos) {
		return new FXKeyframe(time, sound, pos);
	}

	/**
	 * Create a particle and sound keyframe for the specified time (in seconds), particle effect,
	 * and sound event. This sets the position to the origin {@code (0, 0, 0)} (or
	 * {@code (0.5, 0.5, 0.5)} in block position relative ) of the target entity by default.
	 *
	 * @param time The specified time in seconds when the particle effect and sound event should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param sound The sound event to be played at the specified time.
	 *
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, ParticleEffect particle, SoundEvent sound) {
		return new FXKeyframe(time, particle, sound, Vec3d.ZERO);
	}

	/**
	 * Create a particle and sound keyframe for the specified time (in seconds), particle effect,
	 * sound event, and position. The position is relative to the target entity, and the particle
	 * effect and sound event will be applied at the specified position when the keyframe is
	 * reached during the animation. If the position is not specified, it defaults to the origin
	 * {@code (0, 0, 0)} (or {@code (0.5, 0.5, 0.5)} in block position relative) of the target
	 * entity.
	 *
	 * @param time The specified time in seconds when the particle effect and sound event should be applied.
	 * @param particle The particle effect to be applied at the specified time.
	 * @param sound The sound event to be played at the specified time.
	 * @param pos The position relative to the target entity where the particle effect and sound event should be applied.
	 *
	 * @return A new instance of {@code FXKeyframe}.
	 */
	public static FXKeyframe of(double time, ParticleEffect particle, SoundEvent sound, Vec3d pos) {
		return new FXKeyframe(time, particle, sound, pos);
	}

	// /////// //
	// METHODS //
	// /////// //
	public int getTimeMS()  {
		return (int) (this.getTime() * 1000);
	}

	@Nullable
	public ParticleEffect getParticle() {
		return this.particle;
	}

	@Nullable
	public SoundEvent getSound() {
		return this.sound;
	}

	public Vec3d getPos() {
		return this.pos;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	// KEYFRAME METHODS //
	@Override
	public double getTime() {
		return this.time;
	}

	@Override
	public void apply(AnimationState animState, EntityRenderState s) {
		if (s instanceof BaseTurretRenderState state) {
			Vec3d origin = state.eyePos;
			ClientWorld world = MinecraftClient.getInstance().world;

			if (world != null) {
				Vec3d pos = MathUtil.getRelativePos(
					origin, this.getPos(),
					state.relativeHeadYaw, state.pitch
				);

				// Handles particle frames
				if (this.getParticle() != null) {
					world.addParticleClient(
						this.getParticle(),
						pos.getX(), pos.getY(), pos.getZ(),
						0, 0, 0
					);
				}

				// Handles sound frames
				if (this.getSound() != null) {
					world.playSoundClient(
						pos.getX(), pos.getY(), pos.getZ(),
						this.getSound(), SoundCategory.NEUTRAL,
						1, 1, true
					);
				}
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
