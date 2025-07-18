package com.virus5600.defensive_measures.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;

import com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity;
import com.virus5600.defensive_measures.model.entity.BallistaTurretModel;
import com.virus5600.defensive_measures.util.base.superclasses.renderer.BaseTurretRenderer;

/**
 * Ballista Turret's renderer.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class BallistaTurretRenderer extends BaseTurretRenderer<BallistaTurretEntity> {
	public BallistaTurretRenderer(Context ctx) {
		super(ctx, new BallistaTurretModel());
	}
}
