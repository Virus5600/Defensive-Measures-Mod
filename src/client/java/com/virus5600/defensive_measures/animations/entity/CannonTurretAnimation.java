package com.virus5600.defensive_measures.animations.entity;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

import com.virus5600.defensive_measures._util.MathUtil;

public class CannonTurretAnimation {
	public static final AnimationDefinition ANIM_CANNON_SHOOT = AnimationDefinition.Builder.withLength(0.52F)
		.addAnimation("base", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.04F, KeyframeAnimations.degreeVec(MathUtil.random(-0.5f, 0.5f), 0.0F, MathUtil.random(-0.5f, 0.5f)), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.24F, KeyframeAnimations.degreeVec(MathUtil.random(-0.25f, 0.25f), 0.0F, MathUtil.random(-0.25f, 0.25f)), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.52F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("stand", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.04F, KeyframeAnimations.degreeVec(MathUtil.random(-0.25f, 0.25f), 0, MathUtil.random(-0.25f, 0.25f)), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.24F, KeyframeAnimations.degreeVec(MathUtil.random(-0.125f, 0.125f), MathUtil.random(-0.125f, 0.125f), MathUtil.random(-0.125f, 0.125f)), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.52F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.04F, KeyframeAnimations.degreeVec(0.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.08F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.52F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.08F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();

	public static final AnimationDefinition ANIM_CANNON_DEATH = AnimationDefinition.Builder.withLength(1.0F)
		.addAnimation("base", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("stand", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("right_stand", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -110.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.281F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -110.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.36F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -105.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.44F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -110.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.72F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -104.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("right_stand", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.44F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.72F, KeyframeAnimations.posVec(-1.0F, -0.6F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("left_stand", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.08F, KeyframeAnimations.degreeVec(0.0F, -25.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2F, KeyframeAnimations.degreeVec(10.0F, -15.62F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.24F, KeyframeAnimations.degreeVec(10.0F, -12.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.32F, KeyframeAnimations.degreeVec(4.0F, -9.41F, 24.69F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.56F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 100.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.561F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 100.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.64F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 95.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.72F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 100.0F), AnimationChannel.Interpolations.CATMULLROM)
		))
		.addAnimation("left_stand", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.08F, KeyframeAnimations.posVec(1.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.12F, KeyframeAnimations.posVec(1.37F, -0.25F, 1.18F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2F, KeyframeAnimations.posVec(1.75F, -0.3F, 1.37F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.24F, KeyframeAnimations.posVec(2.0F, -0.3F, 1.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.56F, KeyframeAnimations.posVec(2.0F, -1.0F, 1.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.08F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.24F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.32F, KeyframeAnimations.degreeVec(18.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.44F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.56F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.72F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.08F, KeyframeAnimations.posVec(0.0F, -0.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.24F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.241F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.48F, KeyframeAnimations.posVec(0.0F, -0.65F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.72F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
		))
		.build();
}
