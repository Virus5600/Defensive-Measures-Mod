package com.virus5600.defensive_measures.networking.receiver;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import com.virus5600.defensive_measures.entity.projectiles.MicroMissileEntity;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
import com.virus5600.defensive_measures.sound.RocketEngineLoopSoundInstance;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

public final class MicroMissileSpawnPacketReceiver {
	private static final Map<UUID, RocketEngineLoopSoundInstance> ACTIVE_SOUNDS = Maps.newConcurrentMap();

	public static void handle(Minecraft client) {
		if (client.level == null || client.player == null) return;

		AABB searchBox = client.player.getBoundingBox().inflate(64.0);

		for (MicroMissileEntity missile : client.level.getEntitiesOfClass(
			MicroMissileEntity.class,
			searchBox,
			_ -> true
		)) {
			UUID missileId = missile.getUUID();

			if (!ACTIVE_SOUNDS.containsKey(missileId)) {
				RocketEngineLoopSoundInstance sound = new RocketEngineLoopSoundInstance(
					missile,
					ModSoundEvents.ROCKET_ENGINE_LOOP,
					SoundSource.NEUTRAL
				);

				client.getSoundManager().play(sound);
				ACTIVE_SOUNDS.put(missileId, sound);
			}
		}

		ACTIVE_SOUNDS.entrySet().removeIf(entry -> {
			UUID missileId = entry.getKey();
			RocketEngineLoopSoundInstance sound = entry.getValue();

			Entity entity = client.level.getEntity(missileId);
			return sound == null || sound.isStopped() || entity == null || !entity.isAlive();
		});
	}
}
