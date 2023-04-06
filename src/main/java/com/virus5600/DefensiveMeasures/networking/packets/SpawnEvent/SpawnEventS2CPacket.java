package com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent;

import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class SpawnEventS2CPacket {
	/**
	 * Receives a packet from the server and executes them on the client
	 * @param client
	 * @param handler
	 * @param buf
	 * @param responseSender
	 */
	public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		EntityType<?> type = Registry.ENTITY_TYPE.get(buf.readVarInt());
		UUID entityUUID = buf.readUuid();
		int entityID = buf.readVarInt();
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();
		float pitch = (buf.readByte() * 360) / 256.0F;
		float yaw = (buf.readByte() * 360) / 256.0F;

		client.execute(() -> {
			MinecraftClient instance = MinecraftClient.getInstance();
			ClientWorld world = instance.world;
			Entity entity = type.create(world);
			if (entity != null) {
				entity.updatePosition(x, y, z);
				entity.updateTrackedPosition(x, y, z);
				entity.setPitch(pitch);
				entity.setYaw(yaw);
				entity.setId(entityID);
				entity.setUuid(entityUUID);
				world.addEntity(entityID, entity);
			}
		});
	}
}
