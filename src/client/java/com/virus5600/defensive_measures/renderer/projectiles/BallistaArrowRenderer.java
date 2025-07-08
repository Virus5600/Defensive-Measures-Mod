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

import com.virus5600.defensive_measures.entity.projectiles.BallistaArrowEntity;
import com.virus5600.defensive_measures.model.projectiles.BallistaArrowModel;

import java.util.Optional;

/**
 * The renderer for the {@link BallistaArrowEntity}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class BallistaArrowRenderer<
	R extends EntityRenderState & GeoRenderState
> extends GeoEntityRenderer<BallistaArrowEntity, R> {
	public BallistaArrowRenderer(Context ctx) {
		super(ctx, new BallistaArrowModel());
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

	@Override
	public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model,
		@Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
		boolean isReRender, int packedLight, int packedOverlay, int renderColor
	) {
		float newYaw = Optional.ofNullable(renderState.getGeckolibData(DataTickets.ENTITY_YAW))
			.orElse(0f);
		float newPitch = Optional.ofNullable(renderState.getGeckolibData(DataTickets.ENTITY_PITCH))
			.orElse(0f);

		poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((newYaw * (float) Math.PI / 180F)));
		poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-newPitch));

		super.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender,
			packedLight, packedOverlay, renderColor);
	}
}
