package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.AntiAirProjectileModel;
import com.virus5600.DefensiveMeasures.entity.projectile.AntiAirProjectileEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class AntiAirProjectileRenderer extends GeoProjectilesRenderer<AntiAirProjectileEntity> {

	// CONSTRUCTOR //
	public AntiAirProjectileRenderer(final Context ctx) {
		super(ctx, new AntiAirProjectileModel());
	}

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getTexture(final AntiAirProjectileEntity instance) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/anti_air_turret/anti_air_projectile.png");
	}

	protected int getBlockLight(final AntiAirProjectileEntity entityIn, final BlockPos partialTicks) {
		return 15;
	}

	public RenderLayer getRenderType(final AntiAirProjectileEntity animatable, final float partialTicks, final MatrixStack stack,
			final VertexConsumerProvider renderTypeBuffer, final VertexConsumer vertexBuilder, final int packedLightIn,
			final Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}
}
