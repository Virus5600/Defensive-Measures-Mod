package com.virus5600.defensive_measures.animations.entity;

import net.minecraft.client.render.entity.animation.*;

public class CannonTurretAnimation {
	public static final AnimationDefinition ANIM_CANNON_SHOOT = AnimationDefinition.Builder.create(0.52F)
		.addBoneAnimation("base", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.04F, AnimationHelper.createRotationalVector(-0.2662F, 0.0F, -0.0875F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.24F, AnimationHelper.createRotationalVector(0.2105F, 0.0F, -0.2016F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("stand", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.04F, AnimationHelper.createRotationalVector(0.1217F, -0.0928F, 0.1833F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.24F, AnimationHelper.createRotationalVector(-0.1228F, 0.0567F, -0.124F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.04F, AnimationHelper.createRotationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.08F, AnimationHelper.createRotationalVector(-7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.52F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("head", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.08F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.52F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.build();

	public static final AnimationDefinition ANIM_CANNON_DEATH = AnimationDefinition.Builder.create(1.0F)
		.addBoneAnimation("base", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("stand", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightStand", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.28F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -110.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.281F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -110.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.36F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -105.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.44F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -110.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.72F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -104.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightStand", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.44F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.72F, AnimationHelper.createTranslationalVector(-1.0F, -0.6F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("leftStand", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.08F, AnimationHelper.createRotationalVector(0.0F, -25.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2F, AnimationHelper.createRotationalVector(10.0F, -15.62F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.24F, AnimationHelper.createRotationalVector(10.0F, -12.5F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.32F, AnimationHelper.createRotationalVector(4.0F, -9.41F, 24.69F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.56F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 100.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.561F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 100.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.64F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 95.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.72F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 100.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("leftStand", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.08F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.12F, AnimationHelper.createTranslationalVector(1.37F, -0.25F, 1.18F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2F, AnimationHelper.createTranslationalVector(1.75F, -0.3F, 1.37F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.24F, AnimationHelper.createTranslationalVector(2.0F, -0.3F, 1.5F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.56F, AnimationHelper.createTranslationalVector(2.0F, -1.0F, 1.5F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.08F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.24F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.32F, AnimationHelper.createRotationalVector(18.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.44F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.56F, AnimationHelper.createRotationalVector(12.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.72F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("head", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.08F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.24F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.241F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.48F, AnimationHelper.createTranslationalVector(0.0F, -0.65F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.72F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.build();
}
