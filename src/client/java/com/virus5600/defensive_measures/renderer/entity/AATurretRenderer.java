package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.turrets.AATurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.AATurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class AATurretRenderer extends BaseTurretRenderer<
	AATurretEntity,
	BaseTurretRenderState,
	AATurretModel
	> {

	public AATurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new AATurretModel(ctx.getPart(ModEntityModels.AA_TURRET)),
			BaseTurretRenderState::new
		);

		this.disableLookControlOnDeath();
	}
}
