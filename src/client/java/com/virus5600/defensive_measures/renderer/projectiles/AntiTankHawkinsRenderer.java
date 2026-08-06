package com.virus5600.defensive_measures.renderer.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

import com.virus5600.defensive_measures.entity.projectiles.AntiTankHawkinsEntity;
import com.virus5600.defensive_measures.model.ModEntityModels;
import com.virus5600.defensive_measures.model.projectiles.AntiTankMineHawkinsModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

import org.jspecify.annotations.NonNull;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class AntiTankHawkinsRenderer extends BaseProjectileRenderer<
	AntiTankHawkinsEntity,
	BaseProjectileRenderState,
	AntiTankMineHawkinsModel
	> {

	public AntiTankHawkinsRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new AntiTankMineHawkinsModel(ctx.bakeLayer(ModEntityModels.ANTI_TANK_MINE_HAWKINS)),
			0.125f,
			BaseProjectileRenderState::new
		);

		this.shouldLookAtDir(true);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	protected int getModelTint(final BaseProjectileRenderState state) {
		return 0x7CB342;
	}

	@Override
	public void submit(@NonNull BaseProjectileRenderState state, PoseStack stack, @NonNull SubmitNodeCollector queue, @NonNull CameraRenderState camState) {
		stack.pushPose();

		stack.scale(0.125f, 0.125f, 0.125f);
		stack.translate(0, -0.5, 0);

		super.submit(state, stack, queue, camState);

		stack.popPose();
	}
}
