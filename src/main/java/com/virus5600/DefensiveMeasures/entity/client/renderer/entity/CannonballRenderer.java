package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.CannonballModel;
import com.virus5600.DefensiveMeasures.entity.projectile.CannonballEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class CannonballRenderer extends GeoProjectilesRenderer<CannonballEntity> {

	// CONSTRUCTOR //
	public CannonballRenderer(Context ctx) {
		super(ctx, new CannonballModel());
	}

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getTexture(CannonballEntity instance) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannonball.png");
	}

	protected int getBlockLight(RocketProjectile entityIn, BlockPos partialTicks) {
		return 15;
	}

	public RenderLayer getRenderType(CannonballEntity animatable, float partialTicks, MatrixStack stack,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}
}
