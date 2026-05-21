package com.virus5600.defensive_measures.model.entity;

import net.minecraft.client.model.*;

import com.virus5600.defensive_measures.animations.entity.FlameTurretAnimation;
import com.virus5600.defensive_measures.renderer.entity.state.BaseTurretRenderState;

public class FlameTurretModel extends BaseTurretModel<BaseTurretRenderState> {

	protected final static String[] TEXTURES = new String[]{
		"flame_turret.png"
	};

	public FlameTurretModel(ModelPart root) {
		super(
			root, "flame_turret", TEXTURES,

			root.getChild("base").getChild("neck"),
			root.getChild("base").getChild("neck").getChild("head").getChild("nozzle"),

			FlameTurretAnimation.ANIM_FLAME_SHOOT.createAnimation(root),
			FlameTurretAnimation.ANIM_FLAME_DEATH.createAnimation(root)
		);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 33).cuboid(-8.0F, -10.0F, -8.0F, 16.0F, 8.0F, 16.0F, new Dilation(0.0F))
			.uv(0, 0).cuboid(-16.0F, -0.075F, -16.0F, 32.0F, 1.0F, 32.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData stand = base.addChild("stand", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		stand.addChild("stand_r1", ModelPartBuilder.create().uv(56, 82).cuboid(-1.0F, -1.0F, -8.0F, 3.0F, 9.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.0472F));
		stand.addChild("stand_r2", ModelPartBuilder.create().uv(56, 57).cuboid(-2.0F, -1.0F, -8.0F, 3.0F, 9.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -2.0F, 0.0F, 0.0F, 0.0F, 1.0472F));
		stand.addChild("stand_r3", ModelPartBuilder.create().uv(90, 82).cuboid(-8.0F, -1.0F, -1.0F, 16.0F, 9.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 8.0F, 1.0472F, 0.0F, 0.0F));
		stand.addChild("stand_r4", ModelPartBuilder.create().uv(90, 61).cuboid(-8.0F, -1.0F, -2.0F, 16.0F, 9.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, -8.0F, -1.0472F, 0.0F, 0.0F));

		ModelPartData neck = base.addChild("neck", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -11.0F, 0.0F));
		neck.addChild("neck_r1", ModelPartBuilder.create().uv(0, 97).cuboid(0.0F, -3.0F, -7.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		neck.addChild("neck_r2", ModelPartBuilder.create().uv(98, 37).cuboid(-7.0F, -3.0F, -1.0F, 14.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 8.0F, 0.3491F, 0.0F, 0.0F));
		neck.addChild("neck_r3", ModelPartBuilder.create().uv(98, 33).cuboid(-7.0F, -3.0F, 0.0F, 14.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -8.0F, -0.3491F, 0.0F, 0.0F));
		neck.addChild("neck_r4", ModelPartBuilder.create().uv(94, 94).cuboid(-1.0F, -3.0F, -7.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		ModelPartData head = neck.addChild("head", ModelPartBuilder.create().uv(0, 57).cuboid(-7.0F, -15.0F, -7.0F, 14.0F, 14.0F, 14.0F, new Dilation(0.0F))
			.uv(0, 85).cuboid(-5.0F, -17.0F, -5.0F, 10.0F, 2.0F, 10.0F, new Dilation(0.0F))
			.uv(40, 92).cuboid(-2.0F, -11.0F, -9.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 1.0F, 0.0F));

		ModelPartData nozzle = head.addChild("nozzle", ModelPartBuilder.create().uv(64, 33).cuboid(-1.0F, -1.0F, -18.0F, 2.0F, 2.0F, 19.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -9.0F, -9.0F));

		ModelPartData lighter = nozzle.addChild("lighter", ModelPartBuilder.create().uv(92, 44).cuboid(-0.5F, 3.882F, -19.1391F, 1.0F, 1.0F, 16.0F, new Dilation(0.025F))
			.uv(30, 97).cuboid(-1.5F, -1.443F, -14.1391F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
			.uv(64, 54).cuboid(-0.5F, 3.882F, -14.1391F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F))
			.uv(52, 92).cuboid(-0.5F, 1.557F, -14.1391F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		lighter.addChild("lighter_r1", ModelPartBuilder.create().uv(40, 85).cuboid(-0.5F, -0.5F, -5.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData tip = lighter.addChild("tip", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 4.25F, -18.5F));
		tip.addChild("tip_r1", ModelPartBuilder.create().uv(52, 96).cuboid(-0.5F, -2.75F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected float getMinPitch() {
		return -30.0f;
	}

	@Override
	protected float getMaxPitch() {
		return 22.5f;
	}
}
