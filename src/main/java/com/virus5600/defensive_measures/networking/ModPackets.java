package com.virus5600.defensive_measures.networking;

import com.virus5600.defensive_measures.util.RegistryUtil;
import net.minecraft.util.Identifier;

/**
 * Class containing all the packet identifiers used by the mod. This is
 * usually used to create the packet handlers and to register the packets
 * themselves.
 * <br><br>
 * Custom packets are usually created when the mod needs to send data from
 * the server to the client or vice versa. This is usually done when the
 * mod needs to sync data between the server and the client and the data is
 * one that doesn't have a vanilla packet to handle it.
 * <br><br>
 * All packets that will be registered in here will be an {@code S2C} or
 * Server-to-Client packets, allowing server-side codes to communicate with
 * the client side. For the opposite, please check {@code ModClientPackets} on the
 * client side package of this project.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModPackets {
	public static void registerHandlers() {

	}
}
