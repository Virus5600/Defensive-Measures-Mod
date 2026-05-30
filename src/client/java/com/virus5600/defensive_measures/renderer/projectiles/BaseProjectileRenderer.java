package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import com.virus5600.defensive_measures.entity.projectiles.ExplosiveProjectileEntity;
import com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity;
import com.virus5600.defensive_measures.model.projectiles.BaseProjectileModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class BaseProjectileRenderer<
	T extends TurretProjectileEntity,
	S extends BaseProjectileRenderState,
	M extends BaseProjectileModel<S>
	> extends EntityRenderer<T, S> {

	private final Supplier<S> renderStateFactory;
	/** Determines whether this projectile will look at the direction it is going like how an arrow behave. */
	private boolean lookAtDirection = true;
	private final M model;

	public BaseProjectileRenderer(
		EntityRendererFactory.Context context,
		M entityModel,
		float shadowRadius,
		Supplier<S> renderStateFactory
	) {
		super(context);

		this.renderStateFactory = renderStateFactory;
		this.model = entityModel;
		this.shadowRadius = shadowRadius;
	}

	@Override
	public S createRenderState() {
		return this.renderStateFactory.get();
	}

	@Override
	public void updateRenderState(T entity, S state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);

		state.loopAnimationState.copyFrom(entity.getLoopAnimationState());

		state.pitch = -entity.getLerpedPitch(tickProgress);
		state.yaw = entity.getLerpedYaw(tickProgress);

		state.yaw *= entity instanceof ExplosiveProjectileEntity ? -1 : 1;
		state.yaw += entity instanceof ExplosiveProjectileEntity ? 180 : 0;
	}

	@Override
	public void render(S state, MatrixStack stack, OrderedRenderCommandQueue queue, CameraRenderState camState) {
		stack.push();

		if (this.shouldLookAtDir()) {
			stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.pitch));
			stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 180F));
		}

		Identifier textureId = this.getTexture(state);
		if (textureId != null) {
			queue.submitModel(
				this.getModel(), state, stack,
				RenderLayers.entityCutout(textureId),
				state.light,
				OverlayTexture.DEFAULT_UV,
				state.outlineColor,
				null
			);
		}

		stack.pop();
		super.render(state, stack, queue, camState);
	}

	// ////////////// //
	// CUSTOM METHODS //
	// ////////////// //

	public M getModel() {
		return this.model;
	}

	/**
	 * Returns a boolean value identifying whether this projectile should look at the direction
	 * it is moving or not.
	 *
	 * @return {@code true} if should rotate to face, {@code false} otherwise.
	 *
	 * @see #shouldLookAtDir(boolean)
	 */
	public final boolean shouldLookAtDir() {
		return this.lookAtDirection;
	}

	/**
	 * Sets the {@code boolean} value identifying whether this projectile should look at the
	 * direction it is moving or not.
	 *
	 * @param shouldLookAtDir Set to {@code true} if it should rotate, {@code false} otherwise. otherwise.
	 *
	 * @see #shouldLookAtDir()
	 */
	public final void shouldLookAtDir(boolean shouldLookAtDir) {
		this.lookAtDirection = shouldLookAtDir;
	}

	@Nullable
	public Identifier getTexture(S state) {
		return this.getModel().getBaseTexture();
	}
}
