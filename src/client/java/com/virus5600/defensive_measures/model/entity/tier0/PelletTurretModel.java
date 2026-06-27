package com.virus5600.defensive_measures.model.entity.tier0;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.animations.entity.tier0.PelletTurretAnimation;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

import org.jspecify.annotations.NonNull;

public class PelletTurretModel extends BaseTurretModel<BaseTurretRenderState> {
	private final ModelPart SUPPORT_STICKS;

	protected final static String[] TEXTURES = new String[] {
		"pellet_turret.png"
	};

	public PelletTurretModel(ModelPart root) {
		super(
			root, "pellet_turret", TEXTURES,

			root.getChild("base").getChild("pole").getChild("holder"),
			root.getChild("base").getChild("pole").getChild("holder").getChild("barrel"),

			PelletTurretAnimation.ANIM_PELLET_TURRET_SHOOT,
			PelletTurretAnimation.ANIM_PELLET_TURRET_DEATH,
			new AnimationDefinition[] {PelletTurretAnimation.ANIM_PELLET_TURRET_SETUP},
			new AnimationDefinition[] {PelletTurretAnimation.ANIM_PELLET_TURRET_TEARDOWN},
			1f
		);

		this.SUPPORT_STICKS = root.getChild("base")
			.getChild("pole")
			.getChild("support");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition stand = base.addOrReplaceChild("stand", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		stand.addOrReplaceChild("stand_r1", CubeListBuilder.create().texOffs(12, 19).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 0.0F, -3.5F, -0.6155F, 0.5236F, -0.9553F));
		stand.addOrReplaceChild("stand_r2", CubeListBuilder.create().texOffs(8, 19).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 0.0F, 3.5F, 0.6155F, -0.5236F, -0.9553F));
		stand.addOrReplaceChild("stand_r3", CubeListBuilder.create().texOffs(16, 19).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, -3.5F, -0.6155F, -0.5236F, 0.9553F));
		stand.addOrReplaceChild("stand_r4", CubeListBuilder.create().texOffs(4, 19).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 3.5F, 0.6155F, 0.5236F, 0.9553F));

		PartDefinition pole = base.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(0, 19).addBox(-0.5F, -10.0F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition holder = pole.addOrReplaceChild("holder", CubeListBuilder.create().texOffs(20, 19).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition barrel = holder.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -6.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		barrel.addOrReplaceChild("leather_bag", CubeListBuilder.create().texOffs(20, 23).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.0F));

		PartDefinition support = pole.addOrReplaceChild("support", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 0.0F));

		support.addOrReplaceChild("support_r1", CubeListBuilder.create().texOffs(12, 13).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(-0.35F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 0.7854F, 0.0F, 0.0F));
		support.addOrReplaceChild("support_r2", CubeListBuilder.create().texOffs(0, 13).addBox(-0.5F, -0.5F, -4.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(-0.35F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.5F, -0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void setupAnim(@NonNull BaseTurretRenderState state) {
		super.setupAnim(state);

		this.SUPPORT_STICKS.yRot = this.neck.yRot;
		this.SUPPORT_STICKS.xRot = this.head.xRot;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -30;
	}

	@Override
	protected float getMaxPitch() {
		return 30;
	}
}
