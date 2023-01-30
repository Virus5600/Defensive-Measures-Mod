package com.virus5600.DefensiveMeasures.networking;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.networking.packets.SoundEvent.*;
import com.virus5600.DefensiveMeasures.networking.packets.SpawnEvent.*;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {
	public static final Identifier SPAWN_ENTITY_ID = new Identifier(DefensiveMeasures.MOD_ID, "spawn_entity");
	public static final Identifier SOUND_EVENT_ID = new Identifier(DefensiveMeasures.MOD_ID, "sound_event");

	/**
	 * A packet registry for <b>Client-to-Server</b>
	 */
	public static void registerC2SPackets() {
		ServerPlayNetworking.registerGlobalReceiver(SPAWN_ENTITY_ID, SpawnEventC2SPacket::receive);
		ServerPlayNetworking.registerGlobalReceiver(SOUND_EVENT_ID, SoundEventC2SPacket::receive);
	}

	/**
	 *A packet registry for <b>Server-to-Client</b>
	 */
	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(SPAWN_ENTITY_ID, SpawnEventS2CPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(SPAWN_ENTITY_ID, SoundEventS2CPacket::receive);
	}
}