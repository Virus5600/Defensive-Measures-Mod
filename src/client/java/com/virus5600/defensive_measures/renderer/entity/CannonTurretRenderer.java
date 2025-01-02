package com.virus5600.defensive_measures.renderer.entity;

import com.virus5600.defensive_measures.model.entity.CannonTurretModel;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;

import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CannonTurretRenderer extends GeoEntityRenderer<CannonTurretEntity> {
	public CannonTurretRenderer(Context ctx) {
		super(ctx, new CannonTurretModel());
	}

	@Override
	protected float getDeathMaxRotation(CannonTurretEntity animatable, float partialTick) {
		return 0.0f;
	}
}
