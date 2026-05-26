package com.virus5600.defensive_measures.networking.receiver;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.entity.turrets.interfaces.LoopableShootingSound;
import com.virus5600.defensive_measures.network.clientbound.sounds.TurretLoopSoundPacket;
import com.virus5600.defensive_measures.sound.LoopingShootSoundInstance;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * A client-side packet receiver for the {@link TurretLoopSoundPacket}. This receiver handles the
 * logic of starting and stopping the shooting loopSound loop for turrets that implement the
 * {@link LoopableShootingSound} interface. When a packet is received, it checks if the specified
 * entity is a turret and either starts or stops the shooting loopSound loop based on the packet's
 * data. This allows for a more immersive and dynamic loopSound experience, as the shooting
 * loopSound will only play while the turret is actively shooting and will not continue to play
 * indefinitely if the turret is destroyed or removed from the world.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public final class TurretLoopSoundReceiver {
	private static final Map<Integer, LoopingShootSoundInstance> ACTIVE_SOUNDS = Maps.newConcurrentMap();

	public static void handle(TurretLoopSoundPacket payload, Context ctx) {
		@SuppressWarnings("resource")
		MinecraftClient client = ctx.client();

		client.execute(() -> {
			ClientWorld world = client.world;

			if (world == null) return;

			Entity entity = world.getEntityById(payload.entityId());
			boolean shouldStart = payload.start();

			if (entity instanceof TurretEntity turret) {
				SoundManager manager = client.getSoundManager();

				if (shouldStart) {
					SoundCategory category = payload.soundCategory();
					SoundEvent startSound = payload.startSound();
					SoundEvent loopSound = payload.loopSound();
					SoundEvent endSound = payload.endSound();

					LoopingShootSoundInstance instance = new LoopingShootSoundInstance(
						turret,
						startSound,
						loopSound,
						endSound,
						category
					);

					if (ACTIVE_SOUNDS.putIfAbsent(payload.entityId(), instance) == null) {
						instance.startLoop();
					}
				}
				else {
					LoopingShootSoundInstance instance = ACTIVE_SOUNDS.remove(payload.entityId());

					if (instance != null) {
						instance.endLoop();
					}
				}
			}
		});
	}
}
