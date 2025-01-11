package com.virus5600.defensive_measures.model.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.data.EntityModelData;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.model.BaseModel;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A base model for turrets which handles all the common logic for rendering
 * a {@link TurretEntity turret entity} with GeckoLib 3. The class also
 * provides helper methods for setting the pitch and yaw rotation of the head,
 * which are also overridables so that you can customize the rendering behavior
 * of the turret.
 *
 * @param <T> A turret entity that extends {@link TurretEntity} and implements {@link GeoAnimatable}
 */
@Environment(EnvType.CLIENT)
public class BaseTurretModel<T extends TurretEntity & GeoAnimatable> extends BaseModel<T> {
	/**
	 * Identifies the bone that serves as the base or stand of the turret so that
	 * it can be fixed in place while the turret's head and neck rotate.
	 */
	private final String base;
	/**
	 * Identifies the bone that serves as the neck of the turret so that it can
	 * rotate along the yaw axis.
	 */
	private final String neck;
	/**
	 * Identifies the bone that serves as the head of the turret so that it can
	 * rotate along the pitch axis.
	 */
	private final String head;

	/// //////////// ///
	/// CONSTRUCTORS ///
	/// //////////// ///
	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model path,
	 * texture path, and animation path. These paths will be used by this model
	 * renderer to render the turret entity with GeckoLib 3.
	 * <br>
	 * Since this constructor does not specify the bone names for the base, neck,
	 * and head respectively, the default bone names will be used instead ("base",
	 * "neck", and "head").
	 *
	 * @param modID The mod ID of the mod that owns this model
	 * @param modelPath The path of the model
	 * @param texturePath The path of the texture
	 * @param animationPath The path of the animation (can be {@code null})
	 */
	public BaseTurretModel(String modID, String modelPath,
		String texturePath, @Nullable String animationPath
	) {
		this(
			modID,
			modelPath,
			texturePath,
			animationPath,
			"base",
			"neck",
			"head"
		);
	}

	/**
	 * Constructs a new {@link BaseTurretModel} with the specified model path,
	 * texture path, animation path, and bone names for the base, neck, and head.
	 * These paths and bone names will be used by this model renderer to render
	 * the turret entity with GeckoLib 3.
	 * <br>
	 * The bone names will be used to set the pitch of the head and the yaw of
	 * the neck. The base bone will allow the turret's base or stand to be fixed
	 * in place while the turret's head and neck rotate.
	 *
	 * @param modID The mod ID of the mod that owns this model
	 * @param modelPath The path of the model
	 * @param texturePath The path of the texture
	 * @param animationPath The path of the animation (can be {@code null})
	 * @param boneBase The name of the bone that represents the base or stand of the turret
	 * @param boneNeck The name of the bone that represents the neck of the turret
	 * @param boneHead The name of the bone that represents the head of the turret
	 */
	public BaseTurretModel(String modID,
		String modelPath, String texturePath, @Nullable String animationPath,
		String boneBase, String boneNeck, String boneHead
	) {
		super(modID, modelPath, texturePath, animationPath);

		this.base = boneBase;
		this.neck = boneNeck;
		this.head = boneHead;
	}

	/// ////////////// ///
	/// CUSTOM METHODS ///
	/// ////////////// ///

	/**
	 * Sets the pitch rotation of the head.
	 *
	 * @param bone The bone to rotate
	 * @param headPitch The pitch rotation of the head
	 */
	protected void setLookPitch(GeoBone bone, float headPitch) {
		bone.updateRotation(headPitch, 0, 0);
	}

	/**
	 * Sets the yaw rotation of the head.
	 *
	 * @param bone The bone to rotate
	 * @param headYaw The yaw rotation of the head
	 */
	protected void setLookYaw(GeoBone bone, float headYaw) {
		bone.updateRotation(0, headYaw, 0);
	}

	/**
	 * Gets the pitch rotation of the head. This method respects the maximum head
	 * pitch rotation of the animatable entity.
	 *
	 * @param animatable The animatable entity
	 * @param state The animation state
	 * @return The pitch rotation of the head
	 */
	protected float getLookPitch(T animatable, AnimationState<T> state) {
		EntityModelData extraData = (EntityModelData) state.getExtraData()
			.get(DataTickets.ENTITY_MODEL_DATA);
		float maxPitchChange = animatable.getMaxLookPitchChange();

		float targetXRot = (extraData.headPitch() * ((float) Math.PI / 180F));
		targetXRot = Math.clamp(targetXRot, -maxPitchChange, maxPitchChange);

		return targetXRot;
	}

	/**
	 * Gets the yaw rotation of the head. This method respects the maximum head
	 * yaw rotation of the animatable entity.
	 *
	 * @param animatable The animatable entity
	 * @param state The animation state
	 * @return The yaw rotation of the head
	 */
	protected float getLookYaw(T animatable, AnimationState<T> state) {
		EntityModelData extraData = (EntityModelData) state.getExtraData()
			.get(DataTickets.ENTITY_MODEL_DATA);
		float maxYawChange = animatable.getMaxHeadRotation();

		float targetYRot = (extraData.netHeadYaw() * ((float) Math.PI / 180F));
			targetYRot = Math.clamp(targetYRot, -maxYawChange, maxYawChange);

		return targetYRot;
	}

	/////////////////////////
	/// INTERFACE METHODS ///
	/////////////////////////

	@Override
	public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> state) {
		super.setCustomAnimations(animatable, instanceId, state);

		Optional<GeoBone> base = this.getBone(this.base);
		Optional<GeoBone> neck = this.getBone(this.neck);
		Optional<GeoBone> head = this.getBone(this.head);

		if (neck.isPresent()) {
			float targetYRot = this.getLookYaw(animatable, state);
			this.setLookYaw(neck.get(), targetYRot);

			if (head.isPresent()) {
				float targetXRot = this.getLookPitch(animatable, state);
				this.setLookPitch(head.get(), targetXRot);
			}
		}

		animatable.setBodyYaw(0);
		base.ifPresent(bone -> bone.setRotY(0));
	}
}
