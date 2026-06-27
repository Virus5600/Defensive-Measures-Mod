package com.virus5600.defensive_measures.renderer.entity.tier2;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.turrets.tier2.AATurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.tier2.AATurretModel;
import com.virus5600.defensive_measures.renderer.entity.BaseTurretRenderer;
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
