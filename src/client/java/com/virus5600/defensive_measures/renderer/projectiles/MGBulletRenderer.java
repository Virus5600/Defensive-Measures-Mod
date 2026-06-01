package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.projectiles.MGBulletEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.MGBulletModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class MGBulletRenderer extends BaseProjectileRenderer<
	MGBulletEntity,
	BaseProjectileRenderState,
	MGBulletModel
	> {

	public MGBulletRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new MGBulletModel(ctx.getPart(ModEntityModels.MG_BULLET)),
			0.125f,
			BaseProjectileRenderState::new
		);
	}
}
