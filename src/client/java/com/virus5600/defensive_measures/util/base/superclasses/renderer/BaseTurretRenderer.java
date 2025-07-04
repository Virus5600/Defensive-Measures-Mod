package com.virus5600.defensive_measures.util.base.superclasses.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import com.virus5600.defensive_measures.util.base.superclasses.entity.TurretEntity;

/**
 * This class serves as the base renderer class for all the other
 * renderers in the mod. It extends {@link GeoEntityRenderer} and provides
 * a generic type parameter {@code T} that extends {@link TurretEntity},
 * making sure that only entities that extend {@link TurretEntity} can be
 * rendered by this renderer as this renderer is tailored to render only
 * those entities.
 * <br><br>
 * Once again, this class utilizes GeckoLib3 to render the turret entity
 * with the specified model, texture, and animation paths.
 * <br><br>
 * By default, all entities rendered with this base renderer will not
 * rotate upon death thanks to the overridden {@link #getDeathMaxRotation}
 * method, which could also be overridden by inheritors to provide custom
 * death rotation logic.
 *
 * @param <T> An entity that extends {@link TurretEntity} and implements {@link GeoAnimatable}
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class BaseTurretRenderer<T extends TurretEntity & GeoAnimatable> extends GeoEntityRenderer<T> {
	public BaseTurretRenderer(Context ctx, GeoModel<T> model) {
		super(ctx, model);
	}

	@Override
	protected float getDeathMaxRotation(TurretEntity animatable, float partialTick) {
		return 0.0f;
	}
}
