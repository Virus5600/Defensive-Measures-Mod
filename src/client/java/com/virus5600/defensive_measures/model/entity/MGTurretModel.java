package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.animations.entity.MGTurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class MGTurretModel extends BaseTurretModel<BaseTurretRenderState> {
	protected final static String[] TEXTURES = new String[] {
		"mg_turret.png"
	};

	public MGTurretModel(ModelPart root) {
		super(
			root, "mg_turret", TEXTURES,

			root.getChild("turret").getChild("body"),
			root.getChild("turret").getChild("body").getChild("head"),

			MGTurretAnimation.ANIM_MG_SHOOT.createAnimation(root),
			MGTurretAnimation.ANIM_MG_DEATH.createAnimation(root)
		);
	}

	@SuppressWarnings("unused")
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		// Turret
		ModelPartData turret = modelPartData.addChild("turret", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		// Base
		turret.addChild("base", ModelPartBuilder.create().uv(22, 36).cuboid(6.0F, -1.0F, -3.0F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F))
			.uv(38, 36).cuboid(-8.0F, -1.0F, -3.0F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F))
			.uv(22, 43).cuboid(-3.0F, -1.0F, -8.0F, 6.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(38, 43).cuboid(-3.0F, -1.0F, 6.0F, 6.0F, 1.0F, 2.0F, new Dilation(0.0F))
			.uv(0, 0).cuboid(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F))
			.uv(0, 13).cuboid(-4.5F, -1.5F, -4.5F, 9.0F, 2.0F, 9.0F, new Dilation(0.0F))
			.uv(0, 24).cuboid(4.5F, -2.0F, -5.5F, 1.0F, 1.0F, 11.0F, new Dilation(0.0F))
			.uv(24, 24).cuboid(-5.5F, -2.0F, -5.5F, 1.0F, 1.0F, 11.0F, new Dilation(0.0F))
			.uv(36, 20).cuboid(-4.5F, -2.0F, -5.5F, 9.0F, 1.0F, 1.0F, new Dilation(0.0F))
			.uv(36, 22).cuboid(-4.5F, -2.0F, 4.5F, 9.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		// Body
		ModelPartData body = turret.addChild("body", ModelPartBuilder.create().uv(36, 13).cuboid(-3.0F, -0.5F, -3.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.15F)), ModelTransform.origin(0.0F, -1.0F, 0.0F));

		// Right Stand
		ModelPartData right_stand = body.addChild("right_stand", ModelPartBuilder.create(), ModelTransform.origin(-2.5F, -1.3F, 0.25F));
		// Right Stand 1
		right_stand.addChild("right_stand_r1", ModelPartBuilder.create().uv(12, 51).cuboid(-0.5F, 0.0F, 1.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
			.uv(16, 47).cuboid(-0.5F, -2.0F, 0.0F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F))
			.uv(0, 47).cuboid(-0.5F, -2.0F, -1.0F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F))
			.uv(48, 0).cuboid(-0.5F, -1.0F, -2.0F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.45F, 2.0F, -0.5236F, 0.0F, 0.0F));

		// Left Stand
		ModelPartData left_stand = body.addChild("left_stand", ModelPartBuilder.create(), ModelTransform.origin(2.5F, -1.3F, 0.25F));
		// Left Stand 1
		left_stand.addChild("left_stand_r1", ModelPartBuilder.create().uv(52, 0).cuboid(-0.5F, 0.0F, 1.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
			.uv(8, 51).cuboid(-0.5F, -2.0F, 0.0F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F))
			.uv(4, 47).cuboid(-0.5F, -2.0F, -1.0F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F))
			.uv(48, 24).cuboid(-0.5F, -1.0F, -2.0F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.45F, 2.0F, -0.5236F, 0.0F, 0.0F));

		// Head
		ModelPartData head = body.addChild("head", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -5.5F, 2.25F));

		// Gun
		ModelPartData gun = head.addChild("gun", ModelPartBuilder.create().uv(0, 36).cuboid(-1.8F, -1.45F, 0.9484F, 4.0F, 4.0F, 7.0F, new Dilation(0.0F))
			.uv(8, 47).cuboid(-1.3F, -1.2F, -0.0516F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
			.uv(34, 46).cuboid(-0.8F, -0.95F, -4.0516F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F))
			.uv(48, 8).cuboid(-0.3F, -0.7F, -6.0516F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(-0.2F, -0.3F, -4.1984F));

		// Discarded Ammo
		gun.addChild("discarded_ammo", ModelPartBuilder.create().uv(48, 32).cuboid(-0.5F, -0.5F, -0.675F, 1.0F, 1.0F, 2.0F, new Dilation(-0.33F)), ModelTransform.origin(-1.3F, -0.7F, 1.6234F));

		// Ejection Port
		gun.addChild("ejection_port", ModelPartBuilder.create().uv(27, 41).cuboid(-0.5375F, -0.725F, -0.7F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(27, 41).cuboid(-0.5125F, -0.725F, -1.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(27, 41).cuboid(-0.5125F, -0.275F, -0.7F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(27, 41).cuboid(-0.5125F, -0.275F, -1.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(31, 47).cuboid(-0.5375F, -0.6F, -0.7F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(31, 47).cuboid(-0.5125F, -0.6F, -1.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(31, 47).cuboid(-0.5125F, -0.5F, -0.7F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(31, 47).cuboid(-0.4875F, -0.5F, -1.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(31, 47).cuboid(-0.5375F, -0.4F, -0.7F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F))
			.uv(31, 47).cuboid(-0.5125F, -0.4F, -1.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.44F)), ModelTransform.origin(-1.7625F, -0.7F, 2.0F));

		// Ammo Case
		gun.addChild("ammo_case", ModelPartBuilder.create().uv(46, 46).cuboid(1.6502F, -0.5006F, -1.9165F, 1.0F, 2.0F, 4.0F, new Dilation(0.0F))
			.uv(22, 46).cuboid(-0.3498F, -1.5006F, -1.9165F, 2.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(3.5498F, 0.3006F, 3.8649F));

		// Rounds
		ModelPartData rounds = gun.addChild("rounds", ModelPartBuilder.create(), ModelTransform.origin(1.425F, -1.45F, 3.6984F));
		// Ammo 1
		ModelPartData ammo1 = rounds.addChild("ammo1", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		// Ammo 2
		ModelPartData ammo2 = ammo1.addChild("ammo2", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(0.0F, -0.4F, 0.0F, 0.0F, 0.0F, 0.4189F));
		// Ammo 3
		ModelPartData ammo3 = ammo2.addChild("ammo3", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(-0.0065F, -0.3952F, 0.0F, 0.0F, 0.0F, 0.4189F));
		// Ammo 4
		ModelPartData ammo4 = ammo3.addChild("ammo4", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(-0.0065F, -0.3952F, 0.0F, 0.0F, 0.0F, 0.384F));
		// Ammo 5
		ModelPartData ammo5 = ammo4.addChild("ammo5", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(-0.003F, -0.3953F, 0.0F, 0.0F, 0.0F, 0.384F));
		// Ammo 6
		ModelPartData ammo6 = ammo5.addChild("ammo6", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(0.0072F, -0.388F, 0.0F, 0.0F, 0.0F, 0.3491F));
		// Ammo 7
		ModelPartData ammo7 = ammo6.addChild("ammo7", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(0.0072F, -0.388F, 0.0F, 0.0F, 0.0F, 0.3491F));
		// Ammo 8
		ModelPartData ammo8 = ammo7.addChild("ammo8", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(0.0072F, -0.388F, 0.0F, 0.0F, 0.0F, 0.3142F));
		// Ammo 9
		ModelPartData ammo9 = ammo8.addChild("ammo9", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(0.0108F, -0.3951F, 0.0F, 0.0F, 0.0F, 0.3142F));
		// Ammo 10
		ammo9.addChild("ammo10", ModelPartBuilder.create().uv(51, 24).cuboid(-0.5F, -0.675F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.33F)), ModelTransform.of(-0.0063F, -0.388F, 0.0F, 0.0F, 0.0F, 0.2443F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -27.5f;
	}

	@Override
	protected float getMaxPitch() {
		return 90f;
	}
}
