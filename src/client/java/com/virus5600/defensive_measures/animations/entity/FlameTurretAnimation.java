package com.virus5600.defensive_measures.animations.entity;

import net.minecraft.client.render.entity.animation.*;

public class FlameTurretAnimation {
	public static final AnimationDefinition ANIM_FLAME_SHOOT = AnimationDefinition.Builder.create(0.5F)
		.addBoneAnimation("tip", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.25F, AnimationHelper.createRotationalVector(-30.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.build();

	public static final AnimationDefinition ANIM_FLAME_DEATH = AnimationDefinition.Builder.create(1.0F)
		.addBoneAnimation("neck", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.375F, AnimationHelper.createRotationalVector(-50.0F, -27.5F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(-60.0F, -60.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("neck", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.0684F, 0.0F, 0.003F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 25.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, 27.5F, -3.8302F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 25.0F, -3.8302F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(5.4821F, 0.9641F, -3.2453F), Transformation.Interpolations.CUBIC)
		))
		.build();
}
