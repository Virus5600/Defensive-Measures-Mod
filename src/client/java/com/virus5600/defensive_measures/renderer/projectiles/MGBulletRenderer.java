package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.projectiles.MGBulletEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.MGBulletModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

/**
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MGBulletRenderer extends BaseProjectileRenderer<
	MGBulletEntity,
	BaseProjectileRenderState,
	MGBulletModel
	> {

	public MGBulletRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new MGBulletModel(ctx.bakeLayer(ModEntityModels.MG_BULLET)),
			0.125f,
			BaseProjectileRenderState::new
		);
	}
}
