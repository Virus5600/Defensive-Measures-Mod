package com.virus5600.defensive_measures.network.receiver.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.network.clientbound.entity.PlayAnimationPacket;
import com.virus5600.defensive_measures.network.serverbound.entity.StopAnimationPacket;

public final class StopAnimationPacketReceiver {
	public static void handle(StopAnimationPacket payload, Context ctx) {
		ServerPlayer player = ctx.player();
		Level world = player.level();

		if (!world.isClientSide()) {
			Entity entity = world.getEntity(payload.id());

			if (entity instanceof TurretEntity turret) {
				String anim = payload.animationName() + "-stop";

				turret.playAnimation(anim);
				ServerPlayNetworking.send(
					player,
					new PlayAnimationPacket(entity.getId(), anim)
				);
			}
		}
	}
}
