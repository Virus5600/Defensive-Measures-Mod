package com.virus5600.defensive_measures.networking;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.network.clientbound.entity.*;
import com.virus5600.defensive_measures.network.clientbound.item.*;
import com.virus5600.defensive_measures.network.clientbound.sound.*;
import com.virus5600.defensive_measures.networking.receiver.entity.*;
import com.virus5600.defensive_measures.networking.receiver.item.*;
import com.virus5600.defensive_measures.networking.receiver.sound.*;

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

		// /////// //
		// INBOUND //
		// /////// //

		// v1.1.0-beta
		ClientPlayNetworking.registerGlobalReceiver(TurretLoopSoundPacket.PAYLOAD_ID, TurretLoopSoundPacketReceiver::handle);

		ClientTickEvents.END_CLIENT_TICK.register(MicroMissileSpawnPacketReceiver::handle);

		// v1.2.0-beta
		ClientPlayNetworking.registerGlobalReceiver(PlayAnimationPacket.PAYLOAD_ID, PlayAnimationPacketReceiver::handle);
		ClientPlayNetworking.registerGlobalReceiver(BlockHighlightPacket.PAYLOAD_ID, BlockHighlightPacketReceiver::handle);

	}
}
