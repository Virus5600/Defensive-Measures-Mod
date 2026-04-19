package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class BallistaBoltModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[]{
		"ballista.png"
	};

	public BallistaBoltModel(ModelPart root) {
		super(root, "ballista", TEXTURES);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData bolt = modelPartData.addChild("bolt", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 19.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.5F, 0.0F));

		ModelPartData point = bolt.addChild("point", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		point.addChild("point_r1", ModelPartBuilder.create().uv(46, 71).cuboid(0.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.675F, 0.0F, 0.4189F, 0.0F));
		point.addChild("point_r2", ModelPartBuilder.create().uv(38, 71).cuboid(-1.0F, -0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.675F, 0.0F, -0.4189F, 0.0F));
		point.addChild("point_r3", ModelPartBuilder.create().uv(0, 71).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.675F, -0.4189F, 0.0F, 0.0F));
		point.addChild("point_r4", ModelPartBuilder.create().uv(54, 70).cuboid(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.675F, 0.4189F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}
}
