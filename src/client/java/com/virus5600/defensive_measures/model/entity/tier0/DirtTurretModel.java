package com.virus5600.defensive_measures.model.entity.tier0;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.virus5600.defensive_measures.animations.entity.tier0.DirtTurretAnimation;
import com.virus5600.defensive_measures.model.entity.BaseTurretModel;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @version 1.0
 */
public class DirtTurretModel extends BaseTurretModel<BaseTurretRenderState> {
	public final ModelPart MINIMAP_ICON;

	protected final static String[] TEXTURES = new String[] {
		"dirt_turret.png"
	};

	public DirtTurretModel(ModelPart root) {
		super(
			root, "dirt_turret", TEXTURES,

			root.getChild("body"),
			root.getChild("body").getChild("dirt"),

			DirtTurretAnimation.ANIM_DIRT_TURRET_SHOOT,
			DirtTurretAnimation.ANIM_DIRT_TURRET_DEATH,
			new AnimationDefinition[] {DirtTurretAnimation.ANIM_DIRT_TURRET_SETUP},
			new AnimationDefinition[] {DirtTurretAnimation.ANIM_DIRT_TURRET_TEARDOWN},
			1f
		);

		this.MINIMAP_ICON = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(12, 23).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition dirt = body.addOrReplaceChild("dirt", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		dirt.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(9, 16).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -4.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -7;
	}

	@Override
	protected float getMaxPitch() {
		return 7;
	}
}
