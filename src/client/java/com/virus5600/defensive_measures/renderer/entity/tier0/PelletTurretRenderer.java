package com.virus5600.defensive_measures.renderer.entity.tier0;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.turrets.tier0.PelletTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.tier0.PelletTurretModel;
import com.virus5600.defensive_measures.renderer.entity.BaseTurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class PelletTurretRenderer extends BaseTurretRenderer<
	PelletTurretEntity,
	BaseTurretRenderState,
	PelletTurretModel
	> {

	public PelletTurretRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new PelletTurretModel(ctx.bakeLayer(ModEntityModels.PELLET_TURRET)),
			BaseTurretRenderState::new
		);
	}
}
