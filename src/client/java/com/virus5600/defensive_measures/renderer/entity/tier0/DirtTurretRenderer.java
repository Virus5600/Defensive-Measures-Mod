package com.virus5600.defensive_measures.renderer.entity.tier0;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.turrets.tier0.DirtTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.tier0.DirtTurretModel;
import com.virus5600.defensive_measures.renderer.entity.BaseTurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class DirtTurretRenderer extends BaseTurretRenderer<
	DirtTurretEntity,
	BaseTurretRenderState,
	DirtTurretModel
	> {

	public DirtTurretRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new DirtTurretModel(ctx.bakeLayer(ModEntityModels.DIRT_TURRET)),
			BaseTurretRenderState::new
		);
	}
}
