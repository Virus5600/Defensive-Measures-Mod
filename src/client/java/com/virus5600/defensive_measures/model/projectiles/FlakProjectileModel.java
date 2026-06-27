package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class FlakProjectileModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[]{
		"flak_projectile.png"
	};

	public FlakProjectileModel(ModelPart root) {
		super(root, "aa_turret", TEXTURES);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partDefinition = meshdefinition.getRoot();

		partDefinition.addOrReplaceChild("projectile", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
			.texOffs(0, 4).addBox(-0.5F, -0.5F, -1.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.125F)), PartPose.offset(0.0F, 0.5F, -0.5F));

		return LayerDefinition.create(meshdefinition, 8, 8);
	}
}
