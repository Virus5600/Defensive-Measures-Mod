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
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.projectile.Projectile;

import com.virus5600.defensive_measures.model.BaseModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

/**
 * The base projectile renderer, providing common functionality for rendering different types of
 * projectiles. This allows for centralization of all common logics and rendering for all
 * projectile types. When needed, a class can extend this abstract class to implement its own
 * unique custom logic while just calling methods from this class to implement the common logic
 * across all projectiles.
 *
 * @param <P> The projectile entity for this renderer.
 * @param <S> The render state for this renderer.
 * @param <M> The model for this renderer.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class BaseProjectileRenderer<
	P extends Projectile,
	S extends BaseProjectileRenderState,
	M extends BaseModel<S>
	> extends EntityRenderer<P, S> {
	protected final Supplier<S> renderStateFactory;
	/** Determines whether this projectile will look at the direction it is going like how an arrow behave. */
	protected boolean lookAtDirection = true;
	protected final M model;

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

	// /////// //
	// METHODS //
	// /////// //

	@Override
	public @NonNull S createRenderState() {
		return this.renderStateFactory.get();
	}

	@Override
	public void extractRenderState(@NonNull P entity, @NonNull S state, float tickProgress) {
		super.extractRenderState(entity, state, tickProgress);

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
			boolean isBodyVisible = !state.isInvisible;
			int baseColor = !isBodyVisible ? 0x26FFFFFF : 0xFFFFFFFF;
			int tintedColor = ARGB.multiply(baseColor, this.getModelTint(state));

			queue.submitModel(
				this.getModel(), state, stack,
				RenderTypes.entityCutout(textureId),
				state.lightCoords,
				OverlayTexture.NO_OVERLAY,
				tintedColor, null, state.outlineColor,
				null
			);
		}

		super.submit(state, stack, queue, camState);
		stack.popPose();
	}

	// ////////////// //
	// CUSTOM METHODS //
	// ////////////// //

	protected int getModelTint(final S state) {
		return -1;
	}

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
