package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

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
			FlameTurretAnimation.ANIM_FLAME_DEATH,
			2f
		);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		PartDefinition base = modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 33).addBox(-8.0F, -10.0F, -8.0F, 16.0F, 8.0F, 16.0F, new CubeDeformation(0.0F))
			.texOffs(0, 0).addBox(-16.0F, -0.075F, -16.0F, 32.0F, 1.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition stand = base.addOrReplaceChild("stand", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		stand.addOrReplaceChild("stand_r1", CubeListBuilder.create().texOffs(56, 82).addBox(-1.0F, -1.0F, -8.0F, 3.0F, 9.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.0472F));
		stand.addOrReplaceChild("stand_r2", CubeListBuilder.create().texOffs(56, 57).addBox(-2.0F, -1.0F, -8.0F, 3.0F, 9.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -2.0F, 0.0F, 0.0F, 0.0F, 1.0472F));
		stand.addOrReplaceChild("stand_r3", CubeListBuilder.create().texOffs(90, 82).addBox(-8.0F, -1.0F, -1.0F, 16.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 8.0F, 1.0472F, 0.0F, 0.0F));
		stand.addOrReplaceChild("stand_r4", CubeListBuilder.create().texOffs(90, 61).addBox(-8.0F, -1.0F, -2.0F, 16.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -8.0F, -1.0472F, 0.0F, 0.0F));

		PartDefinition neck = base.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, 0.0F));
		neck.addOrReplaceChild("neck_r1", CubeListBuilder.create().texOffs(0, 97).addBox(0.0F, -3.0F, -7.0F, 1.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		neck.addOrReplaceChild("neck_r2", CubeListBuilder.create().texOffs(98, 37).addBox(-7.0F, -3.0F, -1.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 8.0F, 0.3491F, 0.0F, 0.0F));
		neck.addOrReplaceChild("neck_r3", CubeListBuilder.create().texOffs(98, 33).addBox(-7.0F, -3.0F, 0.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -8.0F, -0.3491F, 0.0F, 0.0F));
		neck.addOrReplaceChild("neck_r4", CubeListBuilder.create().texOffs(94, 94).addBox(-1.0F, -3.0F, -7.0F, 1.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 57).addBox(-7.0F, -15.0F, -7.0F, 14.0F, 14.0F, 14.0F, new CubeDeformation(0.0F))
			.texOffs(0, 85).addBox(-5.0F, -17.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
			.texOffs(40, 92).addBox(-2.0F, -11.0F, -9.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition nozzle = head.addOrReplaceChild("nozzle", CubeListBuilder.create().texOffs(64, 33).addBox(-1.0F, -1.0F, -18.0F, 2.0F, 2.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, -9.0F));

		PartDefinition lighter = nozzle.addOrReplaceChild("lighter", CubeListBuilder.create().texOffs(92, 44).addBox(-0.5F, 3.882F, -19.1391F, 1.0F, 1.0F, 16.0F, new CubeDeformation(0.025F))
			.texOffs(30, 97).addBox(-1.5F, -1.443F, -14.1391F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(64, 54).addBox(-0.5F, 3.882F, -14.1391F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
			.texOffs(52, 92).addBox(-0.5F, 1.557F, -14.1391F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		lighter.addOrReplaceChild("lighter_r1", CubeListBuilder.create().texOffs(40, 85).addBox(-0.5F, -0.5F, -5.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition tip = lighter.addOrReplaceChild("tip", CubeListBuilder.create(), PartPose.offset(0.0F, 4.25F, -18.5F));
		tip.addOrReplaceChild("tip_r1", CubeListBuilder.create().texOffs(52, 96).addBox(-0.5F, -2.75F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

		return LayerDefinition.create(modelData, 128, 128);
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
				add(FXKeyframe.of(0.0, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.0, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.0, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.0, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.25, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.25, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.25, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.25, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.375, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.375, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.375, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.375, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.5, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), new Vec3(0, 0, 0.3125)));
				add(FXKeyframe.of(0.5, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.5, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.5, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.625, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), new Vec3(0, 0, 0.3125)));
				add(FXKeyframe.of(0.625, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.625, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(FXKeyframe.of(0.625, ParticleTypes.FLAME, new Vec3(MathUtil.random(-1.125, 1.125), 0, MathUtil.random(-1.125, 1.125))));
				add(ScriptKeyframe.of(0.75, EXPLODE_SCRIPT, new Vec3(0, 0, 0.3125)));
			}
		};
	}
}
