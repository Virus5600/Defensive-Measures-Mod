package com.virus5600.defensive_measures.networking.receiver.item;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.core.BlockPos;

import com.virus5600.defensive_measures.network.clientbound.item.BlockHighlightListPacket;
import com.virus5600.defensive_measures.network.clientbound.item.BlockHighlightPacket;
import com.virus5600.defensive_measures.renderer.BlockHighlightRenderer;

/**
 * A client-side packer receiver for the {@link BlockHighlightPacket} or
 * {@link BlockHighlightListPacket}. This receiver just basically calls the
 * {@link BlockHighlightRenderer#highlight(BlockPos, int, int)} method with the data from the
 * packet.
 * <br><br>
 * The actual logic is already handled by the renderer, leaving a clean packet receiver.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see BlockHighlightPacket
 * @see BlockHighlightListPacket
 * @see BlockHighlightRenderer
 * @see BlockHighlightRenderer#highlight(BlockPos, int, int)
 */
public final class BlockHighlightPacketReceiver {
	public static void handle(BlockHighlightPacket payload, Context ctx) {
		BlockHighlightRenderer.highlight(
			payload.pos(),
			payload.argb(), payload.durationTicks()
		);
	}

	public static void handle(BlockHighlightListPacket payload, Context ctx) {
		for (BlockPos pos : payload.posList()) {
			BlockHighlightRenderer.highlight(
				pos,
				payload.argb(), payload.durationTicks()
			);
		}
	}
}
