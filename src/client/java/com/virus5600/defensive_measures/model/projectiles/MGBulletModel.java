package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class MGBulletModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[] {
		"mg_bullet.png"
	};

	public MGBulletModel(ModelPart root) {
		super(root, "mg_turret", TEXTURES);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		PartDefinition body = modelPartData.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F).scaled(0.2f));

		body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, -11).addBox(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
		body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(0, -6).addBox(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(modelData, 32, 32);
	}
}
