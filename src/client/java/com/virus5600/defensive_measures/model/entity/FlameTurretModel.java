package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.animations.FXKeyframe;
import com.virus5600.defensive_measures.animations.Keyframe;
import com.virus5600.defensive_measures.animations.ScriptKeyframe;
import com.virus5600.defensive_measures.animations.entity.FlameTurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import java.util.PriorityQueue;
import java.util.Queue;

import static com.virus5600.defensive_measures.animations.KeyframeScripts.EXPLODE_SCRIPT;

public class FlameTurretModel extends BaseTurretModel<BaseTurretRenderState> {
	private final static Queue<? extends Keyframe> DEATH_KEYFRAMES;

	protected final static String[] TEXTURES = new String[]{
		"flame_turret.png"
	};

	public FlameTurretModel(ModelPart root) {
		super(
			root, "flame_turret", TEXTURES,

			root.getChild("base").getChild("neck"),
			root.getChild("base").getChild("neck").getChild("head").getChild("nozzle"),

			FlameTurretAnimation.ANIM_FLAME_SHOOT,
			FlameTurretAnimation.ANIM_FLAME_DEATH
		);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 33).cuboid(-8.0F, -10.0F, -8.0F, 16.0F, 8.0F, 16.0F, new Dilation(0.0F))
			.uv(0, 0).cuboid(-16.0F, -0.075F, -16.0F, 32.0F, 1.0F, 32.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData stand = base.addChild("stand", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		stand.addChild("stand_r1", ModelPartBuilder.create().uv(56, 82).cuboid(-1.0F, -1.0F, -8.0F, 3.0F, 9.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.0472F));
		stand.addChild("stand_r2", ModelPartBuilder.create().uv(56, 57).cuboid(-2.0F, -1.0F, -8.0F, 3.0F, 9.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -2.0F, 0.0F, 0.0F, 0.0F, 1.0472F));
		stand.addChild("stand_r3", ModelPartBuilder.create().uv(90, 82).cuboid(-8.0F, -1.0F, -1.0F, 16.0F, 9.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 8.0F, 1.0472F, 0.0F, 0.0F));
		stand.addChild("stand_r4", ModelPartBuilder.create().uv(90, 61).cuboid(-8.0F, -1.0F, -2.0F, 16.0F, 9.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, -8.0F, -1.0472F, 0.0F, 0.0F));

		ModelPartData neck = base.addChild("neck", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -11.0F, 0.0F));
		neck.addChild("neck_r1", ModelPartBuilder.create().uv(0, 97).cuboid(0.0F, -3.0F, -7.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		neck.addChild("neck_r2", ModelPartBuilder.create().uv(98, 37).cuboid(-7.0F, -3.0F, -1.0F, 14.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 8.0F, 0.3491F, 0.0F, 0.0F));
		neck.addChild("neck_r3", ModelPartBuilder.create().uv(98, 33).cuboid(-7.0F, -3.0F, 0.0F, 14.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -8.0F, -0.3491F, 0.0F, 0.0F));
		neck.addChild("neck_r4", ModelPartBuilder.create().uv(94, 94).cuboid(-1.0F, -3.0F, -7.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		ModelPartData head = neck.addChild("head", ModelPartBuilder.create().uv(0, 57).cuboid(-7.0F, -15.0F, -7.0F, 14.0F, 14.0F, 14.0F, new Dilation(0.0F))
			.uv(0, 85).cuboid(-5.0F, -17.0F, -5.0F, 10.0F, 2.0F, 10.0F, new Dilation(0.0F))
			.uv(40, 92).cuboid(-2.0F, -11.0F, -9.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 1.0F, 0.0F));

		ModelPartData nozzle = head.addChild("nozzle", ModelPartBuilder.create().uv(64, 33).cuboid(-1.0F, -1.0F, -18.0F, 2.0F, 2.0F, 19.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -9.0F, -9.0F));

		ModelPartData lighter = nozzle.addChild("lighter", ModelPartBuilder.create().uv(92, 44).cuboid(-0.5F, 3.882F, -19.1391F, 1.0F, 1.0F, 16.0F, new Dilation(0.025F))
			.uv(30, 97).cuboid(-1.5F, -1.443F, -14.1391F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
			.uv(64, 54).cuboid(-0.5F, 3.882F, -14.1391F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F))
			.uv(52, 92).cuboid(-0.5F, 1.557F, -14.1391F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		lighter.addChild("lighter_r1", ModelPartBuilder.create().uv(40, 85).cuboid(-0.5F, -0.5F, -5.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData tip = lighter.addChild("tip", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 4.25F, -18.5F));
		tip.addChild("tip_r1", ModelPartBuilder.create().uv(52, 96).cuboid(-0.5F, -2.75F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	@Override
	public Queue<? extends Keyframe> getDeathAnimProcedureInstance() {
		if (MathUtil.randomBool(50)) {
			return new PriorityQueue<>(DEATH_KEYFRAMES);
		}

		return BaseTurretModel.DEFAULT_EMPTY_KEYFRAME;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -30.0f;
	}

	@Override
	protected float getMaxPitch() {
		return 22.5f;
	}

	// ////// //
	// STATIC //
	// ////// //
	static {
		DEATH_KEYFRAMES = new PriorityQueue<>() {
			{
				add(FXKeyframe.of(0.0, ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.0, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.0, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.0, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.25, ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.25, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.25, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.25, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.375, ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.375, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.375, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.375, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.5, ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), new Vec3d(0, 0, 0.3125)));
				add(FXKeyframe.of(0.5, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.5, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.5, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.625, ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), new Vec3d(0, 0, 0.3125)));
				add(FXKeyframe.of(0.625, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.625, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.625, ParticleTypes.FLAME, new Vec3d(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(ScriptKeyframe.of(0.75, EXPLODE_SCRIPT, new Vec3d(0, 0, 0.3125)));
			}
		};
	}
}
