package com.virus5600.DefensiveMeasures.networking.packets.EntityStatusEffect;

import com.virus5600.DefensiveMeasures.networking.ModPackets;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class EntityStatusEffectC2S {
	/**
	 * Sends a packet from the server to the client.
	 * @param entityId
	 * @param effect
	 */
	@SuppressWarnings("deprecation")
	public static Packet<?> send(int entityId, StatusEffectInstance effect) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		byte flags = 0;
		if (effect.isAmbient())
			flags = (byte) (flags | 1);
		if (effect.shouldShowParticles())
			flags = (byte) (flags | 2);
		if (effect.shouldShowIcon())
			flags = (byte) (flags | 4);

		buf.writeVarInt(entityId);

		buf.writeRegistryValue(Registry.STATUS_EFFECT, effect.getEffectType());
		buf.writeByte((byte) effect.getAmplifier() & 0xFF);
		buf.writeVarInt(effect.getDuration());
		buf.writeByte(flags);
		buf.writeNullable(effect.getFactorCalculationData().orElse(null), (buf2, factorCalculationData) -> buf2.encode(StatusEffectInstance.FactorCalculationData.CODEC, factorCalculationData));

		return ServerPlayNetworking.createS2CPacket(ModPackets.ENTITY_STATUS_EFFECT, buf);
	}
}
