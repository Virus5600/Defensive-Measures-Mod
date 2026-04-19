package com.virus5600.defensive_measures.renderer.entity;

import com.virus5600.defensive_measures.model.ModEntityModels;
import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.turrets.MGTurretEntity;
import com.virus5600.defensive_measures.model.entity.MGTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class MGTurretRenderer extends BaseTurretRenderer<
	MGTurretEntity,
	BaseTurretRenderState,
	MGTurretModel
	> {

	public MGTurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new MGTurretModel(ctx.getPart(ModEntityModels.MG_TURRET)),
			BaseTurretRenderState::new
		);
	}
}
