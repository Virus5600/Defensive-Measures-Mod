package com.virus5600.defensive_measures.renderer.entity;

import com.virus5600.defensive_measures.model.ModEntityModels;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.MGTurretEntity;
import com.virus5600.defensive_measures.model.entity.MGTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class MGTurretRenderer extends BaseTurretRenderer<
	MGTurretEntity,
	BaseTurretRenderState,
	MGTurretModel
	> {
	private static final Identifier[] TEXTURES = {
		Identifier.of(DefensiveMeasures.MOD_ID, "textures/entity/mg_turret/mg_turret.png")
	};

	public MGTurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new MGTurretModel(ctx.getPart(ModEntityModels.MG_TURRET)),
			0.75f,
			BaseTurretRenderState::new
		);
	}

	@Override
	public Identifier getTexture(BaseTurretRenderState state) {
		return TEXTURES[0];
	}
}
