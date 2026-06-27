package com.virus5600.defensive_measures.networking.receiver.entity;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.network.clientbound.entity.PlayAnimationPacket;

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
