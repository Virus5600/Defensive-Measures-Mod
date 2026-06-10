package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.turrets.tier2.MissileTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.MissileTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.MissileTurretRenderState;

import org.jspecify.annotations.NonNull;

public class MissileTurretRenderer extends BaseTurretRenderer<
	MissileTurretEntity,
	MissileTurretRenderState,
	MissileTurretModel
	> {

	public MissileTurretRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new MissileTurretModel(ctx.bakeLayer(ModEntityModels.MISSILE_TURRET)),
			MissileTurretRenderState::new
		);

		this.disableLookControlOnDeath();
	}

	// //////// //
	// OVERRIDE //
	// //////// //
	@Override
	public void extractRenderState(@NonNull MissileTurretEntity turret, @NonNull MissileTurretRenderState state, float tickProgress) {
		super.extractRenderState(turret, state, tickProgress);

		state.maxRange = turret.getMaxAttackRange();
	}
}
