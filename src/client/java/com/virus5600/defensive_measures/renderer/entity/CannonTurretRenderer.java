package com.virus5600.defensive_measures.renderer.entity;

import com.virus5600.defensive_measures.model.ModEntityModels;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;
import com.virus5600.defensive_measures.model.entity.CannonTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class CannonTurretRenderer extends BaseTurretRenderer<
	CannonTurretEntity,
	BaseTurretRenderState,
	CannonTurretModel
	> {
	private static final Identifier[] TEXTURES = {
		Identifier.of(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannon_turret.png")
	};

	public CannonTurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new CannonTurretModel(ctx.getPart(ModEntityModels.CANNON_TURRET)),
			0.75f,
			BaseTurretRenderState::new
		);
	}

	@Override
	public Identifier getTexture(BaseTurretRenderState state) {
		return TEXTURES[0];
	}
}
