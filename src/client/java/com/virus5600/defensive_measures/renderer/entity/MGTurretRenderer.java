package com.virus5600.defensive_measures.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.state.EntityRenderState;

import org.jspecify.annotations.NonNull;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import com.virus5600.defensive_measures.entity.turrets.MGTurretEntity;
import com.virus5600.defensive_measures.model.entity.MGTurretModel;
import com.virus5600.defensive_measures.renderer.BaseTurretRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

/**
 * Machine Gun (MG) Turret's Renderer.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
@Environment(EnvType.CLIENT)
public class MGTurretRenderer<
	R extends EntityRenderState & GeoRenderState
> extends BaseTurretRenderer<MGTurretEntity, R> {
	public MGTurretRenderer(Context ctx) {
		super(ctx, new MGTurretModel());
	}

	@Override
	public void preRenderPass(@NonNull RenderPassInfo<@NonNull R> renderPassInfo, @NonNull OrderedRenderCommandQueue renderTasks) {
		renderPassInfo.poseStack().scale(0.3375f, 0.3375f, 0.3375f);

		super.preRenderPass(renderPassInfo, renderTasks);
	}
}
