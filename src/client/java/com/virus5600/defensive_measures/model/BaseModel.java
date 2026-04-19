package com.virus5600.defensive_measures.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.model.projectiles.BaseProjectileModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
	/** Determines the path of the texture(s) this model will use. */
	protected Identifier texturePath;
	/** Identifies all the texture(s) this model will use. */
	protected Identifier[] textures;
	/** Identifies the default or base texture this model will use. */
	protected Identifier baseTexture;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	/**
	 * Constructs a new {@link BaseModel} with the specified root model part and texture path. The
	 * constructor will use
	 *
	 * @param root        The root model part of this model.
	 * @param texturePath The path of the folder that contains the texture(s) for this model.
	 * @param textures    An array of all the texture names this model will use.
	 *
	 * @see #texturePath
	 * @see #textures
	 */
	public BaseModel(@NotNull ModelPart root, @NotNull String texturePath, @NotNull String[] textures) {
		super(root);

		texturePath = "textures/entity/" + texturePath;
		this.texturePath = Identifier.of(DefensiveMeasures.MOD_ID, texturePath);

		this.textures = Arrays.stream(textures)
			.map(texture -> Identifier.of(
				DefensiveMeasures.MOD_ID,
				this.texturePath.getPath() + "/" + texture
			))
			.toArray(Identifier[]::new);

		this.baseTexture = this.textures[0];
	}

	// ////////////// //
	// CUSTOM METHODS //
	// ////////////// //

	protected Identifier getTexturePath() {
		return this.texturePath;
	}

	public Identifier[] getTextures() {
		return this.textures;
	}

	public Identifier getBaseTexture() {
		return this.baseTexture;
	}
}
