package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.projectiles.FlakProjectileEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.FlakProjectileModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

import org.jspecify.annotations.NonNull;

public class FlakProjectileRenderer extends BaseProjectileRenderer<
	FlakProjectileEntity,
	BaseProjectileRenderState,
	FlakProjectileModel
	> {

	public FlakProjectileRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new FlakProjectileModel(ctx.bakeLayer(ModEntityModels.FLAK_PROJECTILE)),
			0.5f,
			BaseProjectileRenderState::new
		);
	}

	@Override
	public void extractRenderState(@NonNull FlakProjectileEntity entity, @NonNull BaseProjectileRenderState state, float tickProgress) {
		super.extractRenderState(entity, state, tickProgress);

		state.pitch = entity.getXRot(tickProgress);
	}
}
