package com.virus5600.defensive_measures.renderer.entity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;

import com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity;
import com.virus5600.defensive_measures.model.entity.BallistaTurretModel;
import com.virus5600.defensive_measures.renderer.BaseTurretRenderer;

public class BallistaTurretRenderer extends BaseTurretRenderer<BallistaTurretEntity> {
	public BallistaTurretRenderer(Context ctx) {
		super(ctx, new BallistaTurretModel());
	}
}
