package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.BallistaTurretModel;
import com.virus5600.DefensiveMeasures.entity.custom.BallistaTurretEntity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BallistaTurretRenderer extends GeoEntityRenderer<BallistaTurretEntity> {

	// CONSTRUCTOR //
	public BallistaTurretRenderer(Context ctx) {
		super(ctx, new BallistaTurretModel());
	}

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getTexture(BallistaTurretEntity instance) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/ballista/ballista.png");
	}

	@Override
	public void render(BallistaTurretEntity entity, float entityYaw, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
		float newSize = 1 * 0.8125f;
		stack.scale(newSize, newSize, newSize);
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	// PROTECTED
	@Override
	protected float getDeathMaxRotation(BallistaTurretEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
