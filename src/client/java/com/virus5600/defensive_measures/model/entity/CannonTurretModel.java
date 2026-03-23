package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

import com.virus5600.defensive_measures.animations.entity.CannonTurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class CannonTurretModel extends EntityModel<BaseTurretRenderState> {
	private final ModelPart stand;
	private final ModelPart head;

	private final Animation shootAnim;
	private final Animation deathAnim;

	public CannonTurretModel(ModelPart root) {
		super(root);

		this.stand = root.getChild("stand");
		this.head = stand.getChild("head");

		this.shootAnim = CannonTurretAnimation.ANIM_CANNON_SHOOT.createAnimation(root);
		this.deathAnim = CannonTurretAnimation.ANIM_CANNON_DEATH.createAnimation(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 47).cuboid(-8.0F, -1.0F, -8.0F, 16.0F, 1.0F, 16.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData stand = modelPartData.addChild("stand", ModelPartBuilder.create().uv(8, 33).cuboid(-6.0F, -1.5F, -6.0F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 23.5F, 0.0F));

		ModelPartData rightStand = stand.addChild("rightStand", ModelPartBuilder.create().uv(42, 33).cuboid(0.0F, -1.0F, -6.0F, 1.0F, 1.0F, 10.0F, new Dilation(0.0F))
			.uv(44, 34).cuboid(0.0F, -2.0F, -5.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
			.uv(48, 35).cuboid(0.0F, -3.0F, -4.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F))
			.uv(50, 33).cuboid(0.0F, -4.0F, -3.0F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
			.uv(54, 33).cuboid(0.0F, -5.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
			.uv(58, 39).cuboid(0.0F, -6.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(-6.0F, -1.5F, 1.0F));

		ModelPartData leftStand = stand.addChild("leftStand", ModelPartBuilder.create().uv(42, 33).mirrored().cuboid(-1.0F, -1.0F, -6.0F, 1.0F, 1.0F, 10.0F, new Dilation(0.0F)).mirrored(false)
			.uv(44, 34).cuboid(-1.0F, -2.0F, -5.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
			.uv(48, 35).mirrored().cuboid(-1.0F, -3.0F, -4.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
			.uv(50, 33).mirrored().cuboid(-1.0F, -4.0F, -3.0F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
			.uv(54, 33).mirrored().cuboid(-1.0F, -5.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
			.uv(58, 39).cuboid(-1.0F, -6.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(6.0F, -1.5F, 1.0F));

		ModelPartData head = stand.addChild("head", ModelPartBuilder.create().uv(12, 0).cuboid(-5.0F, -4.5F, -3.0F, 10.0F, 6.0F, 8.0F, new Dilation(0.0F))
			.uv(20, 0).cuboid(-4.0F, -5.5F, -8.0F, 8.0F, 8.0F, 14.0F, new Dilation(0.0F))
			.uv(0, 0).mirrored().cuboid(-3.0F, -6.5F, -3.0F, 6.0F, 10.0F, 8.0F, new Dilation(0.0F)).mirrored(false)
			.uv(16, 0).cuboid(-3.0F, -4.5F, -11.0F, 6.0F, 6.0F, 18.0F, new Dilation(0.0F))
			.uv(54, 0).cuboid(-2.0F, -3.5F, 7.0F, 4.0F, 4.0F, 1.0F, new Dilation(0.0F))
			.uv(56, 5).cuboid(-1.0F, -3.5F, -13.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(56, 10).cuboid(-1.0F, -0.5F, -13.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(48, 9).cuboid(1.0F, -3.5F, -13.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
			.uv(2, 2).mirrored().cuboid(-2.0F, -3.5F, -13.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
			.uv(24, 38).cuboid(0.0F, -3.0F, 8.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
			.uv(28, 37).cuboid(0.0F, -5.0F, 9.0F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -6.0F, -1.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(BaseTurretRenderState state) {
		super.setAngles(state);

		this.setHeadAngles(state.relativeHeadYaw + state.bodyYaw + 180, state.pitch);

		this.shootAnim.apply(state.shootAnimationState, state.age);
		this.deathAnim.apply(state.deathAnimationState, state.age);
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headPitch = MathHelper.clamp(headPitch, -30f, 30f);

		this.stand.yaw = headYaw * ((float)Math.PI / 180F);
		this.head.pitch = headPitch * ((float)Math.PI / 180F);
	}
}
