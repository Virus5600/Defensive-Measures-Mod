package com.virus5600.defensive_measures.renderer.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

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
public class MGBulletRenderer extends GeoEntityRenderer<MGBulletEntity> {
	public MGBulletRenderer(Context ctx) {
		super(ctx, new MGBulletModel());
	}

	@Override
	protected float getDeathMaxRotation(MGBulletEntity animatable, float partialTick) {
		return 0.0f;
	}

	@Override
	public RenderLayer getRenderType(MGBulletEntity animatable, Identifier texture,
		@Nullable VertexConsumerProvider bufferSource, float partialTick
	) {
		return RenderLayer.getEntityTranslucent(
			this.getTextureLocation(animatable)
		);
	}

	@Override
	public void preRender(MatrixStack poseStack, MGBulletEntity animatable,
		 BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource,
		 @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
		 int packedOverlay, int renderColor
	) {
		float newYaw = MathHelper.lerp(partialTick, animatable.prevYaw, animatable.getYaw()),
			newPitch = MathHelper.lerp(partialTick, animatable.prevPitch, animatable.getPitch());

		poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(newYaw));
		poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-newPitch));
	}
}
