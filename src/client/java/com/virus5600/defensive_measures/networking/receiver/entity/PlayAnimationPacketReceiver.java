package com.virus5600.defensive_measures.networking.receiver.entity;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.network.clientbound.entity.PlayAnimationPacket;

/**
 * A client-side packet receiver for the {@link PlayAnimationPacket}. This receiver handles the
 * basic logic of when to trigger the {@link TurretEntity#playAnimation(String)} method.
 * <br><br>
 * As this method is exclusive to all {@link TurretEntity}, it only has to check if the entity do
 * extend (or specifically, an instance of) the said class.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public final class PlayAnimationPacketReceiver {
	public static void handle(PlayAnimationPacket payload, Context ctx) {
		Minecraft client = ctx.client();

		if (client.level == null || client.player == null) return;

		Entity entity = client.level.getEntity(payload.entityId());
		if (entity instanceof TurretEntity turret) {
			turret.playAnimation(payload.animationName());
		}
	}
}
