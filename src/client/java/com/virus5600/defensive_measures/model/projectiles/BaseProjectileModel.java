package com.virus5600.defensive_measures.model.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.ProjectileEntity;

import software.bernie.geckolib.animatable.GeoAnimatable;

import com.virus5600.defensive_measures.model.BaseModel;

import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BaseProjectileModel<T extends ProjectileEntity & GeoAnimatable> extends BaseModel<T> {
	/// //////////// ///
	/// CONSTRUCTORS ///
	/// //////////// ///
	/**
	 * Constructs a new {@link BaseProjectileModel} with the specified model path,
	 * texture path, and animation path. These paths will be used by this model
	 * renderer to render the projectile entity with GeckoLib 3.
	 *
	 * @param modID The mod ID of the mod that owns this model
	 * @param modelPath The path of the model
	 * @param texturePath The path of the texture
	 * @param animationPath The path of the animation (can be {@code null})
	 */
	public BaseProjectileModel(String modID, String modelPath, String texturePath, @Nullable String animationPath) {
		super(modID, modelPath, texturePath, animationPath);
	}
}
