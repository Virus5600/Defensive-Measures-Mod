package com.virus5600.DefensiveMeasures.networking.packets.SoundEvent;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class SoundEventS2CPacket {
	/**
	 * Receives a packet from the server and executes them on the client
	 * @param client
	 * @param handler
	 * @param buf
	 * @param responseSender
	 */
	public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
	}
}