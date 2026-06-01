package com.virus5600.defensive_measures.network.clientbound.sounds;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.entity.turrets.interfaces.LoopableShootingSound;
import com.virus5600.defensive_measures.DefensiveMeasures;

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
	SoundCategory soundCategory
) implements CustomPayload {
	private static final PacketCodec<ByteBuf, SoundCategory> SOUND_CATEGORY_CODEC;

	public static final Identifier ID;
	public static final Id<TurretLoopSoundPacket> PAYLOAD_ID;
	public static final PacketCodec<RegistryByteBuf, TurretLoopSoundPacket> CODEC_STREAM;

	@Override
	public Id<? extends CustomPayload> getId() {
		return PAYLOAD_ID;
	}

	static {
		SOUND_CATEGORY_CODEC = PacketCodecs.string(32)
			.xmap(
				(name) -> SoundCategory.valueOf(name.toUpperCase()),
				SoundCategory::getName);

		ID = Identifier.of(DefensiveMeasures.MOD_ID, "turret_loop_sound");
		PAYLOAD_ID = new Id<>(ID);
		CODEC_STREAM = PacketCodec.tuple(
			PacketCodecs.INTEGER, TurretLoopSoundPacket::entityId,
			PacketCodecs.BOOLEAN, TurretLoopSoundPacket::start,
			SoundEvent.PACKET_CODEC, TurretLoopSoundPacket::startSound,
			SoundEvent.PACKET_CODEC, TurretLoopSoundPacket::loopSound,
			SoundEvent.PACKET_CODEC, TurretLoopSoundPacket::endSound,
			SOUND_CATEGORY_CODEC, TurretLoopSoundPacket::soundCategory,
			TurretLoopSoundPacket::new
		);
	}
}
