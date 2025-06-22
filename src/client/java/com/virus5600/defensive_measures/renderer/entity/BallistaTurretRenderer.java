package com.virus5600.defensive_measures.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.state.EntityRenderState;

import software.bernie.geckolib.renderer.base.GeoRenderState;

import com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity;
import com.virus5600.defensive_measures.model.entity.BallistaTurretModel;
import com.virus5600.defensive_measures.renderer.BaseTurretRenderer;

/**
 * Ballista Turret's renderer.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class BallistaTurretRenderer<
	R extends EntityRenderState & GeoRenderState
> extends BaseTurretRenderer<BallistaTurretEntity, R> {
	public BallistaTurretRenderer(Context ctx) {
		super(ctx, new BallistaTurretModel());
	}
}
