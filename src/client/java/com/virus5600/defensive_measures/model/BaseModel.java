package com.virus5600.defensive_measures.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import org.jspecify.annotations.NonNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.model.projectiles.BaseProjectileModel;

import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * This class serves as the base model for all models in the mod. It extends
 * {@link GeoModel} and provides a generic type parameter {@code T} that extends
 * {@link Entity} and implements {@link GeoAnimatable}.
 * <br><br>
 * Utilizing GeckoLib3, this class provides the common logic for identifying the
 * assets needed to render a model, such as the model, texture, and animation.
 * <br><br>
 * This class has two inheritors: {@link BaseTurretModel} and {@link BaseProjectileModel}
 * which both handles the common logic for rendering a turret entity and a projectile. As
 * such, this class is meant to be used as a base class for all models in the mod so that
 * all the common rendering logic can be shared among all models while retaining the
 * ability to create inheritors that can customize the rendering behavior of the model
 * depending on what entity it is rendering.
 *
 * @param <T> An entity that extends {@link Entity} and implements {@link GeoAnimatable}
 *
 * @see BaseTurretModel
 * @see BaseProjectileModel
 * @see software.bernie.geckolib.model.GeoModel
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.1.0
 */
@Environment(EnvType.CLIENT)
public class BaseModel<T extends Entity & GeoAnimatable> extends DefaultedEntityGeoModel<@NonNull T> {
	/** Determines the path of the model. (i.e.: {@code "cannon_turret.geo"} OR {@code "projectiles/mg_bullet.geo"}) */
	protected Identifier modelPath;
	/** Determines the path of the texture this model will use. (i.e.: {@code "cannon_turret/cannon_turret"}) */
	protected Identifier texturePath;
	/** Determines the path of the animation this model will use. (i.e.: {@code "cannon_turret.animation"} OR {@code "projectiles/mg_bullet.animation"}) */
	protected Identifier animationPath;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Constructs a new {@link BaseModel} with the specified model path,
	 * texture path, and animation path. These paths and bone names will be used by this model
	 * renderer to render the turret entity with GeckoLib 3.
	 *
	 * @param modID The mod ID of the mod that owns this model
	 * @param modelPath The path of the model
	 * @param texturePath The path of the texture
	 * @param animationPath The path of the animation
	 *
	 * @see #modelPath
	 * @see #texturePath
	 * @see #animationPath
	 */
	public BaseModel(String modID, String modelPath, String texturePath, String animationPath) {
		super(Identifier.of(modID, modelPath));

		this.modelPath = super.buildFormattedModelPath(Identifier.of(modID, modelPath));
		this.texturePath = super.buildFormattedTexturePath(Identifier.of(modID, texturePath));
		this.animationPath = animationPath != null ?
			super.buildFormattedAnimationPath(Identifier.of(modID, animationPath)) : null;
	}

	// /////////////////// //
	//  INTERFACE METHODS  //
	// /////////////////// //

	@Override
	public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
		return this.modelPath;
	}

	@Override
	public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
		return this.texturePath;
	}

	@Override
	public @NonNull Identifier getAnimationResource(T animatable) {
		return this.animationPath;
	}
}
