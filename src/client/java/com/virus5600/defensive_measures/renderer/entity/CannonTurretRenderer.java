package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.CannonTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class CannonTurretRenderer extends BaseTurretRenderer<
	CannonTurretEntity,
	BaseTurretRenderState,
	CannonTurretModel
	> {

	public CannonTurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new CannonTurretModel(ctx.getPart(ModEntityModels.CANNON_TURRET)),
			0.75f,
			BaseTurretRenderState::new
		);
	}
}
