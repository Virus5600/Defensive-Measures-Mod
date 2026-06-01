package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class MicroMissileModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[]{
		"missile_turret.png"
	};

	public MicroMissileModel(ModelPart root) {
		super(root, "missile_turret", TEXTURES);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData projectile = modelPartData.addChild("projectile", ModelPartBuilder.create()
			.uv(24, 14).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 19.0F, new Dilation(0.0F))
			.uv(42, 32).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.125F)), ModelTransform.origin(0.0F, 1.0F, -8.0F));
		projectile.addChild("fins", ModelPartBuilder.create()
			.uv(40, 32).cuboid(0.0F, -1.0F, 7.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(44, 32).cuboid(0.0F, 0F, 7.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(43, 31).cuboid(-1.0F, 0.0F, 7.5F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
			.uv(39, 31).cuboid(0.0F, 0.0F, 7.5F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 8.5F));

		return TexturedModelData.of(modelData, 64, 64);
	}
}
