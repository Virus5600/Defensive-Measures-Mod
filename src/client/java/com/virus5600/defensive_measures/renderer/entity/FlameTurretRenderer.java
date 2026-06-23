package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.turrets.tier2.FlameTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.FlameTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.FlameTurretRenderState;
import org.jspecify.annotations.NonNull;

public class FlameTurretRenderer extends BaseTurretRenderer<
	FlameTurretEntity,
	FlameTurretRenderState,
	FlameTurretModel
	> {

	public FlameTurretRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new FlameTurretModel(ctx.bakeLayer(ModEntityModels.FLAME_TURRET)),
			FlameTurretRenderState::new
		);
	}

	@Override
	public void extractRenderState(@NonNull FlameTurretEntity turret, @NonNull FlameTurretRenderState state, float tickProgress) {
		super.extractRenderState(turret, state, tickProgress);

		state.lighterPitch = turret.lighterPitch;
	}
}
