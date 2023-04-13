package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.MGTurretModel;
import com.virus5600.DefensiveMeasures.entity.custom.MGTurretEntity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class MGTurretRenderer extends GeoEntityRenderer<MGTurretEntity> {

	// CONSTRUCTOR //
	public MGTurretRenderer(Context ctx) {
		super(ctx, new MGTurretModel());
	}

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getTexture(MGTurretEntity instance) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/mg_turret/machine_gun_turret.png");
	}

	// PROTECTED
	@Override
	protected float getDeathMaxRotation(MGTurretEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
