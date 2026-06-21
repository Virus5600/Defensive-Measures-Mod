package com.virus5600.defensive_measures.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.network.serverbound.entity.StopAnimationPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public abstract class BaseTurretRenderer<
	T extends TurretEntity,
	S extends BaseTurretRenderState,
	M extends BaseTurretModel<S>
	> extends MobRenderer<T, S, M> {

	private final Supplier<S> renderStateFactory;
	private boolean deathRotEnabled = false;
	private boolean lookControlOnDeath = false;

	public BaseTurretRenderer(
		EntityRendererProvider.Context context,
		M entityModel,
		Supplier<S> renderStateFactory
	) {
		this(context, entityModel, 0.5F, renderStateFactory);
	}

	public BaseTurretRenderer(
		EntityRendererProvider.Context context,
		M entityModel,
		float shadowRadius,
		Supplier<S> renderStateFactory
	) {
		super(context, entityModel, shadowRadius);

		this.renderStateFactory = renderStateFactory;
	}

	@Override @NonNull
	public Identifier getTextureLocation(@NonNull S state) {
		Identifier texture = this.getModel().getBaseTexture();
		Identifier[] textureList = this.getModel().getTextures();

		int index = state.turretLvl - 1;
		boolean outOfBounds = index < 0 || index >= this.getModel().getTextures().length;

		// If the list and base textures are both null, or the texture list alone is empty or null,
		// or if the index is out of bounds, return an empty identifier to prevent crashes.
		if ((textureList == null && texture == null) ||
			textureList == null ||
			textureList.length == 0 ||
			outOfBounds
		) {
			return Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "");
		}
		texture = textureList[index];

		return texture;
	}

	@Override
	public @NonNull S createRenderState() {
		return renderStateFactory.get();
	}

	@Override
	public void extractRenderState(@NonNull T turretEntity, @NonNull S turretRenderState, float tickProgress) {
		super.extractRenderState(turretEntity, turretRenderState, tickProgress);

		turretRenderState.isSettingUp = turretEntity.isSettingUp();
		turretRenderState.isTearingDown = turretEntity.isTearingDown();

		// Handles all common turret animations.
		this.handleAnimations(turretEntity, turretRenderState, tickProgress);

		turretRenderState.eyePos = turretEntity.getEyePosition();
		turretRenderState.currentBarrelPos = turretEntity.getCurrentBarrel(false);

		turretRenderState.id = turretEntity.getUUID();
		turretRenderState.turretLvl = turretEntity.getTrackedLevel();
		turretRenderState.hasTarget = turretEntity.hasTarget();
		turretRenderState.isLockedButNotAttacking = turretEntity.getTrackedLockedButNotAttacking();
		turretRenderState.shooting = turretEntity.getTrackedShooting();

		turretRenderState.dead = turretEntity.isDeadOrDying();
		turretRenderState.hasRedOverlay = turretEntity.hurtTime > 0 && turretEntity.isAlive();

		// Death look-control handling
		if (!this.lookControlOnDeath && !turretEntity.isAlive()) {
			float idlePitch = turretEntity.getXRot();

			if (turretEntity.getIdlePitch().isPresent()) {
				idlePitch = turretEntity.getIdlePitch().get();
			}

			turretRenderState.xRot = idlePitch;
		}
	}

	@Override
	protected void setupRotations(@NonNull S turretState, @NonNull PoseStack matrices, float bodyYaw, float baseHeight) {
		if (this.deathRotEnabled) {
			super.setupRotations(turretState, matrices, bodyYaw, baseHeight);
			return;
		}

		// Keeps the flipping easter-egg or feature.
		if (turretState.isUpsideDown) {
			matrices.translate(0.0F, (turretState.boundingBoxHeight + 0.1F) / baseHeight, 0.0F);
			matrices.mulPose(Axis.ZP.rotationDegrees(180.0F));
		}
	}

	/**
	 * Handles all existing common turret animations, allowing them to automatically stop when it
	 * reach its end.
	 *
	 * @param turretEntity      The turret entity being rendered.
	 * @param turretRenderState The render state of the turret, which will be modified to reflect the current animation states.
	 * @param tickProgress      The tick progress for interpolation.
	 */
	protected void handleAnimations(@NonNull T turretEntity, @NonNull S turretRenderState, float tickProgress) {
		BaseTurretModel<S> tModel = this.model;

		List<AnimationRecord<S>> entries = List.of(
			new AnimationRecord<S>(
				turretEntity.getIdleAnimationState(),
				(long)(tModel.idleAnimLen * 1000), "idle",
				_ -> turretRenderState.idleAnimationState.copyFrom(turretEntity.getIdleAnimationState())
			),
			new AnimationRecord<S>(
				turretEntity.getShootAnimationState(),
				(long)(tModel.shootAnimLen * 1000), "shoot",
				_ -> turretRenderState.shootAnimationState.copyFrom(turretEntity.getShootAnimationState())
			),
			new AnimationRecord<S>(
				turretEntity.getDeathAnimationState(),
				(long)(tModel.deathAnimLen * 1000), "death",
				_ -> turretRenderState.deathAnimationState.copyFrom(turretEntity.getDeathAnimationState())
			),
			new AnimationRecord<S>(
				turretEntity.getSetupAnimationState(),
				(long)(tModel.setupAnimLen * 1000), "setup",
				_ -> turretRenderState.setupAnimationState.copyFrom(turretEntity.getSetupAnimationState())
			),
			new AnimationRecord<S>(
				turretEntity.getTeardownAnimationState(),
				(long)(tModel.teardownAnimLen * 1000), "teardown",
				_ -> turretRenderState.teardownAnimationState.copyFrom(turretEntity.getTeardownAnimationState())
			)
		);

		for (AnimationRecord<S> entry : entries) {
			long progress = entry.state().getTimeInMillis(turretEntity.tickCount);
			boolean done = progress >= entry.durationMs;

			if (done && entry.state().isStarted()) {
				entry.state().stop();
				ClientPlayNetworking.send(new StopAnimationPacket(turretEntity.getId(), entry.animName()));
			}
			else {
				entry.copyToRenderState().accept(turretRenderState);
			}
		}
	}

	// ////////////// //
	// CUSTOM METHODS //
	// ////////////// //

	// DEATH RELATED //

	// DEATH ROTATION
	protected void setDeathRotation(boolean deathRotationEnabled) {
		this.deathRotEnabled = deathRotationEnabled;
	}

	protected void enableDeathRotation() {
		this.deathRotEnabled = true;
	}

	protected void disableDeathRotation() {
		this.deathRotEnabled = false;
	}

	protected boolean toggleDeathRotation() {
		this.deathRotEnabled = !this.deathRotEnabled;
		return this.deathRotEnabled;
	}

	protected boolean isDeathRotationEnabled() {
		return this.deathRotEnabled;
	}

	// LOOK CONTROL
	protected void enableLookControlOnDeath() {
		this.lookControlOnDeath = true;
	}

	protected void disableLookControlOnDeath() {
		this.lookControlOnDeath = false;
	}

	protected boolean toggleLookControlOnDeath() {
		this.lookControlOnDeath = !this.lookControlOnDeath;
		return this.lookControlOnDeath;
	}

	protected boolean isLookControlOnDeathEnabled() {
		return this.lookControlOnDeath;
	}

	// //////////// //
	// LOCAL RECORD //
	// //////////// //

	/**
	 * Stores an entry of an animation state, allowing it to be easily handled in a list and to
	 * reduce code duplication when handling animations.
	 *
	 * @param state             The animation state of the turret entity, which will be used to check the progress and whether it should be stopped.
	 * @param durationMs        The duration of the animation in milliseconds, which will be used to check whether the animation has reached its end.
	 * @param animName          The name of the animation, which will be used in the stop packet to identify which animation should be stopped.
	 * @param copyToRenderState A consumer that copies the animation state to the render state, which will be used to update the render state with the current animation state of the turret entity.
	 * @param <S>               The type of the render state, which is used to ensure type safety when copying the animation state to the render state.
	 */
	protected record AnimationRecord<S extends BaseTurretRenderState>(
		AnimationState state,
		long durationMs, String animName,
		Consumer<S> copyToRenderState
	) { }
}
