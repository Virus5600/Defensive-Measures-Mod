package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.turrets.tier1.CannonTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.CannonTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class CannonTurretRenderer extends BaseTurretRenderer<
	CannonTurretEntity,
	BaseTurretRenderState,
	CannonTurretModel
	> {

	public CannonTurretRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new CannonTurretModel(ctx.bakeLayer(ModEntityModels.CANNON_TURRET)),
			BaseTurretRenderState::new
		);
	}
}
