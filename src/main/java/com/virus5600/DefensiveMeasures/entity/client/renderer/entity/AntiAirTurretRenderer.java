package com.virus5600.DefensiveMeasures.entity.client.renderer.entity;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.client.model.entity.AntiAirTurretModel;
import com.virus5600.DefensiveMeasures.entity.custom.AntiAirTurretEntity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class AntiAirTurretRenderer extends GeoEntityRenderer<AntiAirTurretEntity> {

	// CONSTRUCTOR //
	public AntiAirTurretRenderer(final Context ctx) {
		super(ctx, new AntiAirTurretModel());
	}

	// METHODS //
	// PUBLIC
	@Override
	public Identifier getTexture(final AntiAirTurretEntity instance) {
		return new Identifier(DefensiveMeasures.MOD_ID, "textures/entity/anti_air_turret/anti_air_turret.png");
	}

	// PROTECTED
	@Override
	protected float getDeathMaxRotation(final AntiAirTurretEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
