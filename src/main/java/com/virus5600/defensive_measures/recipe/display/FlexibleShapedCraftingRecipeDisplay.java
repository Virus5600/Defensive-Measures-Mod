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

public record FlexibleShapedCraftingRecipeDisplay(
	int width, int height,
	List<SlotDisplay> ingredients, SlotDisplay result,
	SlotDisplay craftingStation
) implements RecipeDisplay {
	public static final StreamCodec<RegistryFriendlyByteBuf, FlexibleShapedCraftingRecipeDisplay> STREAM_CODEC;
	public static final RecipeDisplay.Type<FlexibleShapedCraftingRecipeDisplay> TYPE;
	public static final MapCodec<FlexibleShapedCraftingRecipeDisplay> MAP_CODEC;

	public FlexibleShapedCraftingRecipeDisplay {
		if (ingredients.size() != width * height) {
			throw new IllegalArgumentException("Invalid shaped recipe display contents");
		}
	}

	@NonNull
	public Type<FlexibleShapedCraftingRecipeDisplay> type() {
		return TYPE;
	}

	public boolean isEnabled(@NonNull FeatureFlagSet features) {
		return this.ingredients
			.stream()
			.allMatch(slotDisplay -> slotDisplay.isEnabled(features)) &&
			RecipeDisplay.super.isEnabled(features);
	}

	static {
		MAP_CODEC = RecordCodecBuilder.mapCodec(
			(instance) -> instance.group(
				Codec.INT.fieldOf("width").forGetter(FlexibleShapedCraftingRecipeDisplay::width),
				Codec.INT.fieldOf("height").forGetter(FlexibleShapedCraftingRecipeDisplay::height),
				SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(FlexibleShapedCraftingRecipeDisplay::ingredients),
				SlotDisplay.CODEC.fieldOf("result").forGetter(FlexibleShapedCraftingRecipeDisplay::result),
				SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FlexibleShapedCraftingRecipeDisplay::craftingStation)
			).apply(instance, FlexibleShapedCraftingRecipeDisplay::new)
		);

		STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, FlexibleShapedCraftingRecipeDisplay::width,
			ByteBufCodecs.VAR_INT, FlexibleShapedCraftingRecipeDisplay::height,
			SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), FlexibleShapedCraftingRecipeDisplay::ingredients,
			SlotDisplay.STREAM_CODEC, FlexibleShapedCraftingRecipeDisplay::result,
			SlotDisplay.STREAM_CODEC, FlexibleShapedCraftingRecipeDisplay::craftingStation,
			FlexibleShapedCraftingRecipeDisplay::new
		);

		TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);
	}
}
