package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.projectiles.CannonballEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.CannonballModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

/**
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class CannonballRenderer extends BaseProjectileRenderer<
	CannonballEntity,
	BaseProjectileRenderState,
	CannonballModel
	> {

	public CannonballRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new CannonballModel(ctx.bakeLayer(ModEntityModels.CANNONBALL)),
			0.5f,
			BaseProjectileRenderState::new
		);

		this.shouldLookAtDir(false);
	}
}
