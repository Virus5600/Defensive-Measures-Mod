package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.MGBulletModel;
import com.virus5600.DefensiveMeasures.entity.projectile.MGBulletEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class MGBulletRenderer extends GeoProjectilesRenderer<MGBulletEntity> {

	// CONSTRUCTOR //
	public MGBulletRenderer(Context ctx) {
		super(ctx, new MGBulletModel());
	}

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getTexture(MGBulletEntity instance) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/mg_turret/mg_bullet.png");
	}

	protected int getBlockLight(MGBulletEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	public RenderLayer getRenderType(MGBulletEntity animatable, float partialTicks, MatrixStack stack,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}
}
