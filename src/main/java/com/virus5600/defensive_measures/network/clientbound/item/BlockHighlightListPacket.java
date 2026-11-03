package com.virus5600.defensive_measures.network.clientbound.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;

import java.util.List;

/**
 * A packet sent from the server to the client to highlight a list of blocks. The blocks being
 * highlighted can be seen through other blocks as the highlighting is primarily designed to catch
 * the attention of the player to the target block.
 *
 * @param posList       The list of positions of the blocks to highlight.
 * @param argb          The ARGB color of the highlight. Though, the alpha channel is not used, it is still required to be set to 255 (fully opaque) for the color to be visible.
 * @param durationTicks The duration of the highlight in ticks.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see BlockHighlightPacket
 */
public record BlockHighlightListPacket(
	List<BlockPos> posList, int argb, int durationTicks
) implements CustomPacketPayload {
	public static final Identifier ID;
	public static final Type<BlockHighlightListPacket> PAYLOAD_ID;
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockHighlightListPacket> CODEC_STREAM;

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PAYLOAD_ID;
	}

	static {
		ID = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "block_highlight_list");
		PAYLOAD_ID = new Type<>(ID);
		CODEC_STREAM = StreamCodec.composite(
			BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list(4096)), BlockHighlightListPacket::posList,
			ByteBufCodecs.INT, BlockHighlightListPacket::argb,
			ByteBufCodecs.INT, BlockHighlightListPacket::durationTicks,
			BlockHighlightListPacket::new
		);
	}
}
