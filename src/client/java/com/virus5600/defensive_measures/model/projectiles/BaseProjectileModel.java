package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.entity.projectile.ProjectileEntity;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;
import com.virus5600.defensive_measures.model.BaseModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A base model for projectiles shot by turrets. It handles all the common logic for rendering a
 * {@link ProjectileEntity projectile entity}. The class also provides helper methods commonly
 * used by projectiles, which are also overridables so that you can customize the rendering
 * behavior of the turret.
 * <br><br>
 * This class is meant to be extended by all turret projectile models in the mod, such as {@link CannonballModel},
 * so that all the common rendering logic can be shared among all turret projectiles. This centralization
 * also meant that the render state used by all turret projectile models will be the same, which
 * is {@link BaseProjectileRenderState}, so that all turret projectile models can be rendered using
 * the same render state, allowing for more consistent and efficient rendering of turret projectile
 * entities in the game. However, when more states are needed to be added to the render state, a new
 * render state class that extends {@link BaseProjectileRenderState} could be used, allowing
 * flexibility while keeping everything DRY.
 *
 * @param <S> The type of the render state this model uses. This should be a class that extends
 *            {@link BaseProjectileRenderState} and contains all the necessary information for rendering
 *            the turret entity, such as head yaw and pitch, shooting animation state, and death
 *            animation state.
 *
 * @see BaseModel
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class BaseProjectileModel<S extends BaseProjectileRenderState> extends BaseModel<S> {

	/** An optional looping animation of the custom projectile. */
	protected final Animation loopAnim;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Constructs a new {@link BaseProjectileModel} with a specified model part. This constructor
	 * is used when the model does not have any texture to render, such as the invisible projectile
	 * used by the flame turret.
	 *
	 * @param root The root model part of this model.
	 */
	public BaseProjectileModel(@NotNull ModelPart root) {
		super(root);

		this.loopAnim = null;
	}

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
		@NotNull ModelPart root,@NotNull String texturePath, @NotNull String[] textures,
		@Nullable Animation loopAnim
	) {
		super(root, texturePath, textures);

		// Sets the common animation that may/not be used by all custom projectile models.
		this.loopAnim = loopAnim;
	}
}
