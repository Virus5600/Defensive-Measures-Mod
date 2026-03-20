package com.virus5600.defensive_measures.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.model.projectiles.BaseProjectileModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * This class serves as the base model for all models in the mod. It extends
 * {@link EntityModel} and provides a generic type parameter {@code T} that extends
 * {@link BaseTurretRenderState}.
 * <br><br>
 * This class has two inheritors: {@link BaseTurretModel} and {@link BaseProjectileModel}
 * which both handles the common logic for rendering a turret entity and a projectile. As
 * such, this class is meant to be used as a base class for all models in the mod so that
 * all the common rendering logic can be shared among all models while retaining the
 * ability to create inheritors that can customize the rendering behavior of the model
 * depending on what entity it is rendering.
 *
 * @see BaseTurretModel
 * @see BaseProjectileModel
 * @see BaseTurretRenderState
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 2.0.0
 */
@Environment(EnvType.CLIENT)
public class BaseModel<T extends EntityRenderState> extends EntityModel<T> {
	/** A map of common animations used by turret models. */
	protected final Map<String, Animation> animations;

	/** Determines the path of the texture this model will use. */
	protected Identifier[] texturePath;
	/** Identifies the default or base texture this model will use. */
	protected Identifier baseTexture;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Constructs a new {@link BaseModel} with the specified model path,
	 * texture path, and animation path. These paths and bone names will be used by this model
	 * renderer to render the turret entity with GeckoLib 3.
	 *
	 * @param root        The root model part of this model.
	 * @param texturePath The path of the texture or folder containing textures for this model.
	 * @param idleAnim    The idle animation for this model.
	 * @param shootAnim   The shooting animation for this model.
	 * @param deathAnim   The death animation for this model. Can be null.
	 *
	 * @see #texturePath
	 */
	public BaseModel(
		@NotNull ModelPart root, @NotNull String texturePath,
		@Nullable Animation idleAnim, @Nullable Animation shootAnim, @Nullable Animation deathAnim
		) {
		super(root);

		// Set the texture path(s)
		if (texturePath.endsWith(".png")) {
			this.texturePath = new Identifier[] {
				Identifier.of(DefensiveMeasures.MOD_ID, texturePath)
			};
		}
		else {
			ResourceManager manager = MinecraftClient.getInstance().getResourceManager();

			this.texturePath = manager.findResources(
					texturePath,
					path -> path.getPath().endsWith(".png"))
				.keySet()
				.toArray(new Identifier[0]);
		}

		this.baseTexture = this.texturePath[0];

		// Set the common animations in the animation map
		if (idleAnim != null) {
			this.animations = Map.of(
				"idle", idleAnim,
				"shoot", (shootAnim == null ? idleAnim : shootAnim),
				"death", (deathAnim == null ? idleAnim : deathAnim)
			);
		}
		else {
			this.animations = null;
		}
	}
}
