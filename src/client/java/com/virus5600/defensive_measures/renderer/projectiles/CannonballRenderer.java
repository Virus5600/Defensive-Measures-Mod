package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.render.entity.EntityRendererFactory;

import com.virus5600.defensive_measures.entity.projectiles.CannonballEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.CannonballModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class CannonballRenderer extends BaseProjectileRenderer<
	CannonballEntity,
	BaseProjectileRenderState,
	CannonballModel
	> {

	public CannonballRenderer(EntityRendererFactory.Context ctx) {
		super(
			ctx,
			new CannonballModel(ctx.getPart(ModEntityModels.CANNONBALL)),
			0.5f,
			BaseProjectileRenderState::new
		);

		this.shouldLookAtDir(false);
	}
}
