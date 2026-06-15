package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.display.TASCraftingRecipeDisplay;

import com.google.common.annotations.VisibleForTesting;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class TASShapedRecipe extends BaseCraftingRecipe<CraftingInput> {
	public static final MapCodec<TASShapedRecipe> MAP_CODEC;
	public static final StreamCodec<RegistryFriendlyByteBuf, TASShapedRecipe> STREAM_CODEC;
	public static final RecipeSerializer<TASShapedRecipe> SERIALIZER;

	protected final CustomShapedRecipePattern pattern;
	protected final ItemStackTemplate result;

	public TASShapedRecipe(CommonInfo commonInfo, ModCraftingBookInfo bookInfo, CustomShapedRecipePattern pattern, ItemStackTemplate result) {
		super(commonInfo, bookInfo, pattern, result);

		this.pattern = pattern;
		this.result = result;
	}

	// /////// //
	// METHODS //
	// /////// //

	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.createFromOptionals(this.pattern.ingredients());
	}

	@VisibleForTesting
	public List<Optional<Ingredient>> getIngredients() {
		return this.pattern.ingredients();
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	public @NonNull List<RecipeDisplay> display() {
		return List.of(new TASCraftingRecipeDisplay(
			this.recipe.width(),
			this.recipe.height(),
			this.recipe.ingredients()
				.stream()
				.map((ingredient) ->
					ingredient.map(Ingredient::display)
						.orElse(SlotDisplay.Empty.INSTANCE))
				.toList(),
			new SlotDisplay.ItemStackSlotDisplay(this.result),
			new SlotDisplay.ItemSlotDisplay(ModItems.TURRET_ASSEMBLY_STATION)
		));
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected CustomShapedRecipePattern pattern() {
		return this.pattern;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override @NonNull
	public RecipeType<? extends Recipe<CraftingInput>> getType() {
		return ModRecipeTypes.TAS_RECIPE_TYPE;
	}

	@Override @NonNull
	public RecipeSerializer<? extends BaseCraftingRecipe<CraftingInput>> getSerializer() {
		return ModRecipeSerializers.TAS_SERIALIZER;
	}

	@NonNull
	public ItemStack assemble(@NonNull CraftingInput input) {
		return this.result.create();
	}

	// ////// //
	// STATIC //
	// ////// //

	static {
		MAP_CODEC = BaseCraftingRecipe.createMapCodec(
			7, 7,
			ModCraftingBookInfo.MAP_CODEC,
			TASShapedRecipe::new
		);

		STREAM_CODEC = StreamCodec.composite(
			CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
			ModCraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
			CustomShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
			ItemStackTemplate.STREAM_CODEC, (o) -> o.result, TASShapedRecipe::new
		);

		SERIALIZER = ModRecipeSerializers.TAS_SERIALIZER;
	}
}
