package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;

import com.virus5600.defensive_measures.entity.turrets.CannonTurretEntity;
import com.virus5600.defensive_measures.model.entity.CannonTurretModel;
import com.virus5600.defensive_measures.renderer.BaseTurretRenderer;

public class CannonTurretRenderer extends BaseTurretRenderer<CannonTurretEntity> {
	public CannonTurretRenderer(Context ctx) {
		super(ctx, new CannonTurretModel());
	}
}
