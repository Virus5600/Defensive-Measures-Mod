package com.virus5600.defensive_measures.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.resource.featuretoggle.FeatureSet;

import java.util.List;

public record TASCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
	public static final PacketCodec<RegistryByteBuf, TASCraftingRecipeDisplay> PACKET_CODEC;
	public static final RecipeDisplay.Serializer<TASCraftingRecipeDisplay> SERIALIZER;
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

	public RecipeDisplay.Serializer<TASCraftingRecipeDisplay> serializer() {
		return SERIALIZER;
	}

	public boolean isEnabled(FeatureSet features) {
		return this.ingredients
			.stream()
			.allMatch(ingredient -> ingredient.isEnabled(features)) &&
			RecipeDisplay.super.isEnabled(features);
	}

	static {
		PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, TASCraftingRecipeDisplay::width,
			PacketCodecs.VAR_INT, TASCraftingRecipeDisplay::height,
			SlotDisplay.PACKET_CODEC.collect(PacketCodecs.toList()), TASCraftingRecipeDisplay::ingredients,
			SlotDisplay.PACKET_CODEC, TASCraftingRecipeDisplay::result,
			SlotDisplay.PACKET_CODEC, TASCraftingRecipeDisplay::craftingStation,
			TASCraftingRecipeDisplay::new
		);

		SERIALIZER = new RecipeDisplay.Serializer<>(CODEC, PACKET_CODEC);
	}
}
