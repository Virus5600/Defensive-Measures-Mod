package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

import com.virus5600.defensive_measures.animations.entity.BallistaTurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class BallistaTurretModel extends EntityModel<BaseTurretRenderState> {

	private final ModelPart head;
	private final ModelPart bow;

	private final Animation idleAnim;
	private final Animation shootAnim;
	private final Animation deathAnim;

	public BallistaTurretModel(ModelPart root) {
		super(root);

		ModelPart base = root.getChild("base");
		this.head = base.getChild("head");
		this.bow = this.head.getChild("bow");

		this.idleAnim = BallistaTurretAnimation.ANIM_SETUP.createAnimation(root);
		this.shootAnim = BallistaTurretAnimation.ANIM_SHOOT.createAnimation(root);
		this.deathAnim = BallistaTurretAnimation.ANIM_DEATH.createAnimation(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(56, 46).cuboid(-8.0F, -1.0F, -1.0F, 7.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(56, 49).cuboid(1.0F, -1.0F, -1.0F, 7.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData base_r1 = base.addChild("base_r1", ModelPartBuilder.create().uv(36, 49).cuboid(0.0F, -1.0F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData base_r2 = base.addChild("base_r2", ModelPartBuilder.create().uv(36, 46).cuboid(0.0F, -1.0F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData pedestal = base.addChild("pedestal", ModelPartBuilder.create().uv(46, 58).cuboid(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -1.0F, 0.0F));

		ModelPartData head = base.addChild("head", ModelPartBuilder.create().uv(56, 52).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
			.uv(26, 58).cuboid(-3.0F, -5.0F, -2.0F, 1.0F, 5.0F, 4.0F, new Dilation(0.0F))
			.uv(36, 58).cuboid(2.0F, -5.0F, -2.0F, 1.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -8.0F, 0.0F));

		ModelPartData bow = head.addChild("bow", ModelPartBuilder.create().uv(38, 67).cuboid(-3.5F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(46, 67).cuboid(1.5F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(40, 18).cuboid(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 13.0F, new Dilation(0.0F))
			.uv(66, 57).cuboid(-1.0F, 0.0F, -8.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.0F))
			.uv(0, 40).cuboid(1.0F, -1.0F, -8.0F, 1.0F, 1.0F, 17.0F, new Dilation(0.0F))
			.uv(40, 0).cuboid(-2.0F, -1.0F, -8.0F, 1.0F, 1.0F, 17.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -3.0F, 0.0F));

		ModelPartData bowBlade = bow.addChild("bowBlade", ModelPartBuilder.create().uv(56, 57).cuboid(1.0F, -4.0F, -1.0F, 3.0F, 9.0F, 2.0F, new Dilation(0.0F))
			.uv(0, 58).cuboid(-4.0F, -4.0F, -1.0F, 3.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -1.0F, -5.0F));

		ModelPartData innerLeftBlade = bowBlade.addChild("innerLeftBlade", ModelPartBuilder.create().uv(72, 25).cuboid(0.0F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(4.0F, -0.5F, 0.5F));

		ModelPartData outerLeftBlade = innerLeftBlade.addChild("outerLeftBlade", ModelPartBuilder.create().uv(0, 69).cuboid(0.0F, -0.5F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(3.0F, 0.0F, 0.0F));

		ModelPartData leftBladeTarget = outerLeftBlade.addChild("leftBladeTarget", ModelPartBuilder.create(), ModelTransform.origin(4.0F, 0.0F, 0.0F));

		ModelPartData innerRightBlade = bowBlade.addChild("innerRightBlade", ModelPartBuilder.create().uv(72, 22).cuboid(-3.0F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(-4.0F, -0.5F, 0.5F));

		ModelPartData outerRightBlade = innerRightBlade.addChild("outerRightBlade", ModelPartBuilder.create().uv(54, 68).cuboid(-4.0F, -0.5F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(-3.0F, 0.0F, 0.0F));

		ModelPartData rightBladeTarget = outerRightBlade.addChild("rightBladeTarget", ModelPartBuilder.create(), ModelTransform.origin(-4.0F, 0.0F, 0.0F));

		ModelPartData pulley = bow.addChild("pulley", ModelPartBuilder.create().uv(16, 72).cuboid(1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(72, 28).cuboid(-2.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 9.0F));

		ModelPartData winch = pulley.addChild("winch", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -1.0F, 1.0F));

		ModelPartData rope = winch.addChild("rope", ModelPartBuilder.create().uv(72, 52).cuboid(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData lever = winch.addChild("lever", ModelPartBuilder.create().uv(72, 32).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(22, 67).cuboid(0.5F, -2.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F))
			.uv(10, 63).cuboid(-5.5F, -0.5F, -0.5F, 7.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(3.0F, 0.0F, 0.0F));

		ModelPartData ram = bow.addChild("ram", ModelPartBuilder.create().uv(72, 40).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -1.0F, 7.0F));

		ModelPartData rearDrawString = ram.addChild("rearDrawString", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -0.5F, 0.5F));

		ModelPartData leftRearDrawString = rearDrawString.addChild("leftRearDrawString", ModelPartBuilder.create().uv(36, 41).cuboid(0.0F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(1.0F, 0.0F, 0.0F));

		ModelPartData rightRearDrawString = rearDrawString.addChild("rightRearDrawString", ModelPartBuilder.create().uv(36, 40).cuboid(-1.0F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(-1.0F, 0.0F, 0.0F));

		ModelPartData leftString = ram.addChild("leftString", ModelPartBuilder.create().uv(66, 62).cuboid(-0.25F, 0.0F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(2.0F, -0.5F, 0.5F));

		ModelPartData leftStringTarget = leftString.addChild("leftStringTarget", ModelPartBuilder.create(), ModelTransform.origin(6.75F, 0.0F, 0.0F));

		ModelPartData rightString = ram.addChild("rightString", ModelPartBuilder.create().uv(66, 63).cuboid(-6.75F, 0.0F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(-2.0F, -0.5F, 0.5F));

		ModelPartData rightStringTarget = rightString.addChild("rightStringTarget", ModelPartBuilder.create(), ModelTransform.origin(-6.75F, 0.0F, 0.0F));

		ModelPartData bolt = bow.addChild("bolt", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, -0.25F, 1.0F, 1.0F, 19.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -0.5F, -11.25F));

		ModelPartData point = bolt.addChild("point", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 2.075F));

		ModelPartData point_r1 = point.addChild("point_r1", ModelPartBuilder.create().uv(46, 71).cuboid(0.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 0.0F, 0.4189F, 0.0F));

		ModelPartData point_r2 = point.addChild("point_r2", ModelPartBuilder.create().uv(38, 71).cuboid(-1.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 0.0F, -0.4189F, 0.0F));

		ModelPartData point_r3 = point.addChild("point_r3", ModelPartBuilder.create().uv(0, 71).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, -0.4189F, 0.0F, 0.0F));

		ModelPartData point_r4 = point.addChild("point_r4", ModelPartBuilder.create().uv(54, 70).cuboid(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 0.4189F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void setAngles(BaseTurretRenderState state) {
		super.setAngles(state);

		this.setHeadAngles(state.relativeHeadYaw + state.bodyYaw + 180, state.pitch);

		this.idleAnim.apply(state.idleAnimationState, state.age);
		this.shootAnim.apply(state.shootAnimationState, state.age);
		this.deathAnim.apply(state.deathAnimationState, state.age);
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headPitch = MathHelper.clamp(headPitch, -22.5f, 22.5f);

		this.head.yaw = headYaw * ((float) Math.PI / 180F);
		this.bow.pitch = headPitch * ((float) Math.PI / 180F);
	}
}
