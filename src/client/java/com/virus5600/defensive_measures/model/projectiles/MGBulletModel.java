package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

/**
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @version 1.0
 */
public class MGBulletModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[] {
		"mg_bullet.png"
	};

	public MGBulletModel(ModelPart root) {
		super(root, "mg_turret", TEXTURES);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partDefinition = meshdefinition.getRoot();

		PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F).scaled(0.2f));

		body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, -11).addBox(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
		body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(0, -6).addBox(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}
}
