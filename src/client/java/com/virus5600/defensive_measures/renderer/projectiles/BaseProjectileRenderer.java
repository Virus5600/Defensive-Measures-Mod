package com.virus5600.defensive_measures.renderer.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.entity.projectiles.ExplosiveProjectileEntity;
import com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity;
import com.virus5600.defensive_measures.model.projectiles.BaseProjectileModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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
            EntityRendererProvider.Context context,
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
	public @NonNull S createRenderState() {
		return this.renderStateFactory.get();
	}

	@Override
	public void extractRenderState(@NonNull T entity, @NonNull S state, float tickProgress) {
		super.extractRenderState(entity, state, tickProgress);

		state.loopAnimationState.copyFrom(entity.getLoopAnimationState());

		state.pitch = entity.getXRot(tickProgress);
		state.yaw = entity.getYRot(tickProgress);
	}

	@Override
	public void submit(@NonNull S state, PoseStack stack, @NonNull SubmitNodeCollector queue, @NonNull CameraRenderState camState) {
		stack.pushPose();

		if (this.shouldLookAtDir()) {
			stack.mulPose(Axis.YN.rotationDegrees(state.yaw + 180F));
			stack.mulPose(Axis.XN.rotationDegrees(state.pitch));
		}

		Identifier textureId = this.getTexture(state);
		if (textureId != null) {
			queue.submitModel(
				this.getModel(), state, stack,
				RenderTypes.entityCutout(textureId),
				state.lightCoords,
				OverlayTexture.NO_OVERLAY,
				state.outlineColor,
				null
			);
		}

		stack.popPose();
		super.submit(state, stack, queue, camState);
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
