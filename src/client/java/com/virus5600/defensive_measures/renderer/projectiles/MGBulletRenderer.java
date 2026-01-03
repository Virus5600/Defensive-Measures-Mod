package com.virus5600.defensive_measures.renderer.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

import org.jspecify.annotations.NonNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import com.virus5600.defensive_measures.entity.projectiles.MGBulletEntity;
import com.virus5600.defensive_measures.model.projectiles.MGBulletModel;

/**
 * The renderer for the {@link MGBulletEntity}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class MGBulletRenderer<
	R extends EntityRenderState & GeoRenderState
> extends GeoEntityRenderer<MGBulletEntity, @NonNull R> {
	public MGBulletRenderer(Context ctx) {
		super(ctx, new MGBulletModel());
	}

	@Override
	protected float getDeathMaxRotation(@NonNull GeoRenderState renderState) {
		return 0.0f;
	}

	@Override
	public RenderLayer getRenderType(R renderState, @NonNull Identifier texture) {
		return RenderLayers.entityTranslucent(
			this.getTextureLocation(renderState)
		);
	}
}
