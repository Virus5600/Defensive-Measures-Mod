package com.virus5600.defensive_measures.animations.entity;

import net.minecraft.client.render.entity.animation.*;

public class BallistaTurretAnimation {
	public static final AnimationDefinition ANIM_BALLISTA_SHOOT = AnimationDefinition.Builder.create(1.4167F)
		.addBoneAnimation("innerRightBlade", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0417F, AnimationHelper.createRotationalVector(0.0F, -20.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7083F, AnimationHelper.createRotationalVector(0.0F, -19.4774F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, -17.6398F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, -13.8902F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, -8.9268F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.875F, AnimationHelper.createRotationalVector(0.0F, -3.9583F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9167F, AnimationHelper.createRotationalVector(0.0F, -0.1289F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("outerRightBlade", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0417F, AnimationHelper.createRotationalVector(0.0F, -26.9949F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, -29.1358F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.125F, AnimationHelper.createRotationalVector(0.0F, -30.8615F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, -32.2008F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2083F, AnimationHelper.createRotationalVector(0.0F, -33.1851F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2917F, AnimationHelper.createRotationalVector(0.0F, -34.1906F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, -34.0019F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("innerLeftBlade", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0417F, AnimationHelper.createRotationalVector(0.0F, 20.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7083F, AnimationHelper.createRotationalVector(0.0F, 19.4774F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 17.6398F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 13.8902F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, 8.9268F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.875F, AnimationHelper.createRotationalVector(0.0F, 3.9583F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9167F, AnimationHelper.createRotationalVector(0.0F, 0.1289F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("outerLeftBlade", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0417F, AnimationHelper.createRotationalVector(0.0F, 26.9949F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, 29.1358F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.125F, AnimationHelper.createRotationalVector(0.0F, 30.8615F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 32.2008F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2083F, AnimationHelper.createRotationalVector(0.0F, 33.1851F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2917F, AnimationHelper.createRotationalVector(0.0F, 34.1906F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 34.0019F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("lever", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.6F, AnimationHelper.createRotationalVector(210.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("ram", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -7.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.28F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -4.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -6.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.6F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.92F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("leftString", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0417F, AnimationHelper.createRotationalVector(0.0F, -11.0331F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, -10.9791F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.125F, AnimationHelper.createRotationalVector(0.0F, -10.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, -8.3531F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2083F, AnimationHelper.createRotationalVector(0.0F, -6.3671F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, -5.574F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2917F, AnimationHelper.createRotationalVector(0.0F, -4.5418F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, -4.5427F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, -4.9648F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.4167F, AnimationHelper.createRotationalVector(0.0F, -6.0179F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.4583F, AnimationHelper.createRotationalVector(0.0F, -6.7066F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, -7.2159F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.5417F, AnimationHelper.createRotationalVector(0.0F, -7.5766F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("leftString", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.28F, AnimationHelper.createTranslationalVector(1.75F, 0.0F, -0.75F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4F, AnimationHelper.createTranslationalVector(1.5F, 0.0F, 1.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.6F, AnimationHelper.createTranslationalVector(2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.92F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("leftString", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.0417F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2083F, AnimationHelper.createScalingVector(0.95F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2917F, AnimationHelper.createScalingVector(0.9F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.375F, AnimationHelper.createScalingVector(0.95F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.4583F, AnimationHelper.createScalingVector(0.9F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.5F, AnimationHelper.createScalingVector(0.85F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.5417F, AnimationHelper.createScalingVector(0.8F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.6667F, AnimationHelper.createScalingVector(0.85F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7083F, AnimationHelper.createScalingVector(0.9F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightString", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0417F, AnimationHelper.createRotationalVector(0.0F, 11.0331F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, 10.9791F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.125F, AnimationHelper.createRotationalVector(0.0F, 10.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 8.3531F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2083F, AnimationHelper.createRotationalVector(0.0F, 6.3671F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 5.574F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2917F, AnimationHelper.createRotationalVector(0.0F, 4.5418F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.3333F, AnimationHelper.createRotationalVector(0.0F, 4.5427F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 4.9648F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.4167F, AnimationHelper.createRotationalVector(0.0F, 6.0179F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.4583F, AnimationHelper.createRotationalVector(0.0F, 6.7066F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 7.2159F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.5417F, AnimationHelper.createRotationalVector(0.0F, 7.5766F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightString", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 2.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.28F, AnimationHelper.createTranslationalVector(-1.75F, 0.0F, -0.75F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4F, AnimationHelper.createTranslationalVector(-1.5F, 0.0F, 1.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.6F, AnimationHelper.createTranslationalVector(-2.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.92F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("rightString", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.0417F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2083F, AnimationHelper.createScalingVector(0.95F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.2917F, AnimationHelper.createScalingVector(0.9F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.375F, AnimationHelper.createScalingVector(0.95F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.4583F, AnimationHelper.createScalingVector(0.9F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.5F, AnimationHelper.createScalingVector(0.85F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.5417F, AnimationHelper.createScalingVector(0.8F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.6667F, AnimationHelper.createScalingVector(0.85F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7083F, AnimationHelper.createScalingVector(0.9F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("bolt", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -24.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.92F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("bolt", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.04F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.08F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.92F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.2F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("leftRearDrawString", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createRotationalVector(0.0F, -65.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.28F, AnimationHelper.createRotationalVector(0.0F, 17.5F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4F, AnimationHelper.createRotationalVector(0.0F, -30.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("leftRearDrawString", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createScalingVector(3.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.6F, AnimationHelper.createScalingVector(3.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.92F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("rightRearDrawString", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createRotationalVector(0.0F, 60.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.28F, AnimationHelper.createRotationalVector(0.0F, -17.5F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4F, AnimationHelper.createRotationalVector(0.0F, 30.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.6F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("rightRearDrawString", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createScalingVector(3.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.6F, AnimationHelper.createScalingVector(3.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.92F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("rope", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.04F, AnimationHelper.createScalingVector(1.0F, 1.0F, 4.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.28F, AnimationHelper.createScalingVector(1.0F, 1.0F, 3.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.4F, AnimationHelper.createScalingVector(1.0F, 1.0F, 4.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.6F, AnimationHelper.createScalingVector(1.0F, 1.0F, 3.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.92F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.build();

	public static final AnimationDefinition ANIM_BALLISTA_DEATH = AnimationDefinition.Builder.create(1.5F)
		.addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.625F, AnimationHelper.createRotationalVector(15.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("head", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(3.0F, -7.0F, -4.25F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createTranslationalVector(3.0F, -7.0F, -4.25F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("innerRightBlade", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, -20.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("outerRightBlade", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, -40.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("innerLeftBlade", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, 20.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("outerLeftBlade", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, 40.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("lever", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.4583F, AnimationHelper.createRotationalVector(295.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("ram", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -7.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(0.0F, -0.17F, -14.9971F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("leftString", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 1.1106F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.9451F, -6.5131F, 2.2003F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.875F, AnimationHelper.createRotationalVector(0.7648F, -25.8119F, 3.1619F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9167F, AnimationHelper.createRotationalVector(-0.1906F, -36.7154F, -1.3323F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(-5.9158F, -39.6618F, -47.682F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(-5.2137F, -43.8189F, -54.1109F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0417F, AnimationHelper.createRotationalVector(-4.6139F, -46.9205F, -67.8692F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0833F, AnimationHelper.createRotationalVector(-2.2073F, -50.9471F, -86.1518F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.4137F, -53.3216F, -98.2911F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.1667F, AnimationHelper.createRotationalVector(1.376F, -54.1238F, -101.525F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.2083F, AnimationHelper.createRotationalVector(1.356F, -53.8362F, -103.6839F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.25F, AnimationHelper.createRotationalVector(1.2346F, -53.7827F, -99.2573F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.2917F, AnimationHelper.createRotationalVector(1.177F, -53.8336F, -95.3872F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.3333F, AnimationHelper.createRotationalVector(1.1446F, -53.9016F, -92.5407F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.375F, AnimationHelper.createRotationalVector(1.0954F, -53.9768F, -89.5449F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.4167F, AnimationHelper.createRotationalVector(1.0609F, -54.0256F, -87.9502F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.4583F, AnimationHelper.createRotationalVector(1.0287F, -54.0737F, -86.5434F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("leftString", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(5.4183F, 0.0F, 0.0214F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(2.2F, 0.0712F, 4.3894F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(4.6274F, -3.2017F, 3.6706F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(8.7F, -5.18F, 2.64F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.5F, AnimationHelper.createTranslationalVector(7.2F, -5.43F, 2.64F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("leftString", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createScalingVector(0.4F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.8333F, AnimationHelper.createScalingVector(0.5F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9167F, AnimationHelper.createScalingVector(0.575F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9583F, AnimationHelper.createScalingVector(0.45F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.1667F, AnimationHelper.createScalingVector(0.525F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("rightString", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, -1.1106F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.9451F, 6.5131F, 2.2003F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.875F, AnimationHelper.createRotationalVector(0.7648F, 25.8119F, 3.1619F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9167F, AnimationHelper.createRotationalVector(-0.1906F, 36.7154F, -1.3323F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(-5.9158F, 39.6618F, 47.682F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(-5.2137F, 43.8189F, 54.1109F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0417F, AnimationHelper.createRotationalVector(-4.6139F, 46.9205F, 67.8692F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0833F, AnimationHelper.createRotationalVector(-2.2073F, 50.9471F, 86.1518F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.4137F, 53.3216F, 98.2911F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.1667F, AnimationHelper.createRotationalVector(1.376F, 54.1238F, 101.525F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.2083F, AnimationHelper.createRotationalVector(1.356F, 53.8362F, 103.6839F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.25F, AnimationHelper.createRotationalVector(1.2346F, 53.7827F, 99.2573F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.2917F, AnimationHelper.createRotationalVector(1.177F, 53.8336F, 95.3872F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.3333F, AnimationHelper.createRotationalVector(1.1446F, 53.9016F, 92.5407F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.375F, AnimationHelper.createRotationalVector(1.0954F, 53.9768F, 89.5449F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.4167F, AnimationHelper.createRotationalVector(1.0609F, 54.0256F, 87.9502F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.4583F, AnimationHelper.createRotationalVector(1.0287F, 54.0737F, 86.5434F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightString", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(-5.4183F, 0.0F, 0.0214F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(-2.2F, 0.0712F, 4.3894F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(-4.6274F, -3.2017F, 3.6706F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(-8.7F, -5.18F, 2.64F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.5F, AnimationHelper.createTranslationalVector(-7.2F, -5.43F, 2.64F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightString", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createScalingVector(0.4F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.8333F, AnimationHelper.createScalingVector(0.5F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9167F, AnimationHelper.createScalingVector(0.575F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9583F, AnimationHelper.createScalingVector(0.45F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.1667F, AnimationHelper.createScalingVector(0.525F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("bolt", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, -24.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("bolt", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.875F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("leftRearDrawString", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, -105.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9167F, AnimationHelper.createRotationalVector(0.0F, -112.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightRearDrawString", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 105.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9167F, AnimationHelper.createRotationalVector(0.0F, 112.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rope", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0833F, AnimationHelper.createRotationalVector(-290.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.25F, AnimationHelper.createRotationalVector(-270.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rope", new Transformation(Transformation.Targets.SCALE,
			new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 1.0F, 4.5F), Transformation.Interpolations.CUBIC),
			new Keyframe(0.9167F, AnimationHelper.createScalingVector(1.0F, 1.0F, 2.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 0.75F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("pedestal", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(-90.0F, -4.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(-89.4671F, -3.9644F, -7.5581F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(-90.0F, -4.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("pedestal", new Transformation(Transformation.Targets.MOVE_ORIGIN,
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.75F, AnimationHelper.createTranslationalVector(-0.425F, 1.0F, 1.725F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.875F, AnimationHelper.createTranslationalVector(-0.45F, 1.25F, 1.75F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createTranslationalVector(-0.43F, 1.0F, 1.73F), Transformation.Interpolations.LINEAR)
		))
		.build();
}
