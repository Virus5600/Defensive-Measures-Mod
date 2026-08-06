package com.virus5600.defensive_measures.model.projectiles;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @version 1.0
 */
public class AntiTankMineHawkinsModel extends BaseProjectileModel<BaseProjectileRenderState> {
	protected final static String[] TEXTURES = new String[]{
		"anti_tank_mine_hawkins.png"
	};

	public AntiTankMineHawkinsModel(ModelPart root) {
		super(root, "projectiles", TEXTURES);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition grenade = partdefinition.addOrReplaceChild("grenade", CubeListBuilder.create().texOffs(0, 0)
			.addBox(-12.0F, -8.0F, -19.0F, 24.0F, 16.0F, 40.0F, new CubeDeformation(0.0F)).texOffs(0, 86)
			.addBox(-5.0F, -5.0F, -23.0F, 10.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 1.0F));

		grenade.addOrReplaceChild("trigger", CubeListBuilder.create().texOffs(28, 86)
			.addBox(-6.0F, -16.799F, -16.025F, 12.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(28, 93)
			.addBox(-6.0F, -16.799F, 9.975F, 12.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 56)
			.addBox(-6.0F, -16.899F, -16.025F, 12.0F, 0.0F, 30.0F, new CubeDeformation(0.0F)).texOffs(80, 56)
			.addBox(-4.0F, -15.799F, -4.525F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(84, 74)
			.addBox(-2.0F, -16.799F, -8.025F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.799F, 1.025F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}
