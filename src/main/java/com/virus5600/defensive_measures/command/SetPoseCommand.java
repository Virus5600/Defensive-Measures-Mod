package com.virus5600.defensive_measures.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.virus5600.defensive_measures.command.suggestions.PoseSuggestionProvider;

public final class SetPoseCommand {
	public static void register(
		CommandDispatcher<ServerCommandSource> dispatcher,
		CommandRegistryAccess registryAccess,
		RegistrationEnvironment env
	) {
		dispatcher.register(
			CommandManager.literal("setpose")
				.then(
					CommandManager.argument("target", EntityArgumentType.entity())
						.then(
							CommandManager.argument("pose", StringArgumentType.word())
								.suggests(new PoseSuggestionProvider())
								.executes(SetPoseCommand::execute)
						)
				)
		);
	}

	public static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		Entity target = EntityArgumentType.getEntity(ctx, "target");
		String poseStr = StringArgumentType.getString(ctx, "pose");

		if (target instanceof LivingEntity entity) {
			EntityPose pose = EntityPose.valueOf(poseStr.toUpperCase());
			entity.setPose(pose);

			ctx.getSource()
				.sendFeedback(
					() -> Text.translatable(
						"commands.setpose.success",
						target.getName(),
						pose.asString().toLowerCase()
					),
					false
				);

			return 1;
		}

		ctx.getSource()
			.sendError(
				Text.of("commands.setpose.not_living_entity")
			);

		return 0;
	}
}
