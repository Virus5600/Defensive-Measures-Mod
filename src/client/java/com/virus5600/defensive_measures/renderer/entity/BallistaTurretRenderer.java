package com.virus5600.defensive_measures.renderer.entity;

import com.virus5600.defensive_measures.model.ModEntityModels;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity;
import com.virus5600.defensive_measures.model.entity.BallistaTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class BallistaTurretRenderer extends BaseTurretRenderer<
	BallistaTurretEntity,
	BaseTurretRenderState,
	BallistaTurretModel
	> {
	private static final Identifier[] TEXTURES = {
		Identifier.of(DefensiveMeasures.MOD_ID, "textures/entity/ballista/ballista.png")
	};

	public BallistaTurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new BallistaTurretModel(ctx.getPart(ModEntityModels.BALLISTA_TURRET)),
			0.75f,
			BaseTurretRenderState::new
		);
	}

	@Override
	public Identifier getTexture(BaseTurretRenderState state) {
		return TEXTURES[0];
	}
}
