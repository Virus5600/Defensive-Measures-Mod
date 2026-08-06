package com.virus5600.defensive_measures.networking.receiver.entity;

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

/**
 * A client-side packet receiver for the {@link MicroMissileSpawnPacketReceiver}. Specifically,
 * when the missile spawns in, is also still alive, and the sound is still playing.
 * <br><br>
 * This receiver handles the logic of starting and stopping the rocket engine loop sound for micro
 * missiles. When a micro missile is detected within a certain range of the player, it checks if
 * the sound is already active for that missile. If not, it starts playing the rocket engine loop
 * sound. It also cleans up any sounds that are no longer needed, such as when the missile is
 * destroyed or the sound has stopped playing.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
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
