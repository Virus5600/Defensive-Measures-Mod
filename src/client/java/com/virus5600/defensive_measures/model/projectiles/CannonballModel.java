package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class CannonballModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[] {
		"cannonball.png"
	};

	public CannonballModel(ModelPart root) {
		super(root, "cannon_turret", TEXTURES);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
			.uv(0, 0).cuboid(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F))
			.uv(0, 2).cuboid(-3.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new Dilation(0.0F))
			.uv(0, 0).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 4.0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}
}
