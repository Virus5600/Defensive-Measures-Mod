package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.projectiles.BallistaBoltEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.BallistaBoltModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

/**
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BallistaBoltRenderer extends BaseProjectileRenderer<
	BallistaBoltEntity,
	BaseProjectileRenderState,
	BallistaBoltModel
	> {

	public BallistaBoltRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new BallistaBoltModel(ctx.bakeLayer(ModEntityModels.BALLISTA_BOLT)),
			0.25f,
			BaseProjectileRenderState::new
		);
	}
}
