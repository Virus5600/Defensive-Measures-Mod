package com.virus5600.defensive_measures.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.entity.turrets.MGTurretEntity;
import com.virus5600.defensive_measures.model.entity.MGTurretModel;
import com.virus5600.defensive_measures.renderer.BaseTurretRenderer;

/**
 * Machine Gun (MG) Turret's Renderer.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class MGTurretRenderer<
	R extends EntityRenderState & GeoRenderState
> extends BaseTurretRenderer<MGTurretEntity, R> {
	public MGTurretRenderer(Context ctx) {
		super(ctx, new MGTurretModel());
	}

	@Override
	public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model,
		@Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
		boolean isReRender, int packedLight, int packedOverlay, int renderColor
	) {
		poseStack.scale(0.3375f, 0.3375f, 0.3375f);

		super.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender,
			packedLight, packedOverlay, renderColor);
	}
}
