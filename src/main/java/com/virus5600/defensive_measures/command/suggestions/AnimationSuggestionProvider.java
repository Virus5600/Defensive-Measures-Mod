package com.virus5600.defensive_measures.command.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public final class AnimationSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
	@Override
	public CompletableFuture<Suggestions> getSuggestions(
		CommandContext<CommandSourceStack> ctx,
		SuggestionsBuilder builder
	) throws CommandSyntaxException {
		for (DMAnimation anim : DMAnimation.values()) {
			builder.suggest(anim.name().toLowerCase());
		}

		return builder.buildFuture();
	}

	public enum DMAnimation {
		IDLE,
		SETUP,
		TEARDOWN,
		SHOOT,
		DEATH
		;
	}
}
