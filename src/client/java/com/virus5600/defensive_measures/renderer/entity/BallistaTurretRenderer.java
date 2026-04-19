package com.virus5600.defensive_measures.renderer.entity;

import com.virus5600.defensive_measures.model.ModEntityModels;
import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity;
import com.virus5600.defensive_measures.model.entity.BallistaTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class BallistaTurretRenderer extends BaseTurretRenderer<
	BallistaTurretEntity,
	BaseTurretRenderState,
	BallistaTurretModel
	> {

	public BallistaTurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new BallistaTurretModel(ctx.getPart(ModEntityModels.BALLISTA_TURRET)),
			BaseTurretRenderState::new
		);
	}
}
