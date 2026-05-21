package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.turrets.FlameTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.FlameTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class FlameTurretRenderer extends BaseTurretRenderer<
	FlameTurretEntity,
	BaseTurretRenderState,
	FlameTurretModel
	> {

	public FlameTurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new FlameTurretModel(ctx.getPart(ModEntityModels.FLAME_TURRET)),
			BaseTurretRenderState::new
		);
	}
}
