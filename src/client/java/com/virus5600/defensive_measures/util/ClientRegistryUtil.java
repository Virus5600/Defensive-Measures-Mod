package com.virus5600.defensive_measures.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayPayloadHandler;

import net.minecraft.network.packet.CustomPayload;

@Environment(EnvType.CLIENT)
public class ClientRegistryUtil {
	// Networking - S2C Packet
	/**
	 * Handles the registration of S2C packet handlers.
	 * <br><br>
	 * This method is used to register S2C packet handlers for the
	 * mod, allowing the mod to execute code when a packet is
	 * received from the server.
	 *
	 * @param id The identifier of the packet.
	 * @param function The function to be executed when the packet is received.
	 * @param <T> The type of the packet.
	 *
	 * @see CustomPayload
	 * @see PlayPayloadHandler
	 * @see ClientPlayNetworking
	 *
	 * @since 1.1.0
	 * @author <a href="https://github.com/Virus5600">Virus5600</a>
	 */
	public static <T extends CustomPayload> void registerS2CPacketHandler(CustomPayload.Id<T> id, PlayPayloadHandler<T> function) {
		// Register S2C packet handlers here
		ClientPlayNetworking.registerGlobalReceiver(id, function);
	}

	// Networking - C2S Packet
	// Method - public static void registerC2SPacketPayload() {}
}
