package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.projectiles.BallistaBoltEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.BallistaBoltModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class BallistaBoltRenderer extends BaseProjectileRenderer<
	BallistaBoltEntity,
	BaseProjectileRenderState,
	BallistaBoltModel
	> {

	public BallistaBoltRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new BallistaBoltModel(ctx.getPart(ModEntityModels.BALLISTA_BOLT)),
			0.25f,
			BaseProjectileRenderState::new
		);
	}
}
