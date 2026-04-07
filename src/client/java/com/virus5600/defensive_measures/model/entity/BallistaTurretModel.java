package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.animations.entity.BallistaTurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class BallistaTurretModel extends BaseTurretModel<BaseTurretRenderState> {

	protected final static String[] TEXTURES = new String[] {
		"ballista.png"
	};

	public BallistaTurretModel(ModelPart root) {
		super(
			root, "ballista", TEXTURES,

			root.getChild("base").getChild("head"),
			root.getChild("base").getChild("head").getChild("bow"),

			BallistaTurretAnimation.ANIM_BALLISTA_SHOOT.createAnimation(root),
			BallistaTurretAnimation.ANIM_BALLISTA_DEATH.createAnimation(root)
		);

	}

	@SuppressWarnings("unused")
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		// Base
		ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(56, 46).cuboid(-8.0F, -1.0F, -1.0F, 7.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(56, 49).cuboid(1.0F, -1.0F, -1.0F, 7.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));
		// Base 1
		base.addChild("base_r1", ModelPartBuilder.create().uv(36, 49).cuboid(0.0F, -1.0F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
		// Base 2
		base.addChild("base_r2", ModelPartBuilder.create().uv(36, 46).cuboid(0.0F, -1.0F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		// Pedestal
		base.addChild("pedestal", ModelPartBuilder.create().uv(46, 58).cuboid(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -1.0F, 0.0F));

		// Head
		ModelPartData head = base.addChild("head", ModelPartBuilder.create().uv(56, 52).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
			.uv(26, 58).cuboid(-3.0F, -5.0F, -2.0F, 1.0F, 5.0F, 4.0F, new Dilation(0.0F))
			.uv(36, 58).cuboid(2.0F, -5.0F, -2.0F, 1.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -8.0F, 0.0F));

		// Bow
		ModelPartData bow = head.addChild("bow", ModelPartBuilder.create().uv(38, 67).cuboid(-3.5F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(46, 67).cuboid(1.5F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(40, 18).cuboid(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 13.0F, new Dilation(0.0F))
			.uv(66, 57).cuboid(-1.0F, 0.0F, -8.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.0F))
			.uv(0, 40).cuboid(1.0F, -1.0F, -8.0F, 1.0F, 1.0F, 17.0F, new Dilation(0.0F))
			.uv(40, 0).cuboid(-2.0F, -1.0F, -8.0F, 1.0F, 1.0F, 17.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -3.0F, 0.0F));

		// Bow Blade
		ModelPartData bowBlade = bow.addChild("bowBlade", ModelPartBuilder.create().uv(56, 57).cuboid(1.0F, -4.0F, -1.0F, 3.0F, 9.0F, 2.0F, new Dilation(0.0F))
			.uv(0, 58).cuboid(-4.0F, -4.0F, -1.0F, 3.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -1.0F, -5.0F));
		// Inner Left Blade
		ModelPartData innerLeftBlade = bowBlade.addChild("innerLeftBlade", ModelPartBuilder.create().uv(72, 25).cuboid(0.0F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -0.5F, 0.5F, 0.0F, -0.3491F, 0.0F));
		// Outer Left Blade
		innerLeftBlade.addChild("outerLeftBlade", ModelPartBuilder.create().uv(0, 69).cuboid(0.0F, -0.5F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 0.0F, 0.0F, 0.0F, -0.6981F, 0.0F));
		// Inner Right Blade
		ModelPartData innerRightBlade = bowBlade.addChild("innerRightBlade", ModelPartBuilder.create().uv(72, 22).cuboid(-3.0F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -0.5F, 0.5F, 0.0F, 0.3491F, 0.0F));
		// Outer Right Blade
		innerRightBlade.addChild("outerRightBlade", ModelPartBuilder.create().uv(54, 68).cuboid(-4.0F, -0.5F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 0.0F, 0.0F, 0.0F, 0.6981F, 0.0F));

		// Pulley
		ModelPartData pulley = bow.addChild("pulley", ModelPartBuilder.create().uv(16, 72).cuboid(1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(72, 28).cuboid(-2.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 9.0F));

		// Winch
		ModelPartData winch = pulley.addChild("winch", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -1.0F, 1.0F));

		// Rope
		winch.addChild("rope", ModelPartBuilder.create().uv(72, 52).cuboid(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		// Lever
		winch.addChild("lever", ModelPartBuilder.create().uv(72, 32).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(22, 67).cuboid(0.5F, -2.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F))
			.uv(10, 63).cuboid(-5.5F, -0.5F, -0.5F, 7.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(3.0F, 0.0F, 0.0F));

		// Ram
		ModelPartData ram = bow.addChild("ram", ModelPartBuilder.create().uv(72, 40).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -1.0F, 7.0F));

		// Rear Draw String
		ModelPartData rearDrawString = ram.addChild("rearDrawString", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -0.5F, 0.5F));
		// Left Rear Draw String
		rearDrawString.addChild("leftRearDrawString", ModelPartBuilder.create().uv(36, 41).cuboid(0.0F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(1.0F, 0.0F, 0.0F));
		// Right Rear Draw String
		rearDrawString.addChild("rightRearDrawString", ModelPartBuilder.create().uv(36, 40).cuboid(-1.0F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(-1.0F, 0.0F, 0.0F));

		// Left String
		ram.addChild("leftString", ModelPartBuilder.create().uv(66, 62).cuboid(-0.25F, 0.0F, -0.5F, 11.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -0.5F, 0.5F, 0.0F, 0.925F, 0.0F));

		// Right String
		ram.addChild("rightString", ModelPartBuilder.create().uv(66, 63).cuboid(-10.75F, 0.0F, -0.5F, 11.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -0.5F, 0.5F, 0.0F, -0.925F, 0.0F));

		// Bolt
		ModelPartData bolt = bow.addChild("bolt", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, -0.25F, 1.0F, 1.0F, 19.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -0.5F, -11.25F));

		// Point
		ModelPartData point = bolt.addChild("point", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 2.075F));
		// Point 1
		point.addChild("point_r1", ModelPartBuilder.create().uv(46, 71).cuboid(0.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 0.0F, 0.4189F, 0.0F));
		// Point 2
		point.addChild("point_r2", ModelPartBuilder.create().uv(38, 71).cuboid(-1.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 0.0F, -0.4189F, 0.0F));
		// Point 3
		point.addChild("point_r3", ModelPartBuilder.create().uv(0, 71).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, -0.4189F, 0.0F, 0.0F));
		// Point 4
		point.addChild("point_r4", ModelPartBuilder.create().uv(54, 70).cuboid(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 0.4189F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -22.5f;
	}

	@Override
	protected float getMaxPitch() {
		return 22.5f;
	}
}
