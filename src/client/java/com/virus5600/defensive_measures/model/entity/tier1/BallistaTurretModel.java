package com.virus5600.defensive_measures.model.entity.tier1;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.animations.entity.tier1.BallistaTurretAnimation;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
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

			BallistaTurretAnimation.ANIM_BALLISTA_SHOOT,
			BallistaTurretAnimation.ANIM_BALLISTA_DEATH,
			new AnimationDefinition[] {BallistaTurretAnimation.ANIM_BALLISTA_SETUP},
			new AnimationDefinition[] {BallistaTurretAnimation.ANIM_BALLISTA_TEARDOWN},
			1f
		);

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partDefinition = meshdefinition.getRoot();

		// Base
		PartDefinition base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(56, 46).addBox(-8.0F, -1.0F, -1.0F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(56, 49).addBox(1.0F, -1.0F, -1.0F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		// Base 1
		base.addOrReplaceChild("base_r1", CubeListBuilder.create().texOffs(36, 49).addBox(0.0F, -1.0F, -1.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
		// Base 2
		base.addOrReplaceChild("base_r2", CubeListBuilder.create().texOffs(36, 46).addBox(0.0F, -1.0F, -1.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		// Pedestal
		base.addOrReplaceChild("pedestal", CubeListBuilder.create().texOffs(46, 58).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		// Head
		PartDefinition head = base.addOrReplaceChild("head", CubeListBuilder.create().texOffs(56, 52).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(26, 58).addBox(-3.0F, -5.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(36, 58).addBox(2.0F, -5.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		// Bow
		PartDefinition bow = head.addOrReplaceChild("bow", CubeListBuilder.create().texOffs(38, 67).addBox(-3.5F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(46, 67).addBox(1.5F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(40, 18).addBox(-1.5F, 0.5F, -4.0F, 3.0F, 1.0F, 13.0F, new CubeDeformation(0.0F))
			.texOffs(66, 57).addBox(-1.0F, 0.5F, -8.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(0, 40).addBox(1.0F, -0.5F, -8.0F, 1.0F, 1.0F, 17.0F, new CubeDeformation(0.0F))
			.texOffs(40, 0).addBox(-2.0F, -0.5F, -8.0F, 1.0F, 1.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 0.0F));

		// Bow Blade
		PartDefinition bowBlade = bow.addOrReplaceChild("bowBlade", CubeListBuilder.create().texOffs(56, 57).addBox(1.0F, -4.0F, -1.0F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(0, 58).addBox(-4.0F, -4.0F, -1.0F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -5.0F));
		// Inner Left Blade
		PartDefinition innerLeftBlade = bowBlade.addOrReplaceChild("innerLeftBlade", CubeListBuilder.create().texOffs(72, 25).addBox(0.0F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -0.5F, 0.5F, 0.0F, -0.3491F, 0.0F));
		// Outer Left Blade
		innerLeftBlade.addOrReplaceChild("outerLeftBlade", CubeListBuilder.create().texOffs(0, 69).addBox(0.0F, -0.5F, -1.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, -0.6981F, 0.0F));
		// Inner Right Blade
		PartDefinition innerRightBlade = bowBlade.addOrReplaceChild("innerRightBlade", CubeListBuilder.create().texOffs(72, 22).addBox(-3.0F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -0.5F, 0.5F, 0.0F, 0.3491F, 0.0F));
		// Outer Right Blade
		innerRightBlade.addOrReplaceChild("outerRightBlade", CubeListBuilder.create().texOffs(54, 68).addBox(-4.0F, -0.5F, -1.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.6981F, 0.0F));

		// Pulley
		PartDefinition pulley = bow.addOrReplaceChild("pulley", CubeListBuilder.create().texOffs(16, 72).addBox(1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(72, 28).addBox(-2.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 9.0F));

		// Winch
		PartDefinition winch = pulley.addOrReplaceChild("winch", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 1.0F));

		// Rope
		winch.addOrReplaceChild("rope", CubeListBuilder.create().texOffs(72, 52).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		// Lever
		winch.addOrReplaceChild("lever", CubeListBuilder.create().texOffs(72, 32).addBox(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(22, 67).addBox(0.5F, -2.0F, -2.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(10, 63).addBox(-5.5F, -0.5F, -0.5F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

		// Ram
		PartDefinition ram = bow.addOrReplaceChild("ram", CubeListBuilder.create().texOffs(72, 40).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 7.0F));

		// Rear Draw String
		PartDefinition rearDrawString = ram.addOrReplaceChild("rearDrawString", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.5F));
		// Left Rear Draw String
		rearDrawString.addOrReplaceChild("leftRearDrawString", CubeListBuilder.create().texOffs(36, 41).addBox(0.0F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 0.0F));
		// Right Rear Draw String
		rearDrawString.addOrReplaceChild("rightRearDrawString", CubeListBuilder.create().texOffs(36, 40).addBox(-1.0F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 0.0F));

		// Left String
		ram.addOrReplaceChild("leftString", CubeListBuilder.create().texOffs(66, 62).addBox(-0.25F, 0.0F, -0.5F, 11.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -0.5F, 0.5F, 0.0F, 0.925F, 0.0F));

		// Right String
		ram.addOrReplaceChild("rightString", CubeListBuilder.create().texOffs(66, 63).addBox(-10.75F, 0.0F, -0.5F, 11.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -0.5F, 0.5F, 0.0F, -0.925F, 0.0F));

		// Bolt
		PartDefinition bolt = bow.addOrReplaceChild("bolt", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -0.25F, 1.0F, 1.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -11.25F));

		// Point
		PartDefinition point = bolt.addOrReplaceChild("point", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.075F));
		// Point 1
		point.addOrReplaceChild("point_r1", CubeListBuilder.create().texOffs(46, 71).addBox(0.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.4189F, 0.0F));
		// Point 2
		point.addOrReplaceChild("point_r2", CubeListBuilder.create().texOffs(38, 71).addBox(-1.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, -0.4189F, 0.0F));
		// Point 3
		point.addOrReplaceChild("point_r3", CubeListBuilder.create().texOffs(0, 71).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.4189F, 0.0F, 0.0F));
		// Point 4
		point.addOrReplaceChild("point_r4", CubeListBuilder.create().texOffs(54, 70).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.4189F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -20f;
	}

	@Override
	protected float getMaxPitch() {
		return 20f;
	}
}
