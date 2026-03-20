package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.entity.projectile.ProjectileEntity;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;
import com.virus5600.defensive_measures.model.BaseModel;

/**
 * A base model for projectiles shot by turrets. It handles all the common logic
 * for rendering a {@link ProjectileEntity projectile entity}. The
 * class also provides helper methods for setting the pitch and yaw rotation of the head, which are
 * also overridables so that you can customize the rendering behavior of the turret.
 *
 * @see BaseModel
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 2.0.0
 */
public class BaseProjectileModel extends BaseModel<BaseProjectileRenderState> {
	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	/**
	 * Constructs a new {@link BaseProjectileModel} with a specified model part, texture path,
	 * and loop animation. These paths will be used by this model renderer to render the
	 * projectile entity.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture
	 * @param loopAnim The loop animation. Null if none.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseProjectileModel(
		ModelPart root, String texturePath,
		Animation loopAnim
	) {
		super(root, texturePath, loopAnim, null, null);
	}
}
