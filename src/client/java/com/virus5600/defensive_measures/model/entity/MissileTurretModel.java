package com.virus5600.defensive_measures.model.entity;

import com.virus5600.defensive_measures.particle.ModParticles;
import net.minecraft.client.model.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.animations.FXKeyframe;
import com.virus5600.defensive_measures.animations.Keyframe;
import com.virus5600.defensive_measures.animations.entity.MissileTurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.MissileTurretRenderState;

import java.util.PriorityQueue;
import java.util.Queue;


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

			null,
			MissileTurretAnimation.ANIM_MISSILE_TURRET_DEATH
		);

		this.RADAR_DISH = root.getChild("base")
			.getChild("stand")
			.getChild("column")
			.getChild("swivel")
			.getChild("head")
			.getChild("radar")
			.getChild("radar_dish");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(104, 68).cuboid(-16.0F, -2.0F, -10.0F, 4.0F, 2.0F, 20.0F, new Dilation(0.0F))
			.uv(122, 31).cuboid(12.0F, -2.0F, -10.0F, 4.0F, 2.0F, 20.0F, new Dilation(0.0F))
			.uv(122, 53).cuboid(-10.0F, -2.0F, 12.0F, 20.0F, 2.0F, 4.0F, new Dilation(0.0F))
			.uv(122, 59).cuboid(-10.0F, -2.0F, -16.0F, 20.0F, 2.0F, 4.0F, new Dilation(0.0F))
			.uv(88, 126).cuboid(-10.0F, -3.0F, -12.0F, 20.0F, 3.0F, 2.0F, new Dilation(0.0F))
			.uv(52, 68).cuboid(-12.0F, -3.0F, -12.0F, 2.0F, 3.0F, 24.0F, new Dilation(0.0F))
			.uv(0, 68).cuboid(10.0F, -3.0F, -12.0F, 2.0F, 3.0F, 24.0F, new Dilation(0.0F))
			.uv(104, 90).cuboid(-10.0F, -3.0F, 10.0F, 20.0F, 3.0F, 2.0F, new Dilation(0.0F))
			.uv(0, 0).cuboid(-10.0F, -2.0F, -10.0F, 20.0F, 2.0F, 20.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData stand = base.addChild("stand", ModelPartBuilder.create().uv(0, 51).cuboid(-8.0F, -3.0F, -8.0F, 16.0F, 1.0F, 16.0F, new Dilation(0.0F))
			.uv(80, 0).cuboid(-4.0F, -5.0F, -4.0F, 8.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData column = stand.addChild("column", ModelPartBuilder.create().uv(0, 128).cuboid(-2.0F, -10.0F, -2.0F, 4.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -5.0F, 0.0F));

		ModelPartData swivel = column.addChild("swivel", ModelPartBuilder.create().uv(80, 10).cuboid(-4.0F, -2.0F, -4.0F, 8.0F, 2.0F, 8.0F, new Dilation(0.0F))
			.uv(56, 128).cuboid(6.1213F, -12.1213F, -2.0F, 2.0F, 8.0F, 4.0F, new Dilation(0.0F))
			.uv(44, 128).cuboid(-8.1213F, -12.1213F, -2.0F, 2.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -10.0F, 0.0F));
		swivel.addChild("swivel_r1", ModelPartBuilder.create().uv(82, 131).cuboid(-3.0F, 0.0F, -2.0F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.7854F));
		swivel.addChild("swivel_r2", ModelPartBuilder.create().uv(68, 128).cuboid(0.0F, 0.0F, -2.0F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		ModelPartData head = swivel.addChild("head", ModelPartBuilder.create().uv(44, 95).cuboid(-5.0F, -5.75F, -11.0F, 1.0F, 12.0F, 21.0F, new Dilation(0.0F))
			.uv(112, 0).cuboid(-6.0F, -4.75F, -11.0F, 1.0F, 10.0F, 21.0F, new Dilation(0.0F))
			.uv(0, 22).cuboid(-4.0F, -4.75F, -10.0F, 8.0F, 10.0F, 19.0F, new Dilation(0.0F))
			.uv(54, 22).cuboid(-4.0F, 5.25F, -11.0F, 8.0F, 2.0F, 21.0F, new Dilation(0.0F))
			.uv(64, 45).cuboid(-4.0F, -6.75F, -11.0F, 8.0F, 2.0F, 21.0F, new Dilation(0.0F))
			.uv(88, 95).cuboid(5.0F, -4.75F, -11.0F, 1.0F, 10.0F, 21.0F, new Dilation(0.0F))
			.uv(0, 95).cuboid(4.0F, -5.75F, -11.0F, 1.0F, 12.0F, 21.0F, new Dilation(0.0F))
			.uv(30, 128).cuboid(5.1F, -2.75F, -3.0F, 1.0F, 6.0F, 6.0F, new Dilation(0.0F))
			.uv(16, 128).cuboid(-6.1F, -2.75F, -3.0F, 1.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -10.25F, 0.0F));

		ModelPartData radar = head.addChild("radar", ModelPartBuilder.create().uv(0, 12).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
			.uv(8, 0).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(-0.375F)), ModelTransform.origin(0.0F, -6.75F, 6.0F));

		ModelPartData radar_dish = radar.addChild("radar_dish", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -3.125F, 0.0F));
		radar_dish.addChild("radar_dish_r1", ModelPartBuilder.create().uv(4, 0).cuboid(-2.75F, -1.5F, -2.125F, 1.0F, 3.0F, 1.0F, new Dilation(-0.38F))
			.uv(0, 0).cuboid(1.75F, -1.5F, -2.125F, 1.0F, 3.0F, 1.0F, new Dilation(-0.38F))
			.uv(0, 6).cuboid(-2.5F, 0.75F, -2.125F, 5.0F, 1.0F, 1.0F, new Dilation(-0.38F))
			.uv(0, 4).cuboid(-2.5F, -1.75F, -2.125F, 5.0F, 1.0F, 1.0F, new Dilation(-0.38F))
			.uv(0, 8).cuboid(-2.5F, -1.5F, -1.875F, 5.0F, 3.0F, 1.0F, new Dilation(-0.38F))
			.uv(12, 0).cuboid(-0.5F, -0.5F, -2.625F, 1.0F, 1.0F, 3.0F, new Dilation(-0.38F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.6109F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 256, 256);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void setAngles(MissileTurretRenderState state) {
		super.setAngles(state);

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
		float delta = (state.age * degreesPerTick) % 360f;
		float newAngle = MathUtil.degToRad(delta);
		this.RADAR_DISH.setAngles(0, newAngle, 0);
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
				add(FXKeyframe.of(0.0, ModParticles.CANNON_FLASH, new Vec3d(0, 0.1875, -0.5625)));
				add(FXKeyframe.of(0.165, ParticleTypes.CLOUD, new Vec3d(0, 0.1875, -0.5625)));

				add(FXKeyframe.of(0.33, ModParticles.CANNON_FLASH, new Vec3d(0, 0, -0.5625)));
				add(FXKeyframe.of(0.495, ParticleTypes.CLOUD, new Vec3d(0, 0, -0.5625)));

				add(FXKeyframe.of(0.66, ModParticles.CANNON_FLASH, new Vec3d(0, -0.1875, -0.5625)));
				add(FXKeyframe.of(0.825, ParticleTypes.CLOUD, new Vec3d(0, -0.1875, -0.5625)));
			}
		};
	}
}
