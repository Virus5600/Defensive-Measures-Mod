package com.virus5600.defensive_measures.animations.entity;

import net.minecraft.client.animation.*;
import net.minecraft.client.model.geom.ModelPart;

import com.virus5600.defensive_measures._helper.accessor.model.ModelPartExtensions;

import java.util.List;

/**
 * A class containing static methods for creating common turret animations.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class CommonTurretAnimation {
	/**
	 * Creates a pop-up animation for the setup.
	 * <br><br>
	 * The "pop-up" animation is just the turret sliding up from below the ground for 2.5 seconds.
	 *
	 * @param root   the root model part
	 * @param height the height to slide up (in blocks)
	 *
	 * @return the animation definition
	 *
	 * @see #createPopDownTeardownAnimation(ModelPart, float)
	 */
	public static AnimationDefinition createPopUpSetupAnimation(ModelPart root, float height) {
		ModelPartExtensions rootExt = ((ModelPartExtensions) (Object) root);
		String firstBone = "root";
		float startHeight = -Math.abs(height) * 16.0F;

		if (rootExt != null) {
			List<String> rootChildrenNames = rootExt.dm$getChildrenNames();

			if (rootChildrenNames.contains("base")) {
				firstBone = "base";
			}
		}

		return AnimationDefinition.Builder.withLength(2.5f)
			.addAnimation(firstBone, new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0f, KeyframeAnimations.posVec(0f, startHeight, 0f), AnimationChannel.Interpolations.CATMULLROM),
				new Keyframe(2.5f, KeyframeAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
	}

	/**
	 * Creates a pop-down animation for the teardown.
	 * <br><br>
	 * The "pop-down" animation is just the turret sliding down into the ground for 2.5 seconds.
	 *
	 * @param root   the root model part
	 * @param height the height to slide down (in blocks)
	 *
	 * @return the animation definition
	 *
	 * @see #createPopDownTeardownAnimation(ModelPart, float)
	 */
	public static AnimationDefinition createPopDownTeardownAnimation(ModelPart root, float height) {
		ModelPartExtensions rootExt = ((ModelPartExtensions) (Object) root);
		String firstBone = "root";
		float endHeight = -Math.abs(height) * 16.0F;

		if (rootExt != null) {
			List<String> rootChildrenNames = rootExt.dm$getChildrenNames();

			if (rootChildrenNames.contains("base")) {
				firstBone = "base";
			}
		}

		return AnimationDefinition.Builder.withLength(2.5f)
			.addAnimation(firstBone, new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0f, KeyframeAnimations.posVec(0f, 1f, 0f), AnimationChannel.Interpolations.CATMULLROM),
				new Keyframe(2.5f, KeyframeAnimations.posVec(0f, endHeight, 0f), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
	}

	/**
	 * Creates a scale-up animation for the setup.
	 * <br><br>
	 * The "scale-up" animation is just the turret scaling up from scale 0 to scale 1 for 2.5
	 * seconds.
	 *
	 * @param root the root model part
	 *
	 * @return the animation definition
	 */
	public static AnimationDefinition createScaleUpSetupAnimation(ModelPart root) {
		ModelPartExtensions rootExt = ((ModelPartExtensions) (Object) root);
		String firstBone = "root";

		if (rootExt != null) {
			List<String> rootChildrenNames = rootExt.dm$getChildrenNames();

			if (rootChildrenNames.contains("base")) {
				firstBone = "base";
			}
		}

		return AnimationDefinition.Builder.withLength(2.5f)
			.addAnimation(firstBone, new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0f, KeyframeAnimations.scaleVec(0f, 0f, 0f), AnimationChannel.Interpolations.CATMULLROM),
				new Keyframe(2.5f, KeyframeAnimations.scaleVec(1f, 1f, 1f), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
	}

	/**
	 * Creates a scale-down animation for the teardown.
	 * <br><br>
	 * The "scale-down" animation is just the turret scaling down from scale 1 to scale 0 for 2.5
	 * seconds.
	 *
	 * @param root the root model part
	 *
	 * @return the animation definition
	 */
	public static AnimationDefinition createScaleDownAnimation(ModelPart root) {
		ModelPartExtensions rootExt = ((ModelPartExtensions) (Object) root);
		String firstBone = "root";

		if (rootExt != null) {
			List<String> rootChildrenNames = rootExt.dm$getChildrenNames();

			if (rootChildrenNames.contains("base")) {
				firstBone = "base";
			}
		}

		return AnimationDefinition.Builder.withLength(2.5f)
			.addAnimation(firstBone, new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f), AnimationChannel.Interpolations.CATMULLROM),
				new Keyframe(2.5f, KeyframeAnimations.scaleVec(0f, 0f, 0f), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
	}
}
