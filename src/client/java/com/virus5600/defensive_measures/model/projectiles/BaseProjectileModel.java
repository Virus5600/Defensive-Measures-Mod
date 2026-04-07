package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.entity.projectile.ProjectileEntity;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;
import com.virus5600.defensive_measures.model.BaseModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	/** An optional looping animation of the custom projectile. */
	protected final Animation loopAnim;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Constructs a new {@link BaseProjectileModel} with a specified model part and texture path.
	 * These paths will be used by this model renderer to render the projectile entity.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture.
	 * @param textures An array of all the texture names this model will use.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseProjectileModel(
		@NotNull ModelPart root,
		@NotNull String texturePath,
		@NotNull String[] textures
	) {
		this(root, texturePath, textures, null);
	}

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
		@NotNull ModelPart root, @NotNull String texturePath, @NotNull String[] textures,
		@Nullable Animation loopAnim
	) {
		super(root, texturePath, textures);

		// Sets the common animation that may/not be used by all custom projectile models.
		this.loopAnim = loopAnim;
	}
}
