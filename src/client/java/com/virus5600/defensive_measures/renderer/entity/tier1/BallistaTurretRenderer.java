package com.virus5600.defensive_measures.renderer.entity.tier1;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.turrets.tier1.BallistaTurretEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.entity.tier1.BallistaTurretModel;
import com.virus5600.defensive_measures.renderer.entity.BaseTurretRenderer;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

/**
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BallistaTurretRenderer extends BaseTurretRenderer<
	BallistaTurretEntity,
	BaseTurretRenderState,
	BallistaTurretModel
	> {

	public BallistaTurretRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new BallistaTurretModel(ctx.bakeLayer(ModEntityModels.BALLISTA_TURRET)),
			BaseTurretRenderState::new
		);
	}
}
