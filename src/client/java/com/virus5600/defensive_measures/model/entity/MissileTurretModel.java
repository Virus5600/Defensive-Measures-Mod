package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.animations.FXKeyframe;
import com.virus5600.defensive_measures.animations.Keyframe;
import com.virus5600.defensive_measures.animations.entity.MissileTurretAnimation;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.renderer.entity.state.MissileTurretRenderState;

import java.util.PriorityQueue;
import java.util.Queue;

import org.jspecify.annotations.NonNull;

public class MissileTurretModel extends BaseTurretModel<MissileTurretRenderState> {
	private final static Queue<? extends Keyframe> SHOOT_KEYFRAMES;
	private final ModelPart RADAR_DISH;

	protected final static String[] TEXTURES = new String[] {
		"missile_turret.png"
	};

	public MissileTurretModel(ModelPart root) {
		super(
			root, "missile_turret", TEXTURES,

			root.getChild("base").getChild("stand").getChild("column").getChild("swivel"),
			root.getChild("base").getChild("stand").getChild("column").getChild("swivel").getChild("head"),

			null, MissileTurretAnimation.ANIM_MISSILE_TURRET_DEATH,
			new AnimationDefinition[] {MissileTurretAnimation.ANIM_MISSILE_TURRET_SETUP},
			new AnimationDefinition[] {MissileTurretAnimation.ANIM_MISSILE_TURRET_TEARDOWN},
			2f
		);

		this.RADAR_DISH = root.getChild("base")
			.getChild("stand")
			.getChild("column")
			.getChild("swivel")
			.getChild("head")
			.getChild("radar")
			.getChild("upper_radar");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		PartDefinition base = modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(104, 68).addBox(-16.0F, -2.0F, -10.0F, 4.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
			.texOffs(122, 31).addBox(12.0F, -2.0F, -10.0F, 4.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
			.texOffs(122, 53).addBox(-10.0F, -2.0F, 12.0F, 20.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(122, 59).addBox(-10.0F, -2.0F, -16.0F, 20.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(88, 126).addBox(-10.0F, -3.0F, -12.0F, 20.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(52, 68).addBox(-12.0F, -3.0F, -12.0F, 2.0F, 3.0F, 24.0F, new CubeDeformation(0.0F))
			.texOffs(0, 68).addBox(10.0F, -3.0F, -12.0F, 2.0F, 3.0F, 24.0F, new CubeDeformation(0.0F))
			.texOffs(104, 90).addBox(-10.0F, -3.0F, 10.0F, 20.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(0, 0).addBox(-10.0F, -2.0F, -10.0F, 20.0F, 2.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition stand = base.addOrReplaceChild("stand", CubeListBuilder.create().texOffs(0, 51).addBox(-8.0F, -3.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
			.texOffs(80, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition column = stand.addOrReplaceChild("column", CubeListBuilder.create().texOffs(0, 128).addBox(-2.0F, -10.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition swivel = column.addOrReplaceChild("swivel", CubeListBuilder.create().texOffs(80, 10).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition swivel_right_arm = swivel.addOrReplaceChild("swivel_right_arm", CubeListBuilder.create(), PartPose.offset(-4.0F, -2.0F, 0.0F));

		swivel_right_arm.addOrReplaceChild("swivel_lower_right_arm_r1", CubeListBuilder.create().texOffs(82, 131).addBox(-3.0F, 0.0F, -2.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));
		swivel_right_arm.addOrReplaceChild("swivel_upper_right_arm", CubeListBuilder.create().texOffs(44, 128).addBox(-2.0F, -8.0F, -2.0F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.1213F, -2.1213F, 0.0F));

		PartDefinition swivel_left_arm = swivel.addOrReplaceChild("swivel_left_arm", CubeListBuilder.create(), PartPose.offset(4.0F, -2.0F, 0.0F));

		swivel_left_arm.addOrReplaceChild("swivel_lower_left_arm_r1", CubeListBuilder.create().texOffs(68, 128).addBox(0.0F, 0.0F, -2.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
		swivel_left_arm.addOrReplaceChild("swivel_upper_left_arm", CubeListBuilder.create().texOffs(56, 128).addBox(0.0F, -8.0F, -2.0F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.1213F, -2.1213F, 0.0F));

		PartDefinition head = swivel.addOrReplaceChild("head", CubeListBuilder.create().texOffs(44, 95).addBox(-5.0F, -5.75F, -11.0F, 1.0F, 12.0F, 21.0F, new CubeDeformation(0.0F))
			.texOffs(112, 0).addBox(-6.0F, -4.75F, -11.0F, 1.0F, 10.0F, 21.0F, new CubeDeformation(0.0F))
			.texOffs(0, 22).addBox(-4.0F, -4.75F, -10.0F, 8.0F, 10.0F, 19.0F, new CubeDeformation(0.0F))
			.texOffs(54, 22).addBox(-4.0F, 5.25F, -11.0F, 8.0F, 2.0F, 21.0F, new CubeDeformation(0.0F))
			.texOffs(64, 45).addBox(-4.0F, -6.75F, -11.0F, 8.0F, 2.0F, 21.0F, new CubeDeformation(0.0F))
			.texOffs(88, 95).addBox(5.0F, -4.75F, -11.0F, 1.0F, 10.0F, 21.0F, new CubeDeformation(0.0F))
			.texOffs(0, 95).addBox(4.0F, -5.75F, -11.0F, 1.0F, 12.0F, 21.0F, new CubeDeformation(0.0F))
			.texOffs(30, 128).addBox(5.1F, -2.75F, -3.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(16, 128).addBox(-6.1F, -2.75F, -3.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.25F, 0.0F));

		PartDefinition radar = head.addOrReplaceChild("radar", CubeListBuilder.create().texOffs(0, 12).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.75F, 6.0F));

		radar.addOrReplaceChild("radar_stand", CubeListBuilder.create().texOffs(8, 0).addBox(-0.5F, -2.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition upper_radar = radar.addOrReplaceChild("upper_radar", CubeListBuilder.create(), PartPose.offset(0.0F, -3.125F, 0.0F));

		upper_radar.addOrReplaceChild("upper_radar_r1", CubeListBuilder.create().texOffs(12, 0).addBox(-0.5F, -0.5F, -2.625F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.38F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition radar_dish = upper_radar.addOrReplaceChild("radar_dish", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		radar_dish.addOrReplaceChild("radar_dish_r1", CubeListBuilder.create().texOffs(4, 0).addBox(-2.75F, -1.5F, -2.125F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.38F))
			.texOffs(0, 0).addBox(1.75F, -1.5F, -2.125F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.38F))
			.texOffs(0, 6).addBox(-2.5F, 0.75F, -2.125F, 5.0F, 1.0F, 1.0F, new CubeDeformation(-0.38F))
			.texOffs(0, 4).addBox(-2.5F, -1.75F, -2.125F, 5.0F, 1.0F, 1.0F, new CubeDeformation(-0.38F))
			.texOffs(0, 8).addBox(-2.5F, -1.5F, -1.875F, 5.0F, 3.0F, 1.0F, new CubeDeformation(-0.38F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.6109F, 0.0F, 0.0F));

		return LayerDefinition.create(modelData, 256, 256);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void setupAnim(@NonNull MissileTurretRenderState state) {
		super.setupAnim(state);

		float maxRange = state.maxRange;
		float rpm;

		if (maxRange <= 32) {
			rpm = 60f;
		}
		else if (maxRange <= 64) {
			rpm = 40f;
		}
		else if (maxRange <= 96) {
			rpm = 20f;
		}
		else {
			rpm = 10f;
		}

		float degreesPerTick = rpm * 0.3f;
		float delta = (state.ageInTicks * degreesPerTick) % 360f;
		float newAngle = MathUtil.degToRad(delta);
		this.RADAR_DISH.setRotation(0, newAngle, 0);
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //
	public Queue<? extends Keyframe> getShootAnimProcedureInstance() {
		return new PriorityQueue<>(SHOOT_KEYFRAMES);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -12f;
	}

	@Override
	protected float getMaxPitch() {
		return 12f;
	}

	// ////// //
	// STATIC //
	// ////// //
	static {
		SHOOT_KEYFRAMES = new PriorityQueue<>() {
			{
				add(FXKeyframe.of(0.0, ModParticles.CANNON_FLASH, new Vec3(0, 0.1875, -0.5625)));
				add(FXKeyframe.of(0.165, ParticleTypes.CLOUD, new Vec3(0, 0.1875, -0.5625)));

				add(FXKeyframe.of(0.33, ModParticles.CANNON_FLASH, new Vec3(0, 0, -0.5625)));
				add(FXKeyframe.of(0.495, ParticleTypes.CLOUD, new Vec3(0, 0, -0.5625)));

				add(FXKeyframe.of(0.66, ModParticles.CANNON_FLASH, new Vec3(0, -0.1875, -0.5625)));
				add(FXKeyframe.of(0.825, ParticleTypes.CLOUD, new Vec3(0, -0.1875, -0.5625)));
			}
		};
	}
}
