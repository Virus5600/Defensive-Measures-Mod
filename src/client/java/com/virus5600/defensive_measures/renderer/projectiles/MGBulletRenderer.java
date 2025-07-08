package com.virus5600.defensive_measures.renderer.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import com.virus5600.defensive_measures.entity.projectiles.MGBulletEntity;
import com.virus5600.defensive_measures.model.projectiles.MGBulletModel;

import java.util.Optional;

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
> extends GeoEntityRenderer<MGBulletEntity, R> {
	public MGBulletRenderer(Context ctx) {
		super(ctx, new MGBulletModel());
	}

	@Override
	protected float getDeathMaxRotation(GeoRenderState renderState) {
		return 0.0f;
	}

	@Override
	public RenderLayer getRenderType(R renderState, Identifier texture) {
		return RenderLayer.getEntityTranslucent(
			this.getTextureLocation(renderState)
		);
	}
}
