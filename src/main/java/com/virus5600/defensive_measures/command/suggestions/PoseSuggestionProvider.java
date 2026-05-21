package com.virus5600.defensive_measures.command.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.EntityPose;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public final class PoseSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
	@Override
	public CompletableFuture<Suggestions> getSuggestions(
		CommandContext<ServerCommandSource> context,
		SuggestionsBuilder builder
	) throws CommandSyntaxException {
		for (EntityPose pose : EntityPose.values()) {
			builder.suggest(pose.name().toLowerCase());
		}
		return builder.buildFuture();
	}
}
