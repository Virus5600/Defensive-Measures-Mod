package com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent;

import com.virus5600.DefensiveMeasures.networking.ModPackets;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class SpawnEventC2SPacket {
	/**
	 * Receives a packet from the client and executes them on the server.
	 * @param server
	 * @param player
	 * @param handler
	 * @param buf
	 * @param responseSender
	 */
	public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
	}

	/**
	 * Sends a packet from the server to the client.
	 * @param entity
	 */
	public static Packet<?> send(Entity entity) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
		buf.writeUuid(entity.getUuid());
		buf.writeVarInt(entity.getId());

		buf.writeDouble(entity.getX());
		buf.writeDouble(entity.getY());
		buf.writeDouble(entity.getZ());

		buf.writeByte(MathHelper.floor(entity.getPitch() * 256.0F / 360.0F));
		buf.writeByte(MathHelper.floor(entity.getYaw() * 256.0F / 360.0F));
		buf.writeFloat(entity.getPitch());
		buf.writeFloat(entity.getYaw());

		return ServerPlayNetworking.createS2CPacket(ModPackets.SPAWN_ENTITY_ID, buf);
	}
}