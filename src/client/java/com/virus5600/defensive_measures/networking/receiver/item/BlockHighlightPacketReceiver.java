package com.virus5600.defensive_measures.networking.receiver.item;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;

import com.virus5600.defensive_measures.network.clientbound.item.BlockHighlightPacket;
import com.virus5600.defensive_measures.renderer.BlockHighlightRenderer;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public final class BlockHighlightPacketReceiver {
	public static void handle(BlockHighlightPacket payload, Context ctx) {
		BlockHighlightRenderer.highlight(
			payload.pos(),
			payload.argb(), payload.durationTicks()
		);
	}
}
