package com.virus5600.defensive_measures.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.network.clientbound.sounds.TurretLoopSoundPacket;

/**
 * Class containing all the packet identifiers used by the mod. This is usually used to create the
 * packet handlers and to register the packets themselves.
 * <br><br>
 * Custom packets are usually created when the mod needs to send data from the server to the client
 * or vice versa. This is usually done when the mod needs to sync data between the server and the
 * client and the data is one that doesn't have a vanilla packet to handle it.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public final class ModPackets {
	public static void registerModPackets() {
		DefensiveMeasures.LOGGER.info("REGISTERING PACKETS FOR {}...", DefensiveMeasures.MOD_NAME);

		// ////////////// //
		// S2C - OUTBOUND //
		// ////////////// //

		// v1.1.0-beta
		PayloadTypeRegistry.playS2C().register(TurretLoopSoundPacket.PAYLOAD_ID, TurretLoopSoundPacket.CODEC_STREAM);
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //
}
