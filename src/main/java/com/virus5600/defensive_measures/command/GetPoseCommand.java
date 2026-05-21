package com.virus5600.defensive_measures.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class GetPoseCommand {
	public static void register(
		CommandDispatcher<ServerCommandSource> dispatcher,
		CommandRegistryAccess registryAccess,
		CommandManager.RegistrationEnvironment env
	) {
		dispatcher.register(
			CommandManager.literal("getpose")
				.then(
					CommandManager.argument("target", EntityArgumentType.entity())
						.executes(GetPoseCommand::execute)
				)
		);
	}

	public static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		Entity target = EntityArgumentType.getEntity(ctx, "target");

		if (target instanceof LivingEntity entity) {
			EntityPose pose = entity.getPose();

			ctx.getSource()
				.sendFeedback(
					() -> Text.translatable(
						"commands.getpose.success",
						target.getName().getString(),
						pose.asString().toLowerCase()
					),
					false
				);

			return 1;
		}

		ctx.getSource()
			.sendError(
				Text.of("commands.getpose.not_living_entity")
			);

		return 0;
	}
}
