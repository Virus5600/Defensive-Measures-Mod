package com.virus5600.defensive_measures.renderer.entity;

import com.virus5600.defensive_measures.entity.turrets.AATurretEntity;
import com.virus5600.defensive_measures.model.entity.AATurretModel;
import com.virus5600.defensive_measures.util.base.superclasses.renderer.BaseTurretRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/**
 * Anti Air (AA) Turret's Renderer.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class AATurretRenderer extends BaseTurretRenderer<AATurretEntity> {
	public AATurretRenderer(Context ctx) {
		super(ctx, new AATurretModel());
	}

	@Override
	public void preRender(MatrixStack poseStack, AATurretEntity animatable, BakedGeoModel model,
		 @Nullable VertexConsumerProvider bufferSource,
		 @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
		 int packedLight, int packedOverlay, int renderColor
	) {
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender,
			partialTick, packedLight, packedOverlay, renderColor);
	}
}
