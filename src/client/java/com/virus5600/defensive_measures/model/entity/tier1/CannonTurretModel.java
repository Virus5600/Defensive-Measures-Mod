package com.virus5600.defensive_measures.model.entity.tier1;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.animations.entity.tier1.CannonTurretAnimation;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

/**
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @version 2.0
 */
public class CannonTurretModel extends BaseTurretModel<BaseTurretRenderState> {
	protected final static String[] TEXTURES = new String[] {
		"cannon_turret.png"
	};

	public CannonTurretModel(ModelPart root) {
		super(
			root, "cannon_turret", TEXTURES,

			root.getChild("stand"),
			root.getChild("stand").getChild("head"),

			CannonTurretAnimation.ANIM_CANNON_SHOOT,
			CannonTurretAnimation.ANIM_CANNON_DEATH,
			new AnimationDefinition[] {CannonTurretAnimation.ANIM_CANNON_SETUP},
			new AnimationDefinition[] {CannonTurretAnimation.ANIM_CANNON_TEARDOWN},
			1f
		);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partDefinition = meshdefinition.getRoot();
		partDefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition stand = partDefinition.addOrReplaceChild("stand", CubeListBuilder.create().texOffs(44, 45).addBox(-6.0F, -1.5F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.5F, 0.0F));
		stand.addOrReplaceChild("stand_r1", CubeListBuilder.create().texOffs(54, 32).addBox(-4.975F, 0.0F, -1.0F, 10.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6572F, -5.6744F, 0.8727F, 0.0F, 0.0F));

		PartDefinition right_stand = stand.addOrReplaceChild("right_stand", CubeListBuilder.create().texOffs(26, 67).addBox(0.0F, -9.0F, 0.0F, 1.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
			.texOffs(76, 32).addBox(0.0F, -4.0F, -3.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -1.5F, 1.0F));
		right_stand.addOrReplaceChild("right_stand_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-0.025F, -2.0F, -9.0F, 1.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.8727F, 0.0F, 0.0F));

		PartDefinition left_stand = stand.addOrReplaceChild("left_stand", CubeListBuilder.create().texOffs(34, 67).addBox(-1.0F, -9.0F, 0.0F, 1.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
			.texOffs(42, 76).addBox(-1.0F, -4.0F, -3.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -1.5F, 1.0F));
		left_stand.addOrReplaceChild("left_stand_r1", CubeListBuilder.create().texOffs(0, 67).addBox(-0.975F, -2.0F, -9.0F, 1.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.8727F, 0.0F, 0.0F));

		stand.addOrReplaceChild("head", CubeListBuilder.create().texOffs(54, 18).addBox(-5.0F, -2.5F, -5.5F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(0, 45).addBox(-4.0F, -3.5F, -10.5F, 8.0F, 8.0F, 14.0F, new CubeDeformation(0.0F))
			.texOffs(44, 58).addBox(-3.0F, -4.5F, -5.5F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(0, 18).addBox(-3.0F, -2.5F, -15.5F, 6.0F, 6.0F, 21.0F, new CubeDeformation(0.0F))
			.texOffs(54, 40).addBox(-2.0F, -1.5F, 5.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(64, 15).addBox(-1.0F, -1.5F, -17.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(64, 40).addBox(-1.0F, 1.5F, -17.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(76, 39).addBox(1.0F, -1.5F, -17.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(50, 76).addBox(-2.0F, -1.5F, -17.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(42, 67).addBox(0.0F, -3.0F, 6.5F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 1.5F));

		return LayerDefinition.create(meshdefinition, 128, 128);
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
