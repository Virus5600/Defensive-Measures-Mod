package com.virus5600.defensive_measures.networking.receiver;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;

import com.virus5600.defensive_measures.entity.projectiles.MicroMissileEntity;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
import com.virus5600.defensive_measures.sound.RocketEngineLoopSoundInstance;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

public final class MicroMissileSpawnPacketReceiver {
	private static final Map<UUID, RocketEngineLoopSoundInstance> ACTIVE_SOUNDS = Maps.newConcurrentMap();

	public static void handle(MinecraftClient client) {
		if (client.world == null || client.player == null) return;

		Box searchBox = client.player.getBoundingBox().expand(64.0);

		for (MicroMissileEntity missile : client.world.getEntitiesByClass(
			MicroMissileEntity.class,
			searchBox,
			missile -> true
		)) {
			UUID missileId = missile.getUuid();

			if (!ACTIVE_SOUNDS.containsKey(missileId)) {
				RocketEngineLoopSoundInstance sound = new RocketEngineLoopSoundInstance(
					missile,
					ModSoundEvents.ROCKET_ENGINE_LOOP,
					SoundCategory.NEUTRAL
				);

				client.getSoundManager().play(sound);
				ACTIVE_SOUNDS.put(missileId, sound);
			}
		}

		ACTIVE_SOUNDS.entrySet().removeIf(entry -> {
			UUID missileId = entry.getKey();
			RocketEngineLoopSoundInstance sound = entry.getValue();

			Entity entity = client.world.getEntity(missileId);
			return sound == null || sound.isDone() || entity == null || !entity.isAlive();
		});
	}
}
