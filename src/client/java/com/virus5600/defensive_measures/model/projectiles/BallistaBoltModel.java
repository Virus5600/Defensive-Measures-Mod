package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class BallistaBoltModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[]{
		"ballista.png"
	};

	public BallistaBoltModel(ModelPart root) {
		super(root, "ballista", TEXTURES);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partDefinition = meshdefinition.getRoot();

		PartDefinition bolt = partDefinition.addOrReplaceChild("bolt", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.0F));

		PartDefinition point = bolt.addOrReplaceChild("point", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		point.addOrReplaceChild("point_r1", CubeListBuilder.create().texOffs(46, 71).addBox(0.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.675F, 0.0F, 0.4189F, 0.0F));
		point.addOrReplaceChild("point_r2", CubeListBuilder.create().texOffs(38, 71).addBox(-1.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.675F, 0.0F, -0.4189F, 0.0F));
		point.addOrReplaceChild("point_r3", CubeListBuilder.create().texOffs(0, 71).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.675F, -0.4189F, 0.0F, 0.0F));
		point.addOrReplaceChild("point_r4", CubeListBuilder.create().texOffs(54, 70).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.675F, 0.4189F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}
