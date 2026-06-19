package com.virus5600.defensive_measures.animations.entity;

import net.minecraft.client.animation.*;
import net.minecraft.client.model.geom.ModelPart;

public class CommonTurretAnimation {
	public static KeyframeAnimation createDefaultSetupAnimation(ModelPart root, float height) {
		float startHeight = -Math.abs(height) * 16.0F;

		return AnimationDefinition.Builder.withLength(2.5f)
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0f, KeyframeAnimations.posVec(0f, startHeight, 0f), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5f, KeyframeAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build()
			.bake(root);
	}

	public static KeyframeAnimation createDefaultTeardownAnimation(ModelPart root, float height) {
		float endHeight = -Math.abs(height) * 16.0F;

		return AnimationDefinition.Builder.withLength(2.5f)
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0f, KeyframeAnimations.posVec(0f, 1f, 0f), AnimationChannel.Interpolations.CATMULLROM),
				new Keyframe(2.5f, KeyframeAnimations.posVec(0f, endHeight, 0f), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build()
			.bake(root);
	}
}
