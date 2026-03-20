package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;

import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.model.BaseModel;

import org.jetbrains.annotations.NotNull;

/**
 * A base model for turrets which handles all the common logic for rendering
 * a {@link TurretEntity turret entity}. The class also provides helper methods
 * for setting the pitch and yaw rotation of the head, which are also
 * overridables so that you can customize the rendering behavior of the turret.
 *
 * @see BaseModel
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 2.0.0
 */
public class BaseTurretModel extends BaseModel<BaseTurretRenderState> {
	/**
	 * Identifies the bone that serves as the base or stand of the turret so that
	 * it can be fixed in place while the turret's head and neck rotate.
	 */
	protected final ModelPart base;
	/**
	 * Identifies the bone that serves as the neck of the turret so that it can
	 * rotate along the yaw axis.
	 */
	protected final ModelPart neck;
	/**
	 * Identifies the bone that serves as the head of the turret so that it can
	 * rotate along the pitch axis.
	 */
	protected final ModelPart head;

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model path,
	 * texture path, and animation path. These paths will be used by this model
	 * renderer to render the turret entity with GeckoLib 3.
	 * <br>
	 * Since this constructor does not specify the bone names for the base, neck,
	 * and head respectively, the default bone names will be used instead ("base",
	 * "neck", and "head").
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture
	 * @param idleAnim The idle animation. Null if none.
	 * @param shootAnim The shooting animation. Null if none.
	 * @param deathAnim The death animation. Null if none.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseTurretModel(
		ModelPart root, String texturePath,
		Animation idleAnim, Animation shootAnim, Animation deathAnim
	) {
		this(
			root, texturePath,
			"base", "neck", "head",
			idleAnim, shootAnim, deathAnim
		);
	}

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model part,
	 * texture path, and bone names. These will be used by this model to
	 * render the turret entity and apply default configurations.
	 *
	 * @param root The root model part of this model.
	 * @param texturePath The path of the texture this model will use.
	 * @param base The name of the base bone in the model.
	 * @param neck The name of the bone that let the turret rotate horizontally.
	 * @param head The name of the bone that let the turret rotate vertically.
	 * @param idleAnim The idle animation. Null if none.
	 * @param shootAnim The shooting animation. Null if none.
	 * @param deathAnim The death animation. Null if none.
	 *
	 * @see #texturePath
	 * @see #baseTexture
	 */
	public BaseTurretModel(
		ModelPart root, String texturePath,
		@NotNull String base, @NotNull String neck, @NotNull String head,
		Animation idleAnim, Animation shootAnim, Animation deathAnim
	) {
		super(
			root, texturePath,
			idleAnim, shootAnim, deathAnim
		);

		// Set the common model parts used by all turret models
		this.base = root.getChild(base);
		this.neck = root.getChild(neck);
		this.head = root.getChild(head);
	}
}
