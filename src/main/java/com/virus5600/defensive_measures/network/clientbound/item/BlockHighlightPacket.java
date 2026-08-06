package com.virus5600.defensive_measures.network.clientbound.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;

/**
 * A packet sent from the server to the client to highlight a block. The block being highlighted
 * can be seen through other blocks as the highlighting is primarily designed to catch the attention
 * of the player to the target block.
 *
 * @param pos           The position of the block to highlight.
 * @param argb          The ARGB color of the highlight.
 * @param durationTicks The duration of the highlight in ticks.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public record BlockHighlightPacket(
	BlockPos pos, int argb, int durationTicks
) implements CustomPacketPayload {
	public static final Identifier ID;
	public static final Type<BlockHighlightPacket> PAYLOAD_ID;
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockHighlightPacket> CODEC_STREAM;

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PAYLOAD_ID;
	}

	static {
		ID = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "block_highlight");
		PAYLOAD_ID = new Type<>(ID);
		CODEC_STREAM = StreamCodec.composite(
			BlockPos.STREAM_CODEC, BlockHighlightPacket::pos,
			ByteBufCodecs.INT, BlockHighlightPacket::argb,
			ByteBufCodecs.INT, BlockHighlightPacket::durationTicks,
			BlockHighlightPacket::new
		);
	}
}
