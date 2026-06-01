package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class FlakProjectileModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[]{
		"flak_projectile.png"
	};

	public FlakProjectileModel(ModelPart root) {
		super(root, "aa_turret", TEXTURES);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("projectile", ModelPartBuilder.create()
			.uv(0, 0).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
			.uv(0, 4).cuboid(-0.5F, -0.5F, -1.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.125F)), ModelTransform.origin(0.0F, 0.5F, -0.5F));

		return TexturedModelData.of(modelData, 8, 8);
	}
}
