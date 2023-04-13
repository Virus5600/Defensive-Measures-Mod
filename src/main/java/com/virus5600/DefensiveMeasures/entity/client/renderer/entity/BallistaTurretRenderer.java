package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.BallistaTurretModel;
import com.virus5600.DefensiveMeasures.entity.custom.BallistaTurretEntity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
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

	// PROTECTED
	@Override
	protected float getDeathMaxRotation(BallistaTurretEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
