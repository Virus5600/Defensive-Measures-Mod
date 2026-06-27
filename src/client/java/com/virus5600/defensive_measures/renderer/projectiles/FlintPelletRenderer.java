package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.projectiles.FlintPelletEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.FlintPelletProjectileModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class FlintPelletRenderer extends BaseProjectileRenderer<
	FlintPelletEntity,
	BaseProjectileRenderState,
	FlintPelletProjectileModel
	> {

	public FlintPelletRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new FlintPelletProjectileModel(ctx.bakeLayer(ModEntityModels.FLINT_PELLET)),
			0.125f,
			BaseProjectileRenderState::new
		);
	}
}
