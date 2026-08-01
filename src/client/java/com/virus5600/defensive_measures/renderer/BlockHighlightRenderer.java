package com.virus5600.defensive_measures.renderer;


import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.DefensiveMeasures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

import com.google.common.collect.Maps;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A renderer that highlights blocks in the world for a certain duration.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BlockHighlightRenderer {
	private static final Map<BlockPos, HighlightEntry> highlights = Maps.newHashMap();
	private static final Map<BlockPos, BlockState> previousBlockstates = Maps.newHashMap();

	private static List<HighlightRenderState> renderStates = List.of();
	public static RenderPipeline HIGHLIGHT_NO_DEPTH_PIPELINE;
	public static RenderType HIGHLIGHT_NO_DEPTH;

	private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
	private static final Vector3f MODEL_OFFSET = new Vector3f();
	private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
	private static final StagedVertexBuffer STAGED_BUFFER = new StagedVertexBuffer(
		() -> DefensiveMeasures.MOD_ID + "_block_highlight_buffer",
		RenderType.SMALL_BUFFER_SIZE
	);

	public static void init() {
		HIGHLIGHT_NO_DEPTH_PIPELINE = RenderPipelines.register(
			RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
				.withLocation(Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "pipeline/block_highlight_no_depth"))
				.withDepthStencilState(Optional.empty())
				.build()
		);

		HIGHLIGHT_NO_DEPTH = RenderType.create(
			DefensiveMeasures.MOD_ID + "_block_highlight_no_depth",
			RenderSetup.builder(HIGHLIGHT_NO_DEPTH_PIPELINE)
				.createRenderSetup()
		);

		LevelExtractionEvents.END_EXTRACTION.register(BlockHighlightRenderer::extractHighlights);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(BlockHighlightRenderer::renderAndDrawHighlights);
	}

	// /////// //
	// METHODS //
	// /////// //

	/**
	 * Adds a block to be highlighted for a fixed duration.
	 *
	 * @param pos           The position of the block to highlight.
	 * @param argb          The color of the highlight in ARGB format.
	 * @param durationTicks The duration of the highlight in ticks.
	 */
	public static void highlight(BlockPos pos, int argb, int durationTicks) {
		highlights.put(pos, new HighlightEntry(argb, currentTick(), durationTicks));
		previousBlockstates.put(pos, null); // Initialize to null
	}

	public static void clear(BlockPos pos) {
		highlights.remove(pos);
		previousBlockstates.remove(pos);
	}

	public static void clearAll() {
		highlights.clear();
	}

	// //////////////// //
	// EXTRACTION PHASE //
	// //////////////// //

	/**
	 * Snapshots the current highlight state into immutable render states. Also handles expiring
	 * highlights past their duration. Must run before the drawing phase, since world/state access
	 * (like {@link Level#getBlockState(BlockPos)}) is only safe here.
	 *
	 * @param context The extraction context, providing access to the level.
	 */
	private static void extractHighlights(LevelExtractionContext context) {
		Level level = context.level();
		long now = currentTick();

		List<HighlightRenderState> states = new ArrayList<>(highlights.size());

		Iterator<Map.Entry<BlockPos, HighlightEntry>> it = highlights.entrySet().iterator();
		while (it.hasNext()) {
			var entry = it.next();
			HighlightEntry data = entry.getValue();

			long age = now - data.addedAtTick();
			if (age > data.durationTicks()) {
				it.remove();
				continue;
			}

			BlockPos pos = entry.getKey();
			BlockState state = level.getBlockState(pos);

			// Check if the current block state is already in the previousBlockstates map
			if (previousBlockstates.putIfAbsent(pos, state) != null) {
				BlockState prevState = previousBlockstates.get(pos);

				// If the current block state does not match its initial block state, stop from marking it.
				if (prevState != state) {
					it.remove();
					clear(pos);
					continue;
				}
			}

			float fadeProgress = age / (float) data.durationTicks();
			float alpha = Mth.lerp(fadeProgress, 0.75f, 0f);

			VoxelShape shape = state.getShape(level, pos);
			AABB bounds = shape.isEmpty() ? Shapes.block().bounds() : shape.bounds();

			double size = bounds.getSize();
			if (size < 0.25 && size != 0) {
				bounds = bounds.inflate(size / (size / 0.25));
			}

			states.add(new HighlightRenderState(pos, bounds, data.color(), alpha));
		}

		renderStates = states;
	}

	// ///////////// //
	// DRAWING PHASE //
	// ///////////// //

	/**
	 * Draws all highlight boxes captured during the extraction phase.
	 *
	 * @param context The render context, providing the pose stack and camera state.
	 */
	private static void renderAndDrawHighlights(LevelRenderContext context) {
		if (renderStates.isEmpty()) return;

		RenderPipeline pipeline = HIGHLIGHT_NO_DEPTH_PIPELINE;
		VertexFormat formatBinding = pipeline.getVertexFormatBinding(0);
		assert formatBinding != null;

		PrimitiveTopology primitive = pipeline.getPrimitiveTopology();
		StagedVertexBuffer.Draw draw = STAGED_BUFFER.appendDraw(
			formatBinding, primitive,
			primitive == PrimitiveTopology.QUADS ?
				RenderSystem.getProjectionType().vertexSorting() : null
		);

		PoseStack pose = context.poseStack();
		Vec3 camPos = context.levelState().cameraRenderState.pos;
		VertexConsumer consumer = STAGED_BUFFER.getVertexBuilder(draw);

		pose.pushPose();
		pose.translate(-camPos.x, -camPos.y, -camPos.z);

		for (HighlightRenderState state : renderStates) {
			renderBox(
				pose, consumer,
				state.pos(), state.bounds(),
				state.color(), state.alpha()
			);
		}

		pose.popPose();

		STAGED_BUFFER.upload();

		StagedVertexBuffer.ExecuteInfo info = STAGED_BUFFER.getExecuteInfo(draw);
		if (info != null) {
			draw(Minecraft.getInstance(), info, pipeline);
		}

		STAGED_BUFFER.endFrame();
	}

	// ///////// //
	// CORE DRAW //
	// ///////// //

	private static void renderBox(
		PoseStack pose, VertexConsumer consumer,
		BlockPos pos, AABB bounds,
		int rgbColor, float alpha
	) {
		pose.pushPose();

		pose.translate(pos.getX(), pos.getY(), pos.getZ());

		double centerX = (bounds.minX + bounds.maxX) / 2.0;
		double centerY = (bounds.minY + bounds.maxY) / 2.0;
		double centerZ = (bounds.minZ + bounds.maxZ) / 2.0;

		double halfX = (bounds.maxX - bounds.minX) / 2.0;
		double halfY = (bounds.maxY - bounds.minY) / 2.0;
		double halfZ = (bounds.maxZ - bounds.minZ) / 2.0;

		pose.translate(centerX, centerY, centerZ);
		pose.scale((float) halfX, (float) halfY, (float) halfZ);

		Matrix4f matrix = pose.last().pose();
		int r = (rgbColor >> 16) & 0xFF;
		int g = (rgbColor >> 8) & 0xFF;
		int b = rgbColor & 0xFF;
		int color = ARGB.color((int) (alpha * 255), r, g, b);

		drawBoxOutline(consumer, matrix, color);

		pose.popPose();
	}

	private static void drawBoxOutline(VertexConsumer consumer, Matrix4f matrix, int color) {
		// -Z
		consumer.addVertex(matrix, -1, -1, -1).setColor(color);
		consumer.addVertex(matrix, -1, 1, -1).setColor(color);
		consumer.addVertex(matrix, 1, 1, -1).setColor(color);
		consumer.addVertex(matrix, 1, -1, -1).setColor(color);

		// +Z
		consumer.addVertex(matrix, -1, -1, 1).setColor(color);
		consumer.addVertex(matrix, 1, -1, 1).setColor(color);
		consumer.addVertex(matrix, 1, 1, 1).setColor(color);
		consumer.addVertex(matrix, -1, 1, 1).setColor(color);

		// -Y
		consumer.addVertex(matrix, -1, -1, -1).setColor(color);
		consumer.addVertex(matrix, 1, -1, -1).setColor(color);
		consumer.addVertex(matrix, 1, -1, 1).setColor(color);
		consumer.addVertex(matrix, -1, -1, 1).setColor(color);

		// +Y
		consumer.addVertex(matrix, -1, 1, -1).setColor(color);
		consumer.addVertex(matrix, -1, 1, 1).setColor(color);
		consumer.addVertex(matrix, 1, 1, 1).setColor(color);
		consumer.addVertex(matrix, 1, 1, -1).setColor(color);

		// -X
		consumer.addVertex(matrix, -1, -1, -1).setColor(color);
		consumer.addVertex(matrix, -1, -1, 1).setColor(color);
		consumer.addVertex(matrix, -1, 1, 1).setColor(color);
		consumer.addVertex(matrix, -1, 1, -1).setColor(color);

		// +X
		consumer.addVertex(matrix, 1, -1, -1).setColor(color);
		consumer.addVertex(matrix, 1, 1, -1).setColor(color);
		consumer.addVertex(matrix, 1, 1, 1).setColor(color);
		consumer.addVertex(matrix, 1, -1, 1).setColor(color);
	}

	private static void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info, RenderPipeline pipeline) {
		GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
			.writeTransform(
				RenderSystem.getModelViewMatrixCopy(),
				COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX
			);

		RenderTarget mainTarget = client.gameRenderer.mainRenderTarget();
		GpuTextureView colorTexture = mainTarget.getColorTextureView();
		assert colorTexture != null;

		try (RenderPass renderPass = RenderSystem.getDevice()
			.createCommandEncoder()
			.createRenderPass(
				() -> DefensiveMeasures.MOD_ID + " block highlight rendering",
				colorTexture, Optional.empty(), mainTarget.getDepthTextureView(), OptionalDouble.empty()
			)) {
			renderPass.setPipeline(pipeline);

			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setUniform("DynamicTransforms", dynamicTransforms);

			renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
			renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());

			renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
		}
	}

	private static long currentTick() {
		return Minecraft.getInstance().level.getGameTime();
	}

	public static void close() {
		STAGED_BUFFER.close();
	}

	// ////// //
	// RECORD //
	// ////// //

	private record HighlightEntry(int color, long addedAtTick, int durationTicks) {}
	private record HighlightRenderState(BlockPos pos, AABB bounds, int color, float alpha) {}
}
