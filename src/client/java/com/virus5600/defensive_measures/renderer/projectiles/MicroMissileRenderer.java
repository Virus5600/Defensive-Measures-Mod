package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.projectiles.MicroMissileEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.MicroMissileModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class MicroMissileRenderer extends BaseProjectileRenderer<
	MicroMissileEntity,
	BaseProjectileRenderState,
	MicroMissileModel
	> {

	public MicroMissileRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new MicroMissileModel(ctx.bakeLayer(ModEntityModels.MICRO_MISSILE)),
			0.5f,
			BaseProjectileRenderState::new
		);

		this.shouldLookAtDir(true);
	}
}
