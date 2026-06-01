package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.projectiles.FlakProjectileEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.FlakProjectileModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class FlakProjectileRenderer extends BaseProjectileRenderer<
	FlakProjectileEntity,
	BaseProjectileRenderState,
	FlakProjectileModel
	> {

	public FlakProjectileRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new FlakProjectileModel(ctx.getPart(ModEntityModels.FLAK_PROJECTILE)),
			0.5f,
			BaseProjectileRenderState::new
		);
	}

	@Override
	public void updateRenderState(FlakProjectileEntity entity, BaseProjectileRenderState state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);

		state.pitch = entity.getLerpedPitch(tickProgress);
	}
}
