package com.virus5600.defensive_measures.model.entity.tier1;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.animations.entity.tier1.MGTurretAnimation;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

/**
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @version 2.0
 */
public class MGTurretModel extends BaseTurretModel<BaseTurretRenderState> {
	protected final static String[] TEXTURES = new String[] {
		"mg_turret.png"
	};

	public MGTurretModel(ModelPart root) {
		super(
			root, "mg_turret", TEXTURES,

			root.getChild("turret").getChild("body"),
			root.getChild("turret").getChild("body").getChild("head"),

			MGTurretAnimation.ANIM_MG_SHOOT,
			MGTurretAnimation.ANIM_MG_DEATH,
			new AnimationDefinition[] {MGTurretAnimation.ANIM_MG_SETUP},
			new AnimationDefinition[] {MGTurretAnimation.ANIM_MG_TEARDOWN},
			0.625f
		);
	}

	@SuppressWarnings("unused")
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partDefinition = meshdefinition.getRoot();

		// Turret
		PartDefinition turret = partDefinition.addOrReplaceChild("turret", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		// Base
		turret.addOrReplaceChild("base", CubeListBuilder.create().texOffs(22, 36).addBox(6.0F, -1.0F, -3.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(38, 36).addBox(-8.0F, -1.0F, -3.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(22, 43).addBox(-3.0F, -1.0F, -8.0F, 6.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(38, 43).addBox(-3.0F, -1.0F, 6.0F, 6.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(0, 0).addBox(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
			.texOffs(0, 13).addBox(-4.5F, -1.5F, -4.5F, 9.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
			.texOffs(0, 24).addBox(4.5F, -2.0F, -5.5F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
			.texOffs(24, 24).addBox(-5.5F, -2.0F, -5.5F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
			.texOffs(36, 20).addBox(-4.5F, -2.0F, -5.5F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(36, 22).addBox(-4.5F, -2.0F, 4.5F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		// Body
		PartDefinition body = turret.addOrReplaceChild("body", CubeListBuilder.create().texOffs(36, 13).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		// Right Stand
		PartDefinition right_stand = body.addOrReplaceChild("right_stand", CubeListBuilder.create(), PartPose.offset(-2.5F, -0.42F, -0.25F));
		// Right Stand 1
		right_stand.addOrReplaceChild("right_stand_r1", CubeListBuilder.create().texOffs(12, 51).addBox(-0.5F, 0.0F, 1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(16, 47).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(0, 47).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(48, 0).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.33F, 2.5F, -0.5236F, 0.0F, 0.0F));

		// Left Stand
		PartDefinition left_stand = body.addOrReplaceChild("left_stand", CubeListBuilder.create(), PartPose.offset(2.5F, -0.42F, -0.25F));
		// Left Stand 1
		left_stand.addOrReplaceChild("left_stand_r1", CubeListBuilder.create().texOffs(52, 0).addBox(-0.5F, 0.0F, 1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(8, 51).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(4, 47).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(48, 24).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.33F, 2.5F, -0.5236F, 0.0F, 0.0F));

		// Head
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -5.5F, 2.25F));

		// Gun
		PartDefinition gun = head.addOrReplaceChild("gun", CubeListBuilder.create().texOffs(0, 36).addBox(-2.0F, -1.75F, -3.25F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		// Gun Barrel
		PartDefinition inner_barrel = gun.addOrReplaceChild("inner_barrel", CubeListBuilder.create().texOffs(8, 47).addBox(-1.5F, -1.495F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.005F, -3.25F));
		PartDefinition mid_barrel = inner_barrel.addOrReplaceChild("mid_barrel", CubeListBuilder.create().texOffs(34, 46).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.245F, -1.0F));
		mid_barrel.addOrReplaceChild("outer_barrel", CubeListBuilder.create().texOffs(48, 8).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.25F, -4.0F));

		// Discarded Ammo
		gun.addOrReplaceChild("discarded_ammo", CubeListBuilder.create().texOffs(48, 32).addBox(-0.5F, -0.5F, -0.675F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.33F)), PartPose.offset(-1.5F, -1.0F, -2.575F));

		// Ejection Port
		gun.addOrReplaceChild("ejection_port", CubeListBuilder.create().texOffs(27, 41).addBox(-0.5375F, -0.725F, -0.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(27, 41).addBox(-0.5125F, -0.725F, -1.3F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(27, 41).addBox(-0.5125F, -0.275F, -0.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(27, 41).addBox(-0.5125F, -0.275F, -1.3F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5375F, -0.6F, -0.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.6F, -1.3F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.5F, -0.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.4875F, -0.5F, -1.3F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5375F, -0.4F, -0.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.4F, -1.3F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F)), PartPose.offset(-1.9625F, -1.0F, -2.1984F));

		// Ammo Case
		gun.addOrReplaceChild("ammo_case", CubeListBuilder.create().texOffs(46, 46).addBox(1.6502F, -0.5006F, -1.9165F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(22, 46).addBox(-0.3498F, -1.5006F, -1.9165F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.3498F, 0.0006F, -0.3335F));

		// Ammo Intake
		gun.addOrReplaceChild("ammo_intake", CubeListBuilder.create().texOffs(26, 40).addBox(-0.5125F, -0.725F, -3.075F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.44F))
			.texOffs(26, 40).addBox(-0.5125F, -0.275F, -3.075F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.44F))
			.texOffs(26, 40).addBox(-0.5125F, -0.725F, -0.95F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.44F))
			.texOffs(26, 40).addBox(-0.5125F, -0.275F, -0.95F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.6F, -3.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.4875F, -0.5F, -3.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.4F, -3.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.6F, -2.075F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.4875F, -0.5F, -2.075F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.4F, -2.075F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.6F, 0.175F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.4875F, -0.5F, -0.95F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.4F, -0.95F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.6F, -0.95F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.4875F, -0.5F, 0.175F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F))
			.texOffs(31, 47).addBox(-0.5125F, -0.4F, 0.175F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.44F)), PartPose.offsetAndRotation(1.2375F, -1.75F, 0.5516F, 0.0F, 0.0F, 1.5708F));

		// Rounds
		PartDefinition rounds = gun.addOrReplaceChild("rounds", CubeListBuilder.create(), PartPose.offset(1.225F, -1.75F, -0.5F));
		// Ammo 1
		PartDefinition ammo1 = rounds.addOrReplaceChild("ammo1", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		// Ammo 2
		PartDefinition ammo2 = ammo1.addOrReplaceChild("ammo2", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(0.0F, -0.4F, 0.0F, 0.0F, 0.0F, 0.4189F));
		// Ammo 3
		PartDefinition ammo3 = ammo2.addOrReplaceChild("ammo3", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(-0.0065F, -0.3952F, 0.0F, 0.0F, 0.0F, 0.4189F));
		// Ammo 4
		PartDefinition ammo4 = ammo3.addOrReplaceChild("ammo4", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(-0.0065F, -0.3952F, 0.0F, 0.0F, 0.0F, 0.384F));
		// Ammo 5
		PartDefinition ammo5 = ammo4.addOrReplaceChild("ammo5", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(-0.003F, -0.3953F, 0.0F, 0.0F, 0.0F, 0.384F));
		// Ammo 6
		PartDefinition ammo6 = ammo5.addOrReplaceChild("ammo6", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(0.0072F, -0.388F, 0.0F, 0.0F, 0.0F, 0.3491F));
		// Ammo 7
		PartDefinition ammo7 = ammo6.addOrReplaceChild("ammo7", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(0.0072F, -0.388F, 0.0F, 0.0F, 0.0F, 0.3491F));
		// Ammo 8
		PartDefinition ammo8 = ammo7.addOrReplaceChild("ammo8", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(0.0072F, -0.388F, 0.0F, 0.0F, 0.0F, 0.3142F));
		// Ammo 9
		PartDefinition ammo9 = ammo8.addOrReplaceChild("ammo9", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(0.0108F, -0.3951F, 0.0F, 0.0F, 0.0F, 0.3142F));
		// Ammo 10
		ammo9.addOrReplaceChild("ammo10", CubeListBuilder.create().texOffs(51, 24).addBox(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.33F)), PartPose.offsetAndRotation(-0.0063F, -0.388F, 0.0F, 0.0F, 0.0F, 0.2443F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -27.5f;
	}

	@Override
	protected float getMaxPitch() {
		return 90f;
	}
}
