package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.turrets.tier2.AATurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.AATurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class AATurretRenderer extends BaseTurretRenderer<
	AATurretEntity,
	BaseTurretRenderState,
	AATurretModel
	> {

	public AATurretRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new AATurretModel(ctx.bakeLayer(ModEntityModels.AA_TURRET)),
			BaseTurretRenderState::new
		);

		this.disableLookControlOnDeath();
	}
}
