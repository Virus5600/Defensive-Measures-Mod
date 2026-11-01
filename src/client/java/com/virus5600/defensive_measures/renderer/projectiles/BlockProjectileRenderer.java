package com.virus5600.defensive_measures.renderer.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import com.virus5600.defensive_measures.entity.projectiles.BlockProjectileEntity;
import com.virus5600.defensive_measures.model.projectiles.BaseProjectileModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;
import com.virus5600.defensive_measures.renderer.projectiles.state.BlockProjectileRenderState;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * A more specialized renderer for the {@link BlockProjectileEntity} that handles the rendering of
 * the block that is being shot by the turret. This class extends the {@link BaseTurretProjectileRenderer}
 * class and provides additional functionality for rendering the block projectile, including
 * extracting the block state from the entity and submitting it to the render queue.
 * <br><br>
 * This allows the renderer to render the block projectile with the correct block state, ensuring
 * that the block appears as it should when it is shot from the turret. The renderer also utilizes
 * the new {@link BaseProjectileRenderer#submitExtra(BaseProjectileRenderState, PoseStack, SubmitNodeCollector, CameraRenderState) submitExtra()}
 * method to submit the block state to the render queue, allowing changes to the model without
 * needing to override the entire {@link BaseProjectileRenderer#submit(BaseProjectileRenderState, PoseStack, SubmitNodeCollector, CameraRenderState) submit()}
 * method.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BlockProjectileRenderer extends BaseTurretProjectileRenderer<
	BlockProjectileEntity,
	BlockProjectileRenderState,
	BaseProjectileModel<BlockProjectileRenderState>
	> {

	private final ItemModelResolver itemModelResolver;

	public BlockProjectileRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			null,
			0.5f,
			BlockProjectileRenderState::new
		);

		this.itemModelResolver = ctx.getItemModelResolver();
		this.shouldLookAtDir(true);
		this.shadowRadius = 0.5f;
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	public void extractRenderState(@NonNull BlockProjectileEntity entity, @NonNull BlockProjectileRenderState state, float tickProgress) {
		super.extractRenderState(entity, state, tickProgress);

		MovingBlockRenderState mbs = state.movingBlockRenderState;
		BlockPos pos = entity.blockPosition();

		mbs.randomSeedPos = new BlockPos(entity.getId(), 0, 0);
		mbs.blockPos = pos;
		mbs.blockState = entity.getBlockState();

		if (entity.level() instanceof ClientLevel level) {
			mbs.biome = level.getBiome(pos);
			mbs.cardinalLighting = level.cardinalLighting();
			mbs.lightEngine = level.getLightEngine();
		}

		boolean isInvisible = mbs.blockState.getRenderShape() != RenderShape.MODEL;
		boolean isBlockEntity = mbs.blockState.hasBlockEntity();

		if (isInvisible || isBlockEntity) {
			ItemStack itemStack = new ItemStack(mbs.blockState.getBlock());

			this.itemModelResolver.updateForNonLiving(
				state.itemRenderState,
				itemStack,
				ItemDisplayContext.NONE,
				entity
			);
		}
	}

	// ////////////// //
	// CUSTOM METHODS //
	// ////////////// //

	@Override
	protected void submitExtra(@NonNull BlockProjectileRenderState state, PoseStack stack, @NonNull SubmitNodeCollector queue, @NonNull CameraRenderState camState) {
		float radius = 0.5f;
		float height = 0.5f;
		double yTranslation = -0.5;

		BlockState blockState = state.movingBlockRenderState.blockState;
		boolean isInvisible = blockState.getRenderShape() != RenderShape.MODEL;
		boolean isBlockEntity = blockState.hasBlockEntity();

		// If it's a block entity...
		if (isInvisible || isBlockEntity) {
			stack.pushPose();

			stack.translate(0.0, 0.25, 0.0);
			stack.scale(radius, height, radius);

			state.itemRenderState.submit(
				stack,
				queue,
				state.lightCoords,
				OverlayTexture.NO_OVERLAY,
				state.outlineColor
			);

			stack.popPose();
			return;
		}

		stack.pushPose();

		// Make the render larger if its an Iron Bars block - easter egg to make it look like a
		// Rods from God
		if (state.movingBlockRenderState.blockState == Blocks.IRON_BARS.defaultBlockState()) {
			radius = 10f;
			height = 37.5f;
			yTranslation = -0.75;

			stack.rotateDegrees(Axis.XN, 90F);
		}

		stack.translate(0.0, 0.25, 0.0);
		stack.scale(radius, height, radius);
		stack.translate(-0.5, yTranslation, -0.5);

		queue.submitMovingBlock(stack, state.movingBlockRenderState, state.outlineColor);
		stack.popPose();
	}

	@Override @Nullable
	public Identifier getTexture(BlockProjectileRenderState state) {
		return null;
	}
}
