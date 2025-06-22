package com.virus5600.defensive_measures.renderer.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.state.EntityRenderState;

import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import com.virus5600.defensive_measures.entity.projectiles.CannonballEntity;
import com.virus5600.defensive_measures.model.projectiles.CannonballModel;

/**
 * The renderer for the {@link CannonballEntity}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class CannonballRenderer<
	R extends EntityRenderState & GeoRenderState
> extends GeoEntityRenderer<CannonballEntity, R> {
	public CannonballRenderer(Context ctx) {
		super(ctx, new CannonballModel());
	}

	@Override
	protected float getDeathMaxRotation(GeoRenderState renderState) {
		return 0.0f;
	}
}
