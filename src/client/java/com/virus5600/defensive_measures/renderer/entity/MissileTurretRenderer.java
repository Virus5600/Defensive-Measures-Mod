package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.turrets.tier2.MissileTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.MissileTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.MissileTurretRenderState;

public class MissileTurretRenderer extends BaseTurretRenderer<
	MissileTurretEntity,
	MissileTurretRenderState,
	MissileTurretModel
	> {

	public MissileTurretRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new MissileTurretModel(ctx.getPart(ModEntityModels.MISSILE_TURRET)),
			MissileTurretRenderState::new
		);

		this.disableLookControlOnDeath();
	}

	// //////// //
	// OVERRIDE //
	// //////// //
	@Override
	public void updateRenderState(MissileTurretEntity turret, MissileTurretRenderState state, float tickProgress) {
		super.updateRenderState(turret, state, tickProgress);

		state.maxRange = turret.getMaxAttackRange();
	}
}
