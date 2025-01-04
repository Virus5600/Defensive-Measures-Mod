package com.virus5600.defensive_measures.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BaseTurretRenderer<T extends TurretEntity & GeoAnimatable> extends GeoEntityRenderer<T> {
	public BaseTurretRenderer(Context ctx, GeoModel<T> model) {
		super(ctx, model);
	}

	@Override
	protected float getDeathMaxRotation(TurretEntity animatable, float partialTick) {
		return 0.0f;
	}
}
