package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.CannonTurretModel;
import com.virus5600.DefensiveMeasures.entity.custom.CannonTurretEntity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CannonTurretRenderer extends GeoEntityRenderer<CannonTurretEntity> {

	// CONSTRUCTOR //
	public CannonTurretRenderer(Context ctx) {
		super(ctx, new CannonTurretModel());
	}

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getTexture(CannonTurretEntity instance) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/cannon_turret/cannon_turret.png");
	}

	// PROTECTED
	@Override
	protected float getDeathMaxRotation(CannonTurretEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
