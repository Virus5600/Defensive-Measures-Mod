package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class MicroMissileModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[]{
		"missile_turret.png"
	};

	public MicroMissileModel(ModelPart root) {
		super(root, "missile_turret", TEXTURES);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		PartDefinition projectile = modelPartData.addOrReplaceChild("projectile", CubeListBuilder.create()
			.texOffs(24, 14).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 19.0F, new CubeDeformation(0.0F))
			.texOffs(42, 32).addBox(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.125F)), PartPose.offset(0.0F, 1.0F, -8.0F));
		projectile.addOrReplaceChild("fins", CubeListBuilder.create()
			.texOffs(40, 32).addBox(0.0F, -1.0F, 7.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(44, 32).addBox(0.0F, 0F, 7.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(43, 31).addBox(-1.0F, 0.0F, 7.5F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
			.texOffs(39, 31).addBox(0.0F, 0.0F, 7.5F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 8.5F));

		return LayerDefinition.create(modelData, 64, 64);
	}
}
