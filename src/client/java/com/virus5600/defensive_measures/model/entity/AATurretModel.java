package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.animations.FXKeyframe;
import com.virus5600.defensive_measures.animations.Keyframe;
import com.virus5600.defensive_measures.animations.ScriptKeyframe;
import com.virus5600.defensive_measures.animations.entity.AATurretAnimation;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import java.util.PriorityQueue;
import java.util.Queue;

import static com.virus5600.defensive_measures.animations.KeyframeScripts.EXPLODE_SCRIPT;

public class AATurretModel extends BaseTurretModel<BaseTurretRenderState> {
	private final static Queue<? extends Keyframe> SHOOT_KEYFRAMES;
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
			AATurretAnimation.ANIM_AA_TURRET_DEATH,
			2f
		);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		PartDefinition base = modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -16.0F, 4.0F, 2.0F, 32.0F, new CubeDeformation(0.0F))
			.texOffs(0, 61).addBox(-16.0F, -4.0F, -2.0F, 32.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		base.addOrReplaceChild("base_stand", CubeListBuilder.create().texOffs(20, 91).addBox(14.0F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(16, 101).addBox(-0.5F, -2.0F, -15.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(86, 105).addBox(-0.5F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(86, 100).addBox(-15.0F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(108, 0).addBox(13.0F, 2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
			.texOffs(108, 4).addBox(-16.0F, 2.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
			.texOffs(108, 34).addBox(-1.5F, 2.0F, 13.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
			.texOffs(108, 38).addBox(-1.5F, 2.0F, -16.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition stand = base.addOrReplaceChild("stand", CubeListBuilder.create().texOffs(0, 67).addBox(-5.0F, -0.25F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		stand.addOrReplaceChild("stand_r1", CubeListBuilder.create().texOffs(16, 107).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.2618F, 0.0F, 0.0F));
		stand.addOrReplaceChild("stand_r2", CubeListBuilder.create().texOffs(54, 105).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.2618F, 0.0F, 0.0F));
		stand.addOrReplaceChild("stand_r3", CubeListBuilder.create().texOffs(38, 96).addBox(-1.0F, -5.0F, -3.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));
		stand.addOrReplaceChild("stand_r4", CubeListBuilder.create().texOffs(96, 23).addBox(-1.0F, -5.0F, -3.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition horizontal_traverse = base.addOrReplaceChild("horizontal_traverse", CubeListBuilder.create().texOffs(72, 0).addBox(-5.0F, -1.0F, -4.0F, 10.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(72, 32).addBox(-4.0F, -2.0F, 3.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(98, 18).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(72, 9).addBox(4.0F, -8.0F, -1.0F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
			.texOffs(92, 88).addBox(-5.0F, -4.0F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(48, 84).addBox(-5.0F, -6.0F, -2.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
			.texOffs(74, 61).addBox(-5.0F, -8.0F, -1.0F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
			.texOffs(20, 96).addBox(4.0F, -4.0F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(70, 88).addBox(4.0F, -6.0F, -2.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition balancer = horizontal_traverse.addOrReplaceChild("balancer", CubeListBuilder.create().texOffs(0, 79).addBox(2.0F, -4.0F, -9.0F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.25F))
			.texOffs(24, 84).addBox(-3.0F, -4.0F, -9.0F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.25F))
			.texOffs(70, 84).addBox(2.0F, -3.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(104, 20).addBox(-3.0F, -3.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		balancer.addOrReplaceChild("springs", CubeListBuilder.create().texOffs(70, 100).addBox(2.0F, -0.5F, 0.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
			.texOffs(100, 70).addBox(-3.0F, -0.5F, 0.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 2.0F));

		PartDefinition gun = horizontal_traverse.addOrReplaceChild("gun", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, 1.0F, -19.0F, 2.0F, 2.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.5F, 8.5F, -0.5236F, 0.0F, 0.0F));

		PartDefinition hinges = gun.addOrReplaceChild("hinges", CubeListBuilder.create().texOffs(72, 23).addBox(-2.0F, 2.9F, -6.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(0, 91).addBox(-1.0F, -5.1F, -6.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(24, 79).addBox(-2.0F, -3.8F, -5.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.1F))
			.texOffs(74, 75).addBox(-1.0F, -5.1F, -16.0F, 2.0F, 2.0F, 11.0F, new CubeDeformation(-0.25F))
			.texOffs(98, 20).addBox(-1.0F, -5.1F, -12.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(72, 61).addBox(-1.025F, -3.1F, -12.0F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(90, 100).addBox(1.025F, -3.1F, -12.0F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_hinge = hinges.addOrReplaceChild("right_hinge", CubeListBuilder.create().texOffs(100, 78).addBox(0.0F, -0.5F, -5.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
			.texOffs(92, 99).addBox(1.4F, 1.9F, -6.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, 0.0F));

		right_hinge.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(54, 96).addBox(-1.0F, 0.0F, -6.0F, 1.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.5F, 1.0F, 0.0F, 0.0F, -0.7854F));
		right_hinge.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(108, 42).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -0.5F, -3.5F, 0.0F, 0.0F, 0.5236F));

		PartDefinition left_hinge = hinges.addOrReplaceChild("left_hinge", CubeListBuilder.create().texOffs(0, 101).addBox(-1.0F, -0.5F, -5.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
			.texOffs(98, 9).addBox(-2.4F, 1.9F, -6.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, 0.0F));

		left_hinge.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(100, 61).addBox(0.0F, 0.0F, -6.0F, 1.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.5F, 1.0F, 0.0F, 0.0F, 0.7854F));
		left_hinge.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(46, 107).addBox(0.0F, -3.0F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -0.5F, -3.5F, 0.0F, 0.0F, -0.5236F));

		PartDefinition barrel = gun.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(32, 107).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
			.texOffs(54, 34).addBox(-1.0F, -1.0F, -25.0F, 2.0F, 2.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -1.0F));

		barrel.addOrReplaceChild("inner_barrel", CubeListBuilder.create().texOffs(40, 67).addBox(-1.0F, -1.0F, -14.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 0.0F, -25.0F));
		barrel.addOrReplaceChild("flak_round_casing", CubeListBuilder.create().texOffs(89, 108).addBox(-2.0F, -2.0F, -7.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(-1.5F)), PartPose.offset(0.0F, 0.0F, 3.0F));

		return LayerDefinition.create(modelData, 128, 128);
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	@Override
	public Queue<? extends Keyframe> getShootAnimProcedureInstance() {
		return new PriorityQueue<>(SHOOT_KEYFRAMES);
	}

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
		SHOOT_KEYFRAMES = new PriorityQueue<>() {
			{
				add(ScriptKeyframe.of(0.0, ((animState, state, pos) -> {
					ClientLevel world = Minecraft.getInstance().level;

					if (world != null) {
						Vec3 targetPos = MathUtil.getRelativePos(
							state.eyePos, state.currentBarrelPos,
							-state.yRot, -state.xRot
						);

						Vec3 velMod = MathUtil.getRelativePos(
							state.eyePos,
							0, 0, 1.5,
							-state.yRot, -state.xRot
						).subtract(state.eyePos);

						world.addParticle(
							ModParticles.CANNON_FLASH,
							targetPos.x(), targetPos.y(), targetPos.z(),
							velMod.x(), velMod.y(), velMod.z()
						);
					}
				})));
			}
		};

		DEATH_KEYFRAMES = new PriorityQueue<>() {
			{
				add(FXKeyframe.of(0.0, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), new Vec3(0, 0, -1.125)));
				add(FXKeyframe.of(0.5, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), new Vec3(0, 0, 0.3125)));
				add(ScriptKeyframe.of(0.5, EXPLODE_SCRIPT, new Vec3(0, 0, 0.3125)));
			}
		};
	}
}
