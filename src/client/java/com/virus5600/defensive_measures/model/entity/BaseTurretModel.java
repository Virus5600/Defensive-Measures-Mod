package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.animations.Keyframe;
import com.virus5600.defensive_measures.animations.entity.CommonTurretAnimation;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.model.BaseModel;
import com.virus5600.defensive_measures.model.entity.tier1.CannonTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
 */
public abstract class BaseTurretModel<S extends BaseTurretRenderState> extends BaseModel<S> {
	private final Map<UUID, Queue<? extends Keyframe>> shootAnimProcedure = new HashMap<>();
	private final Map<UUID, Queue<? extends Keyframe>> deathAnimProcedure = new HashMap<>();

	/**
	 * Identifies the bone that serves as the neck of the turret so that it can
	 * rotate along the yaw axis. This part is basically the stand of a turret.
	 */
	protected final ModelPart neck;
	/**
	 * Identifies the bone that serves as the head of the turret so that it can
	 * rotate along the pitch axis.
	 */
	protected final ModelPart head;

	/**
	 * The idle animation that gets randomly played by the turret.
	 */
	protected final KeyframeAnimation idleAnim;

	/**
	 * The shooting animation of the turret. This is used to render the shooting animation of the
	 * turret when it shoots.
	 */
	protected final KeyframeAnimation shootAnim;

	/**
	 * The death animation of the turret. This is used to render the death animation of the turret
	 * when it dies.
	 */
	protected final KeyframeAnimation deathAnim;

	/**
	 * A selected setup animation from one of the map entries from {@link #setupAnims}. This is
	 * used to render the setup animation of the turret when it is being placed or spawned.
	 */
	protected final KeyframeAnimation setupAnim;

	/**
	 * A selected teardown animation from one of the map entries from {@link #teardownAnims}. This
	 * is used to render the teardown animation of the turret when it is being taken (using the
	 * {@link com.virus5600.defensive_measures.item.equipments.TurretRemoverItem Turret Remover}).
	 */
	protected final KeyframeAnimation teardownAnim;

	/**
	 * The setup animations that this turret can play when placed or spawned.
	 */
	protected final Map<KeyframeAnimation, Float> setupAnims;
	/**
	 * The teardown animations that this turret can play when taken (using the
	 * {@link com.virus5600.defensive_measures.item.equipments.TurretRemoverItem Turret Remover}).
	 */
	protected final Map<KeyframeAnimation, Float> teardownAnims;

	public final float idleAnimLen;
	public final float shootAnimLen;
	public final float deathAnimLen;
	public final float setupAnimLen;
	public final float teardownAnimLen;

	/**
	 * An ID assigned to a (this) model to allow a stable randomization, allowing reusability of
	 * randomized values every instance.
	 */
	public final UUID modelId = UUID.randomUUID();

	protected final static Queue<? extends Keyframe> DEFAULT_EMPTY_KEYFRAME = new PriorityQueue<>() {
		{
			add(
				new Keyframe() {
					@Override public int compareTo(@NotNull Keyframe o) { return 0; }
					@Override public double getTime() { return 0; }
					@Override public void apply(AnimationState animState, EntityRenderState state) { }
					@Override public int getTimeMS() { return 0; }
				}
			);
		}
	};

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model part,
	 * texture path, and bone names. These will be used by this model to
	 * render the turret entity and apply default configurations.
	 * <br><br>
	 * Using this overloaded constructor will set the shooting and death animation to {@code null}.
	 * Use {@link BaseTurretModel#BaseTurretModel(ModelPart, String, String[], ModelPart, ModelPart, AnimationDefinition, AnimationDefinition, AnimationDefinition[], AnimationDefinition[], float)}
	 * if a shooting or death, or both animation exists.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture this model will use.
	 * @param textures The file name(s) that this model will use.
	 * @param neck The model part that serves as the neck of the turret. This part will rotate along the yaw axis.
	 * @param head The model part that servesas the head of the turret. This part will rotate along the pitch axis.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseTurretModel(
            @NotNull ModelPart root, @NotNull String texturePath, @NotNull String[] textures,
            @NotNull ModelPart neck, @NotNull ModelPart head,
			float height
	) {
		this(
			root, texturePath, textures, neck, head,
			null, null,
			null, null,
			height
		);
	}

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model part,
	 * texture path, and bone names. These will be used by this model to
	 * render the turret entity and apply default configurations.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture this model will use.
	 * @param textures The file name(s) that this model will use.
	 * @param neck The model part that serves as the neck of the turret. This part will rotate along the yaw axis.
	 * @param head The model part that servesas the head of the turret. This part will rotate along the pitch axis.
	 * @param shootAnim The shoot animation. {@code null} if none.
	 * @param deathAnim The death animation. {@code null} if none.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseTurretModel(
		@NotNull ModelPart root, @NotNull String texturePath, @NotNull String[] textures,
		@NotNull ModelPart neck, @NotNull ModelPart head,
		@Nullable AnimationDefinition shootAnim, @Nullable AnimationDefinition deathAnim,
		float height
	) {
		this(
			root, texturePath, textures, neck, head,
			shootAnim, deathAnim,
			null, null,
			height
		);
	}

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model part,
	 * texture path, and bone names. These will be used by this model to
	 * render the turret entity and apply default configurations.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture this model will use.
	 * @param textures The file name(s) that this model will use.
	 * @param neck The model part that serves as the neck of the turret. This part will rotate along the yaw axis.
	 * @param head The model part that servesas the head of the turret. This part will rotate along the pitch axis.
	 * @param shootAnim The shoot animation. {@code null} if none.
	 * @param deathAnim The death animation. {@code null} if none.
	 * @param setupAnims The setup animations. {@code null} if none.
	 * @param teardownAnims The teardown animations. {@code null} if none.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseTurretModel(
            @NotNull ModelPart root, @NotNull String texturePath, @NotNull String[] textures,
            @NotNull ModelPart neck, @NotNull ModelPart head,
            @Nullable AnimationDefinition shootAnim, @Nullable AnimationDefinition deathAnim,
			@Nullable AnimationDefinition[] setupAnims, @Nullable AnimationDefinition[] teardownAnims,
			float height
	) {
		this(
			root, texturePath, textures, neck, head,
			shootAnim, deathAnim,
			setupAnims, teardownAnims, null,
			height
		);
	}

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model part,
	 * texture path, and bone names. These will be used by this model to
	 * render the turret entity and apply default configurations.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture this model will use.
	 * @param textures The file name(s) that this model will use.
	 * @param neck The model part that serves as the neck of the turret. This part will rotate along the yaw axis.
	 * @param head The model part that servesas the head of the turret. This part will rotate along the pitch axis.
	 * @param shootAnim The shoot animation. {@code null} if none.
	 * @param deathAnim The death animation. {@code null} if none.
	 * @param setupAnims The setup animations. {@code null} if none.
	 * @param idleAnim The idle animation. {@code null} if none.
	 * @param teardownAnims The teardown animations. {@code null} if none.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseTurretModel(
		@NotNull ModelPart root, @NotNull String texturePath, @NotNull String[] textures,
		@NotNull ModelPart neck, @NotNull ModelPart head,
		@Nullable AnimationDefinition shootAnim, @Nullable AnimationDefinition deathAnim,
		@Nullable AnimationDefinition[] setupAnims, @Nullable AnimationDefinition[] teardownAnims,
		@Nullable AnimationDefinition idleAnim,
		float height
	) {
		super(root, texturePath, textures);

		// Adds 25% more height for safety measures.
		height *= 1.25F;

		// Set the common model parts used by all turret models
		this.neck = neck;
		this.head = head;

		// Identify the procedures' length
		Keyframe lastKF;
		lastKF = this.getShootAnimProcedureInstance()
			.stream()
			.max(Comparator.comparingDouble(Keyframe::getTime))
			.orElse(null);
		int shootProcLenMS = lastKF == null ? 0 : lastKF.getTimeMS();

		lastKF = this.getDeathAnimProcedureInstance()
			.stream()
			.max(Comparator.comparingDouble(Keyframe::getTime))
			.orElse(null);
		int deathProcLenMS = lastKF == null ? 0 : lastKF.getTimeMS();

		// Stores the animation lengths first...
		this.idleAnimLen = idleAnim == null ? 0 : idleAnim.lengthInSeconds();
		this.shootAnimLen = shootAnim == null ? shootProcLenMS : shootAnim.lengthInSeconds();
		this.deathAnimLen = deathAnim == null ? deathProcLenMS : deathAnim.lengthInSeconds();

		// Sets the common animation parts that may/not be used by all turret models
		this.idleAnim = idleAnim != null ?
			idleAnim.bake(root) : null;

		this.shootAnim = shootAnim != null ?
			shootAnim.bake(root) :
			AnimationDefinition.Builder
				.withLength(this.shootAnimLen / 1000)
				.build()
				.bake(root);

		this.deathAnim = deathAnim != null ?
			deathAnim.bake(root) :
			AnimationDefinition.Builder
				.withLength(this.deathAnimLen / 1000)
				.build()
				.bake(root);

		// Special cases - Setup & Teardown animations
		this.setupAnims = setupAnims == null || setupAnims.length == 0 ?
			Stream.of(
				CommonTurretAnimation.createPopUpSetupAnimation(root, height),
				CommonTurretAnimation.createScaleUpSetupAnimation(root)
			).collect(Collectors.toMap(
				anim -> anim.bake(root),
				AnimationDefinition::lengthInSeconds
			))
			: Stream.of(setupAnims).filter(Objects::nonNull)
				.collect(Collectors.toMap(
					anim -> anim.bake(root),
					AnimationDefinition::lengthInSeconds
				));

		this.teardownAnims = teardownAnims == null || teardownAnims.length == 0 ?
			Stream.of(
				CommonTurretAnimation.createPopDownTeardownAnimation(root, height),
				CommonTurretAnimation.createScaleDownAnimation(root)
			).collect(Collectors.toMap(
				anim -> anim.bake(root),
				AnimationDefinition::lengthInSeconds
			))
			: Stream.of(teardownAnims).filter(Objects::nonNull)
			  .collect(Collectors.toMap(
				  anim -> anim.bake(root),
				  AnimationDefinition::lengthInSeconds
			  ));

		// Finalize setup and teardown selection
		this.setupAnim = (KeyframeAnimation) this.setupAnims.keySet().toArray()[getRandomAnimation(this.setupAnims, this.modelId)];
		this.teardownAnim = (KeyframeAnimation) this.teardownAnims.keySet().toArray()[getRandomAnimation(this.teardownAnims, this.modelId)];

		this.setupAnimLen = this.setupAnims.get(this.setupAnim);
		this.teardownAnimLen = this.teardownAnims.get(this.teardownAnim);
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
	public void setupAnim(@NonNull S state) {
		super.setupAnim(state);

		// Play the setup animation if the state says we are setting up
		if (state.isSettingUp && this.setupAnims != null && this.setupAnims.isEmpty()) {
			this.setupAnim.apply(
				state.setupAnimationState,
				state.ageInTicks
			);
		}

		// Play the teardown animation if the state says we are tearing down
		if (state.isTearingDown && this.teardownAnims != null && !this.teardownAnims.isEmpty()) {
			if (state.teardownAnimationState.isStarted()) {
				this.teardownAnim.apply(
					state.teardownAnimationState,
					state.ageInTicks
				);
			}
		}

		// HEAD ANGLE HANDLING
		float headYaw = state.yRot + state.bodyRot + 180;
		float headPitch = state.xRot;

		// ANIMATION HANDLING (& ADDITIONAL PROCEDURES)
		// Only though if the turret isn't being set up or torn down.
		if (!(state.isSettingUp || state.isTearingDown)) {
			this.handleShootAnimation(state);
			this.handleDeathAnimation(state);
		}

		// Only set head angles while the animation isn't started yet or if the head angle isn't at 0° yet when the animations are to be played.
		boolean isFacingNorth = Math.abs(Mth.wrapDegrees(headYaw)) == 0F;
		boolean setupOrTeardownPlaying = state.setupAnimationState.isStarted() || state.teardownAnimationState.isStarted();
		if (!setupOrTeardownPlaying || !isFacingNorth) {
			this.setHeadAngles(headYaw, headPitch);
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
		headPitch = Mth.clamp(
			headPitch,
			-this.getMaxPitch(),
			-this.getMinPitch()
		);

		this.neck.yRot = MathUtil.degToRad(headYaw);
		this.head.xRot = MathUtil.degToRad(headPitch);
	}

	/**
	 * Handles the shooting animation for the turret.
	 *
	 * @param state The current state of the turret.
	 */
	protected final void handleShootAnimation(@NonNull S state) {
		// If there's a shoot animation...
		if (this.shootAnim != null) {
			long animProgress = state.shootAnimationState.getTimeInMillis(state.ageInTicks);

			boolean shootAnimProcedureNull = this.getShootAnimProcedure(state.id) == null;
			boolean shootAnimDurNotZero = animProgress > 0;
			boolean shootAnimStarted = state.shootAnimationState.isStarted();

			// If the shooting anim procedure is null, the current animation progress not zero, and
			// the animation isn't started yet, set the animation procedure to track
			if (shootAnimProcedureNull && shootAnimDurNotZero && !shootAnimStarted) {
				this.setShootAnimProcedure(state.id, this.getShootAnimProcedureInstance());
			}

			// If the shooting animation is started, apply states to the shoot animation and play
			// the procedures in the shoot animation procedure queue.
			if (shootAnimStarted) {
				this.shootAnim.apply(state.shootAnimationState, state.ageInTicks);
				this.playShootAnimProcedures(state.shootAnimationState, state);
			}

			// If the animation reached its end, stop the animation state.
			if (animProgress >= (this.shootAnimLen * 1000)) {
				state.shootAnimationState.stop();
			}
		}
		// Though, if there're none, we can't play the procedure as well so forcefully set this
		// entity's procedure to none
		else {
			this.setShootAnimProcedure(state.id, null);
		}
	}

	/**
	 * Handles the death animation for the turret.
	 *
	 * @param state The current state of the turret.
	 */
	protected final void handleDeathAnimation(@NonNull S state) {
		// If there's a death animation...
		if (this.deathAnim != null) {
			long animProgress = state.deathAnimationState.getTimeInMillis(state.ageInTicks);

			boolean deathAnimProcedureNull = this.getDeathAnimProcedure(state.id) == null;
			boolean deathAnimDurNotZero = animProgress > 0;
			boolean deathAnimStarted = state.deathAnimationState.isStarted();

			// If the death anim procedure is null, the current animation progress not zero, and
			// the animation isn't started yet, set the animation procedure to track
			if (deathAnimProcedureNull && deathAnimDurNotZero && !deathAnimStarted) {
				this.setDeathAnimProcedure(state.id, this.getDeathAnimProcedureInstance());
			}

			// If the death animation is started, apply states to the death animation and play
			// the procedures in the dearg animation procedure queue.
			if (deathAnimStarted) {
				this.deathAnim.apply(state.deathAnimationState, state.ageInTicks);
				this.playDeathAnimProcedures(state.deathAnimationState, state);
			}

			// If the animation reached its end, stop the animation state.
			if (animProgress >= (this.deathAnimLen * 1000)) {
				state.deathAnimationState.stop();
			}
		}
		// Though, if there're none, we can't play the procedure as well so forcefully set this
		// entity's procedure to none
		else {
			this.setDeathAnimProcedure(state.id, null);
		}
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	/**
	 * Sets the shoot animation procedure for an entity via its entity ID. This is used to set the
	 * procedure that will get played when the shooting animation is played, allowing tracking of
	 * the procedure's progress.
	 * <br><br>
	 * Setting the {@code queue} parameter to a non-null value will set the procedure for the
	 * entity with the specified ID, while setting it to {@code null} will remove the currently
	 * assigned procedure to the entity. Do note that providing a non-null value to an entity that
	 * already has a procedure will overwrite said procedure, thus reseting its progress.
	 *
	 * @param id    The entity ID of the turret entity to set the shoot animation procedure for.
	 * @param queue The shoot animation procedure to set for the turret entity. Setting this to {@code null} will remove the currently assigned procedure.
	 *
	 * @see #getShootAnimProcedure(UUID)
	 */
	protected void setShootAnimProcedure(UUID id, @Nullable Queue<? extends Keyframe> queue) {
		if (queue == null) {
			this.shootAnimProcedure.remove(id);
		}
		else {
			this.shootAnimProcedure.put(id, queue);
		}
	}

	/**
	 * Gets the shoot animation procedure (if one was ever set) for an entity via its entity ID.
	 * This is used to get the procedure that will get played when the shooting animation is played,
	 * allowing tracking of the procedure's progress.
	 *
	 * @param id  The entity ID of the turret entity to get the shoot animation procedure for.
	 *
	 * @return The shoot animation procedure assigned to the turret entity with the specified ID, or {@code null} if no procedure is assigned.
	 *
	 * @see #setShootAnimProcedure(UUID, Queue)
	 */
	@Nullable
	protected final Queue<? extends Keyframe> getShootAnimProcedure(UUID id) {
		return this.shootAnimProcedure.get(id);
	}

	/**
	 * Sets the death animation procedure for an entity via its entity ID. This is used to set the
	 * procedure that will get played when the death animation is played, allowing tracking of
	 * the procedure's progress.
	 * <br><br>
	 * Setting the {@code queue} parameter to a non-null value will set the procedure for the
	 * entity with the specified ID, while setting it to {@code null} will remove the currently
	 * assigned procedure to the entity. Do note that providing a non-null value to an entity that
	 * already has a procedure will overwrite said procedure, thus reseting its progress.
	 *
	 * @param id    The entity ID of the turret entity to set the shoot animation procedure for.
	 * @param queue The death animation procedure to set for the turret entity. Setting this to {@code null} will remove the currently assigned procedure.
	 *
	 * @see #getDeathAnimProcedure(UUID)
	 */
	protected void setDeathAnimProcedure(UUID id, @Nullable Queue<? extends Keyframe> queue) {
		if (queue == null) {
			this.deathAnimProcedure.remove(id);
		}
		else {
			this.deathAnimProcedure.put(id, queue);
		}
	}

	/**
	 * Gets the death animation procedure (if one was ever set) for an entity via its entity ID.
	 * This is used to get the procedure that will get played when the death animation is played,
	 * allowing tracking of the procedure's progress.
	 *
	 * @param id  The entity ID of the turret entity to get the death animation procedure for.
	 *
	 * @return The death animation procedure assigned to the turret entity with the specified ID, or {@code null} if no procedure is assigned.
	 *
	 * @see #setDeathAnimProcedure(UUID, Queue)
	 */
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
	 * Defines additional procedures to be executed during the shooting animation.
	 * <br><br>
	 * This method is called after the shooting animation is applied to the model parts, allowing
	 * for any additional transformations or effects to be applied to the model during the shooting
	 * animation. This can be used to add extra visual flair or effects to the turret's shooting
	 * animation, such as adding particle effects, changing the model's color, or applying
	 * additional transformations to the model parts to enhance the visual impact of the turret's
	 * shooting.
	 * <br><br>
	 * Once called, this method plays the procedures in the shoot animation procedure queue one by
	 * one based on their timing, allowing for precise timing of the procedures during the shooting
	 * animation. The procedures will be removed from the queue once they are played, and the queue
	 * will be set to {@code null} once all procedures are played to prevent unnecessary processing
	 * in subsequent frames.
	 *
	 * @param animState The shooting animation state that is running.
	 * @param state	 The current state of the turret.
	 */
	protected void playShootAnimProcedures(AnimationState animState, S state) {
		long ms = animState.getTimeInMillis(state.ageInTicks);
		UUID id = state.id;
		Queue<? extends Keyframe> procedure = this.getShootAnimProcedure(id);
		Keyframe keyframe = procedure == null ? null : procedure.peek();

		if (animState.isStarted() && keyframe != null) {
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
	 * Defines additional procedures to be executed during the death animation.
	 * <br><br>
	 * This method is called after the death animation is applied to the model parts, allowing
	 * for any additional transformations or effects to be applied to the model during the death
	 * animation. This can be used to add extra visual flair or effects to the turret's death
	 * animation, such as adding particle effects, changing the model's color, or applying
	 * additional transformations to the model parts to enhance the visual impact of the turret's
	 * death.
	 * <br><br>
	 * Once called, this method plays the procedures in the death animation procedure queue one by
	 * one based on their timing, allowing for precise timing of the procedures during the death
	 * animation. The procedures will be removed from the queue once they are played, and the queue
	 * will be set to {@code null} once all procedures are played to prevent unnecessary processing
	 * in subsequent frames.
	 *
	 * @param animState The death animation state that is running.
	 * @param state	 The current state of the turret.
	 */
	protected void playDeathAnimProcedures(AnimationState animState, S state) {
		long ms = animState.getTimeInMillis(state.ageInTicks);
		UUID id = state.id;
		Queue<? extends Keyframe> procedure = this.getDeathAnimProcedure(id);
		Keyframe keyframe = procedure == null ? null : procedure.peek();

		if (animState.isStarted() && keyframe != null) {
			if (keyframe.getTimeMS() <= ms) {
				keyframe.apply(animState, state);
				procedure.remove();
			}
		}

		if (procedure != null && procedure.isEmpty()) {
			this.setDeathAnimProcedure(id, null);
		}
	}

	/**
	 * Selects an index based on the size of the provided animations map and the entity ID. This is
	 * used to randomly select an animation from the provided map of animations, allowing for
	 * variation in the animations played by different turret entities. The selection is based on
	 * a seeded random generator using the least significant bits of the entity ID, ensuring that
	 * the same entity will consistently select the same animation from the map, while different
	 * entities may select different animations, adding visual diversity to the turret entities in
	 * the game.
	 *
	 * @return The index of the selected animation from the provided map of animations.
	 */
	protected static int getRandomAnimation(Map<KeyframeAnimation, Float> animations, UUID entityId) {
		if (animations == null || animations.isEmpty()) {
			return 0;
		}

		Random seededRandom = new Random(entityId.getLeastSignificantBits());
		return seededRandom.nextInt(animations.size());
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
