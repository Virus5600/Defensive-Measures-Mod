package com.virus5600.defensive_measures.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;

/**
 * A command that reloads the configuration files of this mod.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ReloadConfigCommand {
	public static void register(
		CommandDispatcher<CommandSourceStack> dispatcher,
		CommandBuildContext ctx,
		CommandSelection selection
	) {
		dispatcher.register(
			Commands.literal("dm:reload_config")
				.executes(ReloadConfigCommand::execute)
				.then(
					Commands.argument("config", StringArgumentType.greedyString())
						.executes(ReloadConfigCommand::execute)
				)
		);
	}

	private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		String config;

		try {
			config = StringArgumentType.getString(ctx, "config");
		} catch (IllegalArgumentException e) {
			config = null;
		}

		// Without a specific targeted config file path.
		if (config == null) {

		}

		return 0;
	}
}
