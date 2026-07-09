package com.virus5600.defensive_measures.network.receiver.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.network.clientbound.entity.PlayAnimationPacket;
import com.virus5600.defensive_measures.network.serverbound.entity.StopAnimationPacket;

/**
 * A packet receiver that handles the {@link StopAnimationPacket} sent from the client to the
 * server. It stops the specified animation on a turret entity and sends a PlayAnimationPacket back
 * to the client to synchronize the animation state.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
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
