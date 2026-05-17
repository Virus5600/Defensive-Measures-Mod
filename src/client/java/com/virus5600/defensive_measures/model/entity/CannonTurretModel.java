package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.animations.entity.CannonTurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class CannonTurretModel extends BaseTurretModel<BaseTurretRenderState> {
	protected final static String[] TEXTURES = new String[]{
		"cannon_turret.png"
	};

	public CannonTurretModel(ModelPart root) {
		super(
			root, "cannon_turret", TEXTURES,

			root.getChild("stand"),
			root.getChild("stand").getChild("head"),

			CannonTurretAnimation.ANIM_CANNON_SHOOT.createAnimation(root),
			CannonTurretAnimation.ANIM_CANNON_DEATH.createAnimation(root)
		);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.0F, -8.0F, 16.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData stand = modelPartData.addChild("stand", ModelPartBuilder.create().uv(44, 45).cuboid(-6.0F, -1.5F, -6.0F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 23.5F, 0.0F));
		stand.addChild("stand_r1", ModelPartBuilder.create().uv(54, 32).cuboid(-4.975F, 0.0F, -1.0F, 10.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.6572F, -5.6744F, 0.8727F, 0.0F, 0.0F));

		ModelPartData right_stand = stand.addChild("right_stand", ModelPartBuilder.create().uv(26, 67).cuboid(0.0F, -9.0F, 0.0F, 1.0F, 9.0F, 3.0F, new Dilation(0.0F))
			.uv(76, 32).cuboid(0.0F, -4.0F, -3.0F, 1.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(-6.0F, -1.5F, 1.0F));
		right_stand.addChild("right_stand_r1", ModelPartBuilder.create().uv(55, 0).cuboid(-0.025F, -2.0F, -9.0F, 1.0F, 3.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 0.0F, 0.8727F, 0.0F, 0.0F));

		ModelPartData left_stand = stand.addChild("left_stand", ModelPartBuilder.create().uv(34, 67).cuboid(-1.0F, -9.0F, 0.0F, 1.0F, 9.0F, 3.0F, new Dilation(0.0F))
			.uv(42, 76).cuboid(-1.0F, -4.0F, -3.0F, 1.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(6.0F, -1.5F, 1.0F));
		left_stand.addChild("left_stand_r1", ModelPartBuilder.create().uv(0, 67).cuboid(-0.975F, -2.0F, -9.0F, 1.0F, 3.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 0.0F, 0.8727F, 0.0F, 0.0F));

		stand.addChild("head", ModelPartBuilder.create().uv(54, 18).cuboid(-5.0F, -4.5F, -3.0F, 10.0F, 6.0F, 8.0F, new Dilation(0.0F))
			.uv(0, 45).cuboid(-4.0F, -5.5F, -8.0F, 8.0F, 8.0F, 14.0F, new Dilation(0.0F))
			.uv(44, 58).cuboid(-3.0F, -6.5F, -3.0F, 6.0F, 10.0F, 8.0F, new Dilation(0.0F))
			.uv(0, 18).cuboid(-3.0F, -4.5F, -13.0F, 6.0F, 6.0F, 21.0F, new Dilation(0.0F))
			.uv(54, 40).cuboid(-2.0F, -3.5F, 8.0F, 4.0F, 4.0F, 1.0F, new Dilation(0.0F))
			.uv(64, 15).cuboid(-1.0F, -3.5F, -15.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(64, 40).cuboid(-1.0F, -0.5F, -15.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(76, 39).cuboid(1.0F, -3.5F, -15.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
			.uv(50, 76).cuboid(-2.0F, -3.5F, -15.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
			.uv(42, 67).cuboid(0.0F, -5.0F, 9.0F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, -1.0F, -0.2618F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -20.0f;
	}

	@Override
	protected float getMaxPitch() {
		return 22.5f;
	}
}
