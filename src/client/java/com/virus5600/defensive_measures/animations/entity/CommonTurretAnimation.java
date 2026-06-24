package com.virus5600.defensive_measures.animations.entity;

import net.minecraft.client.animation.*;
import net.minecraft.client.model.geom.ModelPart;

import com.virus5600.defensive_measures._helper.accessor.model.ModelPartExtensions;

import java.util.List;

public class CommonTurretAnimation {
	public static AnimationDefinition createPopUpSetupAnimation(ModelPart root, float height) {
		float startHeight = -Math.abs(height) * 16.0F;

		return AnimationDefinition.Builder.withLength(2.5f)
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0f, KeyframeAnimations.posVec(0f, startHeight, 0f), AnimationChannel.Interpolations.CATMULLROM),
				new Keyframe(2.5f, KeyframeAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
	}

	public static AnimationDefinition createPopDownTeardownAnimation(ModelPart root, float height) {
		float endHeight = -Math.abs(height) * 16.0F;

		return AnimationDefinition.Builder.withLength(2.5f)
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0f, KeyframeAnimations.posVec(0f, 1f, 0f), AnimationChannel.Interpolations.CATMULLROM),
				new Keyframe(2.5f, KeyframeAnimations.posVec(0f, endHeight, 0f), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
	}

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

	public enum ANIMATIONS {
		SETUP("setup"),
		TEARDOWN("teardown"),

		IDLE("idle"),
		SHOOT("shoot"),
		DEATH("death")
		;

		private final String name;

		ANIMATIONS(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public String toString() {
			return "Animations[name=\"" + this.name + "\"]";
		}
	}
}
