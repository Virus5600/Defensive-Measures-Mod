package com.virus5600.defensive_measures.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import org.jspecify.annotations.NonNull;

import java.util.List;

public record TASCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
	public static final StreamCodec<RegistryFriendlyByteBuf, TASCraftingRecipeDisplay> PACKET_CODEC;
	public static final RecipeDisplay.Type<TASCraftingRecipeDisplay> SERIALIZER;
	public static final MapCodec<TASCraftingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
		(instance) -> instance.group(
			Codec.INT.fieldOf("width").forGetter(TASCraftingRecipeDisplay::width),
			Codec.INT.fieldOf("height").forGetter(TASCraftingRecipeDisplay::height),
			SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(TASCraftingRecipeDisplay::ingredients),
			SlotDisplay.CODEC.fieldOf("result").forGetter(TASCraftingRecipeDisplay::result),
			SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(TASCraftingRecipeDisplay::craftingStation)
		).apply(instance, TASCraftingRecipeDisplay::new)
	);

	public TASCraftingRecipeDisplay {
		if (ingredients.size() != width * height) {
			throw new IllegalArgumentException("Invalid shaped recipe display contents");
		}
	}

	@NonNull
	public Type<TASCraftingRecipeDisplay> type() {
		return SERIALIZER;
	}

	public boolean isEnabled(@NonNull FeatureFlagSet features) {
		return this.ingredients
			.stream()
			.allMatch(ingredient -> ingredient.isEnabled(features)) &&
			RecipeDisplay.super.isEnabled(features);
	}

	static {
		PACKET_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, TASCraftingRecipeDisplay::width,
			ByteBufCodecs.VAR_INT, TASCraftingRecipeDisplay::height,
			SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), TASCraftingRecipeDisplay::ingredients,
			SlotDisplay.STREAM_CODEC, TASCraftingRecipeDisplay::result,
			SlotDisplay.STREAM_CODEC, TASCraftingRecipeDisplay::craftingStation,
			TASCraftingRecipeDisplay::new
		);

		SERIALIZER = new RecipeDisplay.Type<>(CODEC, PACKET_CODEC);
	}
}
