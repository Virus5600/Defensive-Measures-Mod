package com.virus5600.defensive_measures.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.network.clientbound.sounds.TurretLoopSoundPacket;
import com.virus5600.defensive_measures.networking.receiver.TurretLoopSoundReceiver;

/**
 * Class containing all the client packet identifiers used by the mod. This is usually used to
 * create the client packet handlers and to register the client packets themselves.
 * <br><br>
 * Custom packets are usually created when the mod needs to send data from the server to the client
 * or vice versa. This is usually done when the mod needs to sync data between the server and the
 * client and the data is one that doesn't have a vanilla packet to handle it.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModClientPackets {
	public static void registerClientPackets() {
		DefensiveMeasures.LOGGER.info("REGISTERING CLIENT PACKETS FOR {}...", DefensiveMeasures.MOD_NAME);

		// ///////////// //
		// C2S - INBOUND //
		// ///////////// //

		// v1.1.0-beta
		ClientPlayNetworking.registerGlobalReceiver(TurretLoopSoundPacket.PAYLOAD_ID, TurretLoopSoundReceiver::handle);
	}
}
