package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.projectiles.FlammableAerosolEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.FlammableAerosolModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class FlammableAerosolRenderer extends BaseProjectileRenderer<
	FlammableAerosolEntity,
	BaseProjectileRenderState,
	FlammableAerosolModel
	> {

	public FlammableAerosolRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new FlammableAerosolModel(ctx.getPart(ModEntityModels.FLAMMABLE_AEROSOL)),
			0.0f,
			BaseProjectileRenderState::new
		);

		this.shouldLookAtDir(false);
	}
}
