package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;
import net.minecraft.entity.AnimationState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.animations.FXKeyframe;
import com.virus5600.defensive_measures.animations.Keyframe;
import com.virus5600.defensive_measures.animations.ScriptKeyframe;
import com.virus5600.defensive_measures.animations.entity.AATurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import java.util.PriorityQueue;
import java.util.Queue;

import static com.virus5600.defensive_measures.animations.KeyframeScripts.EXPLODE_SCRIPT;

public class AATurretModel extends BaseTurretModel<BaseTurretRenderState> {
	private final static Queue<? extends Keyframe> DEATH_KEYFRAMES;

	protected final static String[] TEXTURES = new String[]{
		"aa_turret.png"
	};

	public AATurretModel(ModelPart root) {
		super(
			root, "aa_turret", TEXTURES,

			root.getChild("base").getChild("horizontal_traverse"),
			root.getChild("base").getChild("horizontal_traverse").getChild("gun"),

			AATurretAnimation.ANIM_AA_TURRET_SHOOT,
			AATurretAnimation.ANIM_AA_TURRET_DEATH
		);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -4.0F, -16.0F, 4.0F, 2.0F, 32.0F, new Dilation(0.0F))
			.uv(0, 61).cuboid(-16.0F, -4.0F, -2.0F, 32.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		base.addChild("base_stand", ModelPartBuilder.create().uv(20, 91).cuboid(14.0F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
			.uv(16, 101).cuboid(-0.5F, -2.0F, -15.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
			.uv(86, 105).cuboid(-0.5F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
			.uv(86, 100).cuboid(-15.0F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
			.uv(108, 0).cuboid(13.0F, 2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
			.uv(108, 4).cuboid(-16.0F, 2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
			.uv(108, 34).cuboid(-1.5F, 2.0F, 13.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
			.uv(108, 38).cuboid(-1.5F, 2.0F, -16.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -3.0F, 0.0F));

		ModelPartData stand = base.addChild("stand", ModelPartBuilder.create().uv(0, 67).cuboid(-5.0F, -0.25F, -5.0F, 10.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -4.0F, 0.0F));

		stand.addChild("stand_r1", ModelPartBuilder.create().uv(16, 107).cuboid(-3.0F, -5.0F, -1.0F, 6.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -4.0F, -0.2618F, 0.0F, 0.0F));
		stand.addChild("stand_r2", ModelPartBuilder.create().uv(54, 105).cuboid(-3.0F, -5.0F, -1.0F, 6.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 4.0F, 0.2618F, 0.0F, 0.0F));
		stand.addChild("stand_r3", ModelPartBuilder.create().uv(38, 96).cuboid(-1.0F, -5.0F, -3.0F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));
		stand.addChild("stand_r4", ModelPartBuilder.create().uv(96, 23).cuboid(-1.0F, -5.0F, -3.0F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		ModelPartData horizontal_traverse = base.addChild("horizontal_traverse", ModelPartBuilder.create().uv(72, 0).cuboid(-5.0F, -1.0F, -4.0F, 10.0F, 1.0F, 8.0F, new Dilation(0.0F))
			.uv(72, 32).cuboid(-4.0F, -2.0F, 3.0F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F))
			.uv(98, 18).cuboid(-4.0F, -2.0F, -4.0F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F))
			.uv(72, 9).cuboid(4.0F, -8.0F, -1.0F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F))
			.uv(92, 88).cuboid(-5.0F, -4.0F, -4.0F, 1.0F, 3.0F, 8.0F, new Dilation(0.0F))
			.uv(48, 84).cuboid(-5.0F, -6.0F, -2.0F, 1.0F, 2.0F, 10.0F, new Dilation(0.0F))
			.uv(74, 61).cuboid(-5.0F, -8.0F, -1.0F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F))
			.uv(20, 96).cuboid(4.0F, -4.0F, -4.0F, 1.0F, 3.0F, 8.0F, new Dilation(0.0F))
			.uv(70, 88).cuboid(4.0F, -6.0F, -2.0F, 1.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -9.0F, 0.0F));

		ModelPartData balancer = horizontal_traverse.addChild("balancer", ModelPartBuilder.create().uv(0, 79).cuboid(2.0F, -4.0F, -9.0F, 1.0F, 1.0F, 11.0F, new Dilation(0.25F))
			.uv(24, 84).cuboid(-3.0F, -4.0F, -9.0F, 1.0F, 1.0F, 11.0F, new Dilation(0.25F))
			.uv(70, 84).cuboid(2.0F, -3.0F, -5.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
			.uv(104, 20).cuboid(-3.0F, -3.0F, -5.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		balancer.addChild("springs", ModelPartBuilder.create().uv(70, 100).cuboid(2.0F, -0.5F, 0.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F))
			.uv(100, 70).cuboid(-3.0F, -0.5F, 0.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -3.5F, 2.0F));

		ModelPartData gun = horizontal_traverse.addChild("gun", ModelPartBuilder.create().uv(0, 34).cuboid(-1.0F, 1.0F, -19.0F, 2.0F, 2.0F, 25.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.5F, 8.5F, -0.5236F, 0.0F, 0.0F));

		ModelPartData hinges = gun.addChild("hinges", ModelPartBuilder.create().uv(72, 23).cuboid(-2.0F, 2.9F, -6.0F, 4.0F, 1.0F, 8.0F, new Dilation(0.0F))
			.uv(0, 91).cuboid(-1.0F, -5.1F, -6.0F, 2.0F, 2.0F, 8.0F, new Dilation(0.0F))
			.uv(24, 79).cuboid(-2.0F, -3.8F, -5.0F, 4.0F, 1.0F, 3.0F, new Dilation(0.1F))
			.uv(74, 75).cuboid(-1.0F, -5.1F, -16.0F, 2.0F, 2.0F, 11.0F, new Dilation(-0.25F))
			.uv(98, 20).cuboid(-1.0F, -5.1F, -12.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
			.uv(72, 61).cuboid(-1.025F, -3.1F, -12.0F, 0.0F, 5.0F, 1.0F, new Dilation(0.0F))
			.uv(90, 100).cuboid(1.025F, -3.1F, -12.0F, 0.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData right_hinge = hinges.addChild("right_hinge", ModelPartBuilder.create().uv(100, 78).cuboid(0.0F, -0.5F, -5.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F))
			.uv(92, 99).cuboid(1.4F, 1.9F, -6.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(-4.0F, 0.0F, 0.0F));

		right_hinge.addChild("cube_r1", ModelPartBuilder.create().uv(54, 96).cuboid(-1.0F, 0.0F, -6.0F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 0.5F, 1.0F, 0.0F, 0.0F, -0.7854F));
		right_hinge.addChild("cube_r2", ModelPartBuilder.create().uv(108, 42).cuboid(-1.0F, -3.0F, -1.5F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -0.5F, -3.5F, 0.0F, 0.0F, 0.5236F));

		ModelPartData left_hinge = hinges.addChild("left_hinge", ModelPartBuilder.create().uv(0, 101).cuboid(-1.0F, -0.5F, -5.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F))
			.uv(98, 9).cuboid(-2.4F, 1.9F, -6.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(4.0F, 0.0F, 0.0F));

		left_hinge.addChild("cube_r3", ModelPartBuilder.create().uv(100, 61).cuboid(0.0F, 0.0F, -6.0F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.5F, 1.0F, 0.0F, 0.0F, 0.7854F));
		left_hinge.addChild("cube_r4", ModelPartBuilder.create().uv(46, 107).cuboid(0.0F, -3.0F, -1.5F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -0.5F, -3.5F, 0.0F, 0.0F, -0.5236F));

		ModelPartData barrel = gun.addChild("barrel", ModelPartBuilder.create().uv(32, 107).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 3.0F, new Dilation(0.0F))
			.uv(54, 34).cuboid(-1.0F, -1.0F, -25.0F, 2.0F, 2.0F, 25.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -1.0F, -1.0F));

		barrel.addChild("inner_barrel", ModelPartBuilder.create().uv(40, 67).cuboid(-1.0F, -1.0F, -14.0F, 2.0F, 2.0F, 15.0F, new Dilation(-0.25F)), ModelTransform.origin(0.0F, 0.0F, -25.0F));
		barrel.addChild("flak_round_casing", ModelPartBuilder.create().uv(89, 108).cuboid(-2.0F, -2.0F, -7.0F, 4.0F, 4.0F, 8.0F, new Dilation(-1.5F)), ModelTransform.origin(0.0F, 0.0F, 3.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	@Override
	public Queue<? extends Keyframe> getDeathAnimProcedureInstance() {
		if (MathUtil.randomBool(25)) {
			return new PriorityQueue<>(DEATH_KEYFRAMES);
		}

		return BaseTurretModel.DEFAULT_EMPTY_KEYFRAME;
	}

	@Override
	protected void additionalDeathAnimProcedures(AnimationState animState, BaseTurretRenderState state) {
		super.additionalDeathAnimProcedures(animState, state);
	}

	@Override
	protected float getDefaultHeadPitch() {
		return 30f;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return 0f;
	}

	@Override
	protected float getMaxPitch() {
		return 90f;
	}

	// ////// //
	// STATIC //
	// ////// //
	static {
		DEATH_KEYFRAMES = new PriorityQueue<>() {
			{
				add(FXKeyframe.of(0.0, ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), new Vec3d(0, 0, -1.125)));
				add(FXKeyframe.of(0.5, ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), new Vec3d(0, 0, 0.3125)));
				add(ScriptKeyframe.of(0.5, EXPLODE_SCRIPT, new Vec3d(0, 0, 0.3125)));
			}
		};
	}
}
