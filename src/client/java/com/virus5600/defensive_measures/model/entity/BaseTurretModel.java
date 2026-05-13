package com.virus5600.defensive_measures.model.entity;

import com.virus5600.defensive_measures.animations.FXKeyframe;
import com.virus5600.defensive_measures.animations.Keyframe;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.math.MathHelper;

import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.model.BaseModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A base model for turrets which handles all the common logic for rendering
 * a {@link TurretEntity turret entity}. The class also provides helper methods
 * for setting the pitch and yaw rotation of the head, which are also
 * overridables so that you can customize the rendering behavior of the turret.
 * <br><br>
 * This class is meant to be extended by all turret models in the mod, such as {@link CannonTurretModel},
 * so that all the common rendering logic can be shared among all turret. This centralization also
 * meant that the render state used by all turret models will be the same, which is {@link BaseTurretRenderState},
 * so that all turret models can be rendered using the same render state, allowing for more
 * consistent and efficient rendering of turret entities in the game. However, when more states are
 * needed to be added to the render state, a new render state class that extends {@link BaseTurretRenderState}
 * could be used, allowing flexibility while keeping everything DRY.
 *
 * @param <S> The type of the render state this model uses. This should be a class that extends
 *           {@link BaseTurretRenderState} and contains all the necessary information for rendering
 *           the turret entity, such as head yaw and pitch, shooting animation state, and death
 *           animation state.
 *
 * @see BaseModel
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 2.1.0
 */
public abstract class BaseTurretModel<S extends BaseTurretRenderState> extends BaseModel<S> {
	private final Map<UUID, Queue<? extends Keyframe>> shootAnimProcedure = new HashMap<>();
	private final Map<UUID, Queue<? extends Keyframe>> deathAnimProcedure = new HashMap<>();

	/**
	 * Identifies the bone that serves as the neck of the turret so that it can
	 * rotate along the yaw axis. This part is basically the stand of a turret.
	 */
	protected final ModelPart stand;
	/**
	 * Identifies the bone that serves as the head of the turret so that it can
	 * rotate along the pitch axis.
	 */
	protected final ModelPart head;

	/**
	 * The shooting animation of the turret. This is used to render the shooting animation of the
	 * turret when it shoots.
	 */
	protected final Animation shootAnim;

	/**
	 * The death animation of the turret. This is used to render the death animation of the turret
	 * when it dies.
	 */
	protected final Animation deathAnim;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model part,
	 * texture path, and bone names. These will be used by this model to
	 * render the turret entity and apply default configurations.
	 * <br><br>
	 * Using this overloaded constructor will set the shooting and death animation to {@code null}.
	 * Use {@link BaseTurretModel#BaseTurretModel(ModelPart, String, String[], ModelPart, ModelPart, Animation, Animation)}
	 * if a shooting or death, or both animation exists.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture this model will use.
	 * @param textures The file name(s) that this model will use.
	 * @param stand The model part that serves as the stand of the turret. This part will rotate along the yaw axis.
	 * @param head The model part that servesas the head of the turret. This part will rotate along the pitch axis.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseTurretModel(
		@NotNull ModelPart root, @NotNull String texturePath, @NotNull String[] textures,
		@NotNull ModelPart stand, @NotNull ModelPart head
	) {
		this(root, texturePath, textures, stand, head, null, null);
	}

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model part,
	 * texture path, and bone names. These will be used by this model to
	 * render the turret entity and apply default configurations.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture this model will use.
	 * @param textures The file name(s) that this model will use.
	 * @param stand The model part that serves as the stand of the turret. This part will rotate along the yaw axis.
	 * @param head The model part that servesas the head of the turret. This part will rotate along the pitch axis.
	 * @param shootAnim The shoot animation. {@code null} if none.
	 * @param deathAnim The death animation. {@code null} if none.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseTurretModel(
		@NotNull ModelPart root, @NotNull String texturePath, @NotNull String[] textures,
		@NotNull ModelPart stand, @NotNull ModelPart head,
		@Nullable Animation shootAnim, @Nullable Animation deathAnim
	) {
		super(root, texturePath, textures);

		// Set the common model parts used by all turret models
		this.stand = stand;
		this.head = head;

		// Sets the common animation parts that may/not be used by all turret models
		this.shootAnim = shootAnim;
		this.deathAnim = deathAnim;
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	/**
	 * Sets the angles of the model parts based on the render state of the turret entity. This
	 * method is called every frame to update the model's pose and animations according to the
	 * turret's current state, such as its head yaw and pitch, shooting animation state, and death
	 * animation state. The method also applies the appropriate animations to the model parts based
	 * on the turret's shooting and death animation states. This allows the turret model to visually
	 * reflect the actions and status of the turret entity in the game, such as aiming at targets
	 * and playing shooting or death animations when necessary.
	 * <br><br>
	 * When more animations are needed to be rendered, the method could be freely overriden so long
	 * as the super method ({@code super.setAngles(state)}) must be called so that all base
	 * rendering logic could still be called, preventing visual bugs or glitches that may occur.
	 *
	 * @param state The current state of the turret.
	 */
	@Override
	public void setAngles(S state) {
		super.setAngles(state);

		// HEAD ANGLE HANDLING
		float headYaw = state.relativeHeadYaw + state.bodyYaw + 180;
		float headPitch = state.hasTarget ? state.pitch : -this.getDefaultHeadPitch();

		this.setHeadAngles(headYaw, headPitch);

		// ANIMATION HANDLING (& ADDITIONAL PROCEDURES)
		if (this.shootAnim != null) {
			if (this.getShootAnimProcedure(state.id) == null &&
				state.shootAnimationState.getTimeInMilliseconds(state.age) > 0 &&
				!state.shootAnimationState.isRunning()
			) {
				this.setShootAnimProcedure(state.id, this.getShootAnimProcedureInstance());
			}

			this.shootAnim.apply(state.shootAnimationState, state.age);
			this.additionalShootAnimProcedures(state.shootAnimationState, state);
		}
		else {
			this.setShootAnimProcedure(state.id, null);
		}

		if (this.deathAnim != null) {
			if (this.getDeathAnimProcedure(state.id) == null &&
				state.deathAnimationState.getTimeInMilliseconds(state.age) > 0 &&
				!state.deathAnimationState.isRunning()
			) {
				this.setDeathAnimProcedure(state.id, this.getDeathAnimProcedureInstance());
			}

			this.deathAnim.apply(state.deathAnimationState, state.age);
			this.additionalDeathAnimProcedures(state.deathAnimationState, state);
		}
		else {
			this.setDeathAnimProcedure(state.id, null);
		}

		// Garbage Collector
		Queue<? extends Keyframe> deathProcedure = this.getDeathAnimProcedure(state.id);
		if (state.dead && (deathProcedure == null || deathProcedure.isEmpty())) {
			this.setShootAnimProcedure(state.id, null);
			this.setDeathAnimProcedure(state.id, null);
		}
	}

	/**
	 * Sets the look angle of the turret's head and stand, allowing the turret to accurately render
	 * its model to aim on its target. The head will rotate along the pitch axis while the stand
	 * will rotate along the yaw axis, simulating a turret's rotational origin.
	 *
	 * @param headYaw The horizontal angle the turret should rotate to. This is used to determine the yaw rotation of the stand.
	 * @param headPitch The vertical angle the turret should rotate to. This is used to determine the pitch rotation of the head.
	 */
	protected void setHeadAngles(float headYaw, float headPitch) {
		headPitch = MathHelper.clamp(
			headPitch,
			-this.getMaxPitch(),
			-this.getMinPitch()
		);

		this.stand.yaw = headYaw * ((float)Math.PI / 180F);
		this.head.pitch = headPitch * ((float)Math.PI / 180F);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	protected void setShootAnimProcedure(UUID id, @Nullable Queue<? extends Keyframe> queue) {
		if (queue == null) {
			this.shootAnimProcedure.remove(id);
		}
		else {
			this.shootAnimProcedure.put(id, queue);
		}
	}

	@Nullable
	protected final Queue<? extends Keyframe> getShootAnimProcedure(UUID id) {
		return this.shootAnimProcedure.get(id);
	}

	protected void setDeathAnimProcedure(UUID id, @Nullable Queue<? extends Keyframe> queue) {
		if (queue == null) {
			this.deathAnimProcedure.remove(id);
		}
		else {
			this.deathAnimProcedure.put(id, queue);
		}
	}

	@Nullable
	protected final Queue<? extends Keyframe> getDeathAnimProcedure(UUID id) {
		return this.deathAnimProcedure.get(id);
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	/**
	 * Returns a queue of keyframes that defines the shooting animation of the turret. This method
	 * is called whenever the turret starts shooting and the shooting animation needs to be applied
	 * to the model parts.
	 *
	 * @return {@code Queue<? extends Keyframe>} a queue of keyframes.
	 */
	public Queue<? extends Keyframe> getShootAnimProcedureInstance() {
		return new PriorityQueue<>();
	}

	/**
	 * Returns a queue of keyframes that defines the death animation of the turret. This method is
	 * called whenever the turret dies and the death animation needs to be applied to the model parts.
	 *
	 * @return {@code Queue<? extends Keyframe>} a queue of keyframes.
	 */
	public Queue<? extends Keyframe> getDeathAnimProcedureInstance() {
		return new PriorityQueue<>();
	}

	/**
	 * Defines additional procedures to be executed during the shooting animation. This method is called
	 * after the shooting animation is applied to the model parts, allowing for any additional
	 * transformations or effects to be applied to the model during the shooting animation. This can be
	 * used to add extra visual flair or effects to the turret's shooting animation, such as adding
	 * particle effects, changing the model's color, or applying additional transformations to the
	 * model parts to enhance the visual impact of the turret's shooting.<br>
	 * <br>
	 * By default, this method does nothing, but it can be overridden in subclasses to implement
	 * custom behavior during the shooting animation.
	 *
	 * @param animState The shooting animation state that is running.
	 * @param state	 The current state of the turret.
	 */
	protected void additionalShootAnimProcedures(AnimationState animState, S state) {
		long ms = animState.getTimeInMilliseconds(state.age);
		UUID id = state.id;
		Queue<? extends Keyframe> procedure = this.getShootAnimProcedure(id);
		Keyframe keyframe = procedure == null ? null : procedure.peek();

		if (animState.isRunning() && keyframe != null) {
			if (keyframe.getTimeMS() <= ms) {
				keyframe.apply(animState, state);
				procedure.remove();
			}
		}

		if (procedure != null && procedure.isEmpty()) {
			this.setShootAnimProcedure(id, null);
		}
	}

	/**
	 * Defines additional procedures to be executed during the death animation. This method is called
	 * after the death animation is applied to the model parts, allowing for any additional
	 * transformations or effects to be applied to the model during the death animation. This can be
	 * used to add extra visual flair or effects to the turret's death animation, such as adding
	 * particle effects, changing the model's color, or applying additional transformations to the
	 * model parts to enhance the visual impact of the turret's death.<br>
	 * <br>
	 * By default, this method does nothing, but it can be overridden in subclasses to implement
	 * custom behavior during the death animation.
	 *
	 * @param animState The death animation state that is running.
	 * @param state	 The current state of the turret.
	 */
	protected void additionalDeathAnimProcedures(AnimationState animState, S state) {
		long ms = animState.getTimeInMilliseconds(state.age);
		UUID id = state.id;
		Queue<? extends Keyframe> procedure = this.getDeathAnimProcedure(id);
		Keyframe keyframe = procedure == null ? null : procedure.peek();

		if (animState.isRunning() && keyframe != null) {
			if (keyframe instanceof FXKeyframe fxKeyframe) {
				if (fxKeyframe.getTimeMS() <= ms) {
					fxKeyframe.apply(animState, state);
					procedure.remove();
				}
			}
		}

		if (procedure != null && procedure.isEmpty()) {
			this.setDeathAnimProcedure(id, null);
		}
	}

	/**
	 * Defines the default head pitch the turret uses. This is to render the model
	 * with their guns in their resting pose. A good example is the {@link AATurretModel} which
	 * has a 30 degree tilt upward when in rest, allowing the turret to portray the constant state
	 * of monitoring the air for targets.
	 *
	 * @return The default head pitch in degrees. By default, this is 0, meaning the turret will be
	 * rendered with its gun parallel to the ground when in rest.
	 *
	 * @see AATurretModel#getDefaultHeadPitch()
	 */
	protected float getDefaultHeadPitch() {
		return 0;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	/**
	 * Gets the minimum pitch angle that the head of this turret can rotate to. This is used to
	 * determine the minimum pitch the turret's head can depress to.
	 *
	 * @return The minimum pitch angle in degrees.
	 *
	 * @see #setHeadAngles(float, float)
	 */
	protected abstract float getMinPitch();

	/**
	 * Gets the maximum pitch angle that the head of this turret can rotate to. This is used to
	 * determine the maximum pitch the turret's head can elevate to.
	 *
	 * @return The maximum pitch angle in degrees.
	 *
	 * @see #setHeadAngles(float, float)
	 */
	protected abstract float getMaxPitch();
}
