package com.virus5600.defensive_measures.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CannonTurretRenderer extends GeoEntityRenderer<CannonTurretEntity> {
	public CannonTurretRenderer(Context ctx, GeoModel<CannonTurretEntity> model) {
		super(ctx, model);
	}
}
