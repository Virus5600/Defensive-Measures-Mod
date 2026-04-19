package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class MGBulletModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[] {
		"mg_bullet.png"
	};

	public MGBulletModel(ModelPart root) {
		super(root, "mg_turret", TEXTURES);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F).scaled(0.2f));

		body.addChild("body_r1", ModelPartBuilder.create().uv(0, -11).cuboid(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
		body.addChild("body_r2", ModelPartBuilder.create().uv(0, -6).cuboid(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		return TexturedModelData.of(modelData, 32, 32);
	}
}
