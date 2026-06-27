package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class FlintPelletProjectileModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[] {
		"flint_pellet.png"
	};

	public FlintPelletProjectileModel(ModelPart root) {
		super(root, "pellet_turret", TEXTURES);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition pellet = partdefinition.addOrReplaceChild("pellet", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 0.5F, 0.0F));
		pellet.addOrReplaceChild("pellet_r1", CubeListBuilder.create().texOffs(0, 2).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, -0.7854F));
		pellet.addOrReplaceChild("pellet_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.7854F));
		pellet.addOrReplaceChild("pellet_r3", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.9553F, -0.5236F, 0.6155F));
		pellet.addOrReplaceChild("pellet_r4", CubeListBuilder.create().texOffs(0, 2).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.9553F, -0.5236F, -0.6155F));

		return LayerDefinition.create(meshdefinition, 8, 8);
	}
}
