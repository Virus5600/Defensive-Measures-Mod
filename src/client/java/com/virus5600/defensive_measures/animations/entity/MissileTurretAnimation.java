package com.virus5600.defensive_measures.animations.entity;

import net.minecraft.client.render.entity.animation.*;

public class MissileTurretAnimation {
	public static final AnimationDefinition ANIM_MISSILE_TURRET_DEATH = AnimationDefinition.Builder.create(1.5F)
		.addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.875F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(-112.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.125F, AnimationHelper.createRotationalVector(-100.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.2083F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("head", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.125F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 14.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, -15.25F, 26.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, -13.25F, 28.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.125F, AnimationHelper.createTranslationalVector(0.0F, -13.75F, 32.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, -15.25F, 33.0F), Transformation.Interpolations.LINEAR)
		))
		.build();
}
