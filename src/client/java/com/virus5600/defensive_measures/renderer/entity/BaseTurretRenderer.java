package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import java.util.function.Supplier;

public abstract class BaseTurretRenderer<
	T extends TurretEntity,
	S extends BaseTurretRenderState,
	M extends BaseTurretModel<S>
	> extends MobEntityRenderer<T, S, M> {

	private final Supplier<S> renderStateFactory;
	private boolean deathRotEnabled = false;

	public BaseTurretRenderer(
		EntityRendererFactory.Context context,
		M entityModel,
		float shadowRadius,
		Supplier<S> renderStateFactory
	) {
		super(context, entityModel, shadowRadius);

		this.renderStateFactory = renderStateFactory;
	}

	@Override
	public S createRenderState() {
		return renderStateFactory.get();
	}

	@Override
	public void updateRenderState(T turretEntity, S turretRenderState, float tickProgress) {
		super.updateRenderState(turretEntity, turretRenderState, tickProgress);

		turretRenderState.idleAnimationState.copyFrom(turretEntity.getIdleAnimationState());
		turretRenderState.shootAnimationState.copyFrom(turretEntity.getShootAnimationState());
		turretRenderState.deathAnimationState.copyFrom(turretEntity.getDeathAnimationState());

		turretRenderState.turretLvl = turretEntity.getTrackedLevel();
		turretRenderState.shooting = turretEntity.getTrackedShooting();
	}

	@Override
	protected void setupTransforms(S turretState, MatrixStack matrices, float bodyYaw, float baseHeight) {
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

	@Override
	public Identifier getTexture(S state) {
		int index = state.turretLvl - 1;

		return this.getModel().getTextures()[index];
	}

	// ////////////// //
	// CUSTOM METHODS //
	// ////////////// //

	// DEATH RELATED //

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
