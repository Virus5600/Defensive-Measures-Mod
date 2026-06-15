package com.virus5600.defensive_measures.network.clientbound.sounds;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.entity.turrets.interfaces.LoopableShootingSound;

import org.jspecify.annotations.NonNull;

import io.netty.buffer.ByteBuf;

/**
 * A packet sent from the server to the client to play or stop a looping loopSound for a turret. This
 * is used to play the shooting loopSound for turrets that implement the {@link LoopableShootingSound}
 * interface, allowing the shooting loopSound to loop while the turret is shooting and stop when it is
 * not.
 *
 * @param entityId The entity ID of the turret for which to play or stop the loopSound.
 * @param start Whether to start or stop the loopSound. If true, the client will start playing the loopSound. If false, the client will stop playing the loopSound.
 * @param startSound The loopSound event to play when starting the loopSound. This is used by the client to play a one-shot loopSound at the start of the shooting action, such as a firing loopSound or a charge-up loopSound.
 * @param loopSound The loopSound event to play or stop. This is used by the client to identify which loopSound to play or stop, allowing for different turrets to have different shooting sounds if desired.
 * @param endSound The loopSound event to play when stopping the loopSound. This is used by the client to play a one-shot loopSound at the end of the shooting action, such as a firing stop loopSound or a cool-down loopSound.
 * @param soundCategory The loopSound category to play the loopSound under. This is used by the client to determine the volume and other properties of the loopSound when it is played.
 */
public record TurretLoopSoundPacket(
        int entityId, boolean start,
        SoundEvent startSound, SoundEvent loopSound, SoundEvent endSound,
        SoundSource soundCategory
) implements CustomPacketPayload {
	private static final StreamCodec<ByteBuf, SoundSource> SOUND_CATEGORY_CODEC;

	public static final Identifier ID;
	public static final Type<TurretLoopSoundPacket> PAYLOAD_ID;
	public static final StreamCodec<RegistryFriendlyByteBuf, TurretLoopSoundPacket> CODEC_STREAM;

	@Override @NonNull
	public Type<? extends CustomPacketPayload> type() {
		return PAYLOAD_ID;
	}

	static {
		SOUND_CATEGORY_CODEC = ByteBufCodecs.stringUtf8(32)
			.map(
				(name) -> SoundSource.valueOf(name.toUpperCase()),
				SoundSource::getName);

		ID = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "turret_loop_sound");
		PAYLOAD_ID = new Type<>(ID);
		CODEC_STREAM = StreamCodec.composite(
			ByteBufCodecs.INT, TurretLoopSoundPacket::entityId,
			ByteBufCodecs.BOOL, TurretLoopSoundPacket::start,
			SoundEvent.DIRECT_STREAM_CODEC, TurretLoopSoundPacket::startSound,
			SoundEvent.DIRECT_STREAM_CODEC, TurretLoopSoundPacket::loopSound,
			SoundEvent.DIRECT_STREAM_CODEC, TurretLoopSoundPacket::endSound,
			SOUND_CATEGORY_CODEC, TurretLoopSoundPacket::soundCategory,
			TurretLoopSoundPacket::new
		);
	}
}
