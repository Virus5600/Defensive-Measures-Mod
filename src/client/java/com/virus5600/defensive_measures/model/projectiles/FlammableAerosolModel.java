package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

public class FlammableAerosolModel extends BaseProjectileModel<BaseProjectileRenderState> {

	public FlammableAerosolModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();

		return LayerDefinition.create(meshdefinition, 8, 8);
	}
}
