package com.virus5600.defensive_measures.network.serverbound.entity;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;

import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record StopAnimationPacket(
	UUID id, String animationName
) implements CustomPacketPayload {
	public static final Identifier ID;
	public static final Type<StopAnimationPacket> PAYLOAD_ID;
	public static final StreamCodec<RegistryFriendlyByteBuf, StopAnimationPacket> CODEC_STREAM;

	@Override @NonNull
	public Type<? extends CustomPacketPayload> type() {
		return PAYLOAD_ID;
	}

	static {
		ID = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "stopanimation");
		PAYLOAD_ID = new Type<>(ID);
		CODEC_STREAM = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, StopAnimationPacket::id,
			ByteBufCodecs.STRING_UTF8, StopAnimationPacket::animationName,
			StopAnimationPacket::new
		);
	}
}
