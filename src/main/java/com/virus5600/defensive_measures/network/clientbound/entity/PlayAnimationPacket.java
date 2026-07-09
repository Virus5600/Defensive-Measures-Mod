package com.virus5600.defensive_measures.network.clientbound.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.CommonTurretAnimations;

import org.jspecify.annotations.NonNull;

/**
 * A packet that is sent from the server to the client to play an animation on an entity.
 *
 * @param entityId      The ID of the entity for which to play the animation.
 * @param animationName The name of the animation to play.
 *
 * @see CommonTurretAnimations
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
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
