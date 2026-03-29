package com.virus5600.defensive_measures.renderer.entity;

import com.virus5600.defensive_measures.animations.entity.BallistaTurretAnimation;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import java.util.function.Supplier;

public abstract class BaseTurretRenderer<
	T extends TurretEntity,
	R extends BaseTurretRenderState,
	M extends EntityModel<R>
	> extends MobEntityRenderer<T, R, M> {

	private final Supplier<R> renderStateFactory;
	private boolean deathRotEnabled = false;

	public BaseTurretRenderer(EntityRendererFactory.Context context, M entityModel, float f, Supplier<R> renderStateFactory) {
		super(context, entityModel, f);

		this.renderStateFactory = renderStateFactory;
	}

	@Override
	public R createRenderState() {
		return renderStateFactory.get();
	}

	@Override
	public void updateRenderState(T turretEntity, R turretRenderState, float tickProgress) {
		super.updateRenderState(turretEntity, turretRenderState, tickProgress);

		turretRenderState.variant = turretEntity.getVariant();
		turretRenderState.shooting = turretEntity.getTrackedShooting();

		turretRenderState.shootAnimationState.copyFrom(turretEntity.getShootAnimationState());
		turretRenderState.deathAnimationState.copyFrom(turretEntity.getDeathAnimationState());
	}

	@Override
	protected void setupTransforms(R turretState, MatrixStack matrices, float bodyYaw, float baseHeight) {
		if (this.deathRotEnabled) {
			super.setupTransforms(turretState, matrices, bodyYaw, baseHeight);
			return;
		}

		// Keeps the flipping easter-egg or feature.
		if (turretState.flipUpsideDown) {
			matrices.translate(0.0F, (turretState.height + 0.1F) / baseHeight, 0.0F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
		}
	}

	// ////////////// //
	// CUSTOM METHODS //
	// ////////////// //

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
}
