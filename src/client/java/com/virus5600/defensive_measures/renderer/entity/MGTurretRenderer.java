package com.virus5600.defensive_measures.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;

import software.bernie.geckolib.cache.object.BakedGeoModel;

import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.entity.turrets.MGTurretEntity;
import com.virus5600.defensive_measures.model.entity.MGTurretModel;
import com.virus5600.defensive_measures.util.base.superclasses.renderer.BaseTurretRenderer;

/**
 * Machine Gun (MG) Turret's Renderer.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class MGTurretRenderer extends BaseTurretRenderer<MGTurretEntity> {
	public MGTurretRenderer(Context ctx) {
		super(ctx, new MGTurretModel());
	}

	@Override
	public void preRender(MatrixStack poseStack, MGTurretEntity animatable, BakedGeoModel model,
		 @Nullable VertexConsumerProvider bufferSource,
		 @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
		 int packedLight, int packedOverlay, int renderColor
	) {
		poseStack.scale(0.3375f, 0.3375f, 0.3375f);

		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender,
			partialTick, packedLight, packedOverlay, renderColor);
	}
}
