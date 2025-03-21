package com.virus5600.defensive_measures.renderer.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import com.virus5600.defensive_measures.entity.projectiles.CannonballEntity;
import com.virus5600.defensive_measures.model.projectiles.CannonballModel;

/**
 * The renderer for the {@link CannonballEntity}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class CannonballRenderer extends GeoEntityRenderer<CannonballEntity> {
	public CannonballRenderer(Context ctx) {
		super(ctx, new CannonballModel());
	}

	@Override
	protected float getDeathMaxRotation(CannonballEntity animatable, float partialTick) {
		return 0.0f;
	}
}
