package com.virus5600.defensive_measures.network.clientbound.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;

import org.jspecify.annotations.NonNull;

public record PlayAnimationPacket(
	int entityId, String animationName
) implements CustomPacketPayload {
	public static final Identifier ID;
	public static final Type<PlayAnimationPacket> PAYLOAD_ID;
	public static final StreamCodec<RegistryFriendlyByteBuf, PlayAnimationPacket> CODEC_STREAM;

	@Override @NonNull
	public Type<? extends CustomPacketPayload> type() {
		return PAYLOAD_ID;
	}

	static {
		ID = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "playanimation");
		PAYLOAD_ID = new Type<>(ID);
		CODEC_STREAM = StreamCodec.composite(
			ByteBufCodecs.INT, PlayAnimationPacket::entityId,
			ByteBufCodecs.STRING_UTF8, PlayAnimationPacket::animationName,
			PlayAnimationPacket::new
		);
	}
}
