package com.virus5600.defensive_measures.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.state.EntityRenderState;

import software.bernie.geckolib.renderer.base.GeoRenderState;

import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;
import com.virus5600.defensive_measures.model.entity.CannonTurretModel;
import com.virus5600.defensive_measures.renderer.BaseTurretRenderer;

/**
 * Cannon Turret's Renderer.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class CannonTurretRenderer<
	R extends EntityRenderState & GeoRenderState
> extends BaseTurretRenderer<CannonTurretEntity, R> {
	public CannonTurretRenderer(Context ctx) {
		super(ctx, new CannonTurretModel());
	}
}
