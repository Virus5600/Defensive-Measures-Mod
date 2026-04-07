package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.util.math.MathHelper;

import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.model.BaseModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 2.1.0
 */
public abstract class BaseTurretModel<S extends BaseTurretRenderState> extends BaseModel<S> {
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
		this.setHeadAngles(headYaw, state.pitch);

		// ANIMATION HANDLING
		if (this.shootAnim != null) {
			this.shootAnim.apply(state.shootAnimationState, state.age);
		}

		if (this.deathAnim != null) {
			this.deathAnim.apply(state.deathAnimationState, state.age);
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
			this.getMinPitch(),
			this.getMaxPitch()
		);

		this.stand.yaw = headYaw * ((float)Math.PI / 180F);
		this.head.pitch = headPitch * ((float)Math.PI / 180F);
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
