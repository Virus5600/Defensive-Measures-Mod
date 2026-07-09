package com.virus5600.defensive_measures.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.virus5600.defensive_measures.network.clientbound.entity.PlayAnimationPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import com.virus5600.defensive_measures.command.suggestions.AnimationSuggestionProvider;
import com.virus5600.defensive_measures.command.suggestions.AnimationSuggestionProvider.DMAnimation;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import java.util.Arrays;
import java.util.Collection;

/**
 * A command that plays an animation on a turret entity.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class PlayAnimationCommand {
	private static String turretName = "";
	private static int turretCount = 0;
	private static int skippedCount = 0;

	public static void register(
		CommandDispatcher<CommandSourceStack> dispatcher,
		CommandBuildContext ctx,
		CommandSelection selection
	) {
		dispatcher.register(
			Commands.literal("playanimation")
				.then(
					Commands.argument("target", EntityArgument.entities())
						.then(
							Commands.argument("animation", StringArgumentType.word())
								.suggests(new AnimationSuggestionProvider())
								.executes(PlayAnimationCommand::execute)
						)
				)
		);
	}

	private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
		String animation = StringArgumentType.getString(ctx, "animation");

		if (!entities.isEmpty()) {
			if (animation != null
				&& !animation.isEmpty()
				&& Arrays.stream(DMAnimation.values()).anyMatch(anim -> anim.toString().equals(animation.toUpperCase()))
			) {
				boolean isMany = entities.size() > 1;
				turretName = "";
				turretCount = 0;
				skippedCount = 0;

				for (Entity entity : entities) {
					if (entity instanceof TurretEntity turret) {
						++turretCount;
						turretName = turret.getPlainTextName();

						for (ServerPlayer player : PlayerLookup.tracking(turret)) {
							ServerPlayNetworking.send(
								player,
								new PlayAnimationPacket(turret.getId(), animation)
							);
						}
					}
					else {
						++skippedCount;
					}
				}

				if (isMany) {
					ctx.getSource().sendSuccess(
						() -> Component.translatable(
							"commands.playanimation.success.multiple",
							animation.toLowerCase(),
							turretCount,
							turretCount > 1 ? "turrets" : "turret",
							skippedCount,
							skippedCount > 1 || skippedCount == 0 ? "entities" : "entity"
						),
						false
					);
				}
				else {
					ctx.getSource().sendSuccess(
						() -> Component.translatable(
							"commands.playanimation.success.single",
							turretName,
							animation.toLowerCase()
						),
						false
					);
				}

				return 1;
			}

			ctx.getSource()
				.sendFailure(
					Component.translatable(
						"commands.playanimation.empty_animation",
						animation == null ? "" : animation.toLowerCase()
					)
				);
		}

		ctx.getSource()
			.sendFailure(
				Component.translatable(
					"commands.playanimation.no_target"
				)
			);

		return 0;
	}
}
