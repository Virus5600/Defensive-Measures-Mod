package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class FlammableAerosolModel extends BaseProjectileModel<BaseProjectileRenderState> {

	public FlammableAerosolModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();

		return TexturedModelData.of(modelData, 8, 8);
	}
}
