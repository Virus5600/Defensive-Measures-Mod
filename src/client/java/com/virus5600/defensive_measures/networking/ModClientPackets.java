package com.virus5600.defensive_measures.networking;

/**
 * Class containing all the packet identifiers used by the mod. This is
 * usually used to create the packet handlers and to register the packets
 * themselves.
 * <br><br>
 * Custom packets are usually created when the mod needs to send data from
 * the client to the server or vice versa. This is usually done when the
 * mod needs to sync data between the server and the client and the data is
 * one that doesn't have a vanilla packet to handle it.
 * <br><br>
 * All packets that will be registered in here will be an {@code C2S} or
 * Client-to-Server packets, allowing client-side codes to communicate with
 * the server side. For the opposite, please check {@code ModPackets} on the
 * server or common side package of this project.
 *
 * @since 1.1.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModClientPackets {
	public static void registerHandlers() {
	}
}
