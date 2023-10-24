package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.BallistaArrowModel;
import com.virus5600.DefensiveMeasures.entity.projectile.BallistaArrowEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class BallistaArrowRenderer extends GeoProjectilesRenderer<BallistaArrowEntity> {

	// CONSTRUCTOR //
	public BallistaArrowRenderer(Context ctx) {
		super(ctx, new BallistaArrowModel());
	}

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getTexture(BallistaArrowEntity instance) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/ballista/ballista_arrow.png");
	}

	protected int getBlockLight(BallistaArrowEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	public RenderLayer getRenderType(BallistaArrowEntity animatable, float partialTicks, MatrixStack stack,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}
}
