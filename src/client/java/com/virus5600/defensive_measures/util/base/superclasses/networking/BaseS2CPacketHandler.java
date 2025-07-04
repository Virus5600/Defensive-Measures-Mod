package com.virus5600.defensive_measures.util.base.superclasses.networking;

import net.minecraft.network.packet.CustomPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;

/**
 * Base class for all S2C packet handlers.
 * <br><br>
 * This class provides a base template for all server-to-client packet
 * handlers, allowing for easy creation and management of packet handlers.
 * It is imperative to extend this class when creating a new S2C packet
 * so that helper methods and other features can be used, along with
 * code standardization.
 * <br><br>
 * When inheriting this class, it is imperative to override the {@link #handle}
 * method to provide the logic for handling the packet. This method will
 * throw an {@link UnsupportedOperationException} if it is not overridden,
 * indicating that the inheritor has not provided a way to handle the
 * packet.
 *
 * @since 1.1.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public abstract class BaseS2CPacketHandler<T extends CustomPayload> {
	/**
	 * Handles the packet when it is received.
	 * <br><br>
	 * This method is called when the packet is received by the client.
	 * It is imperative to override this method when creating a new S2C
	 * packet handler so that the packet can be handled correctly.
	 *
	 * @param payload The payload of the packet.
	 * @param ctx The context of the packet.
	 *
	 * @throws UnsupportedOperationException If this method is not overridden
	 *
	 * @since 1.1.0
	 * @author <a href="https://github.com/Virus5600">Virus5600</a>
	 */
	public static void handle(CustomPayload payload, Context ctx) {
		throw new UnsupportedOperationException("This method should be overridden by the inheritor.");
	}
}
