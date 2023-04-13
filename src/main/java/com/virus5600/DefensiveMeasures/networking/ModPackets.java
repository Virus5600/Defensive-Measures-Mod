package com.virus5600.DefensiveMeasures.networking;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent.SpawnEventC2SPacket;
import com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent.SpawnEventS2CPacket;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public final class ModPackets {
	private ModPackets() { }

	public static final Identifier SPAWN_ENTITY_ID = new Identifier(DefensiveMeasures.MOD_ID, "spawn_entity");
	public static final Identifier ENTITY_STATUS_EFFECT = new Identifier(DefensiveMeasures.MOD_ID, "entity_status_effect");

	/**
	 * A packet registry for <b>Client-to-Server</b>.
	 */
	public static void registerC2SPackets() {
		ServerPlayNetworking.registerGlobalReceiver(SPAWN_ENTITY_ID, SpawnEventC2SPacket::receive);
	}

	/**
	 *A packet registry for <b>Server-to-Client</b>.
	 */
	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(SPAWN_ENTITY_ID, SpawnEventS2CPacket::receive);
	}
}
