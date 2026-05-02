package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.display.TASCraftingRecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;

import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;

import java.util.List;

public class TASShapedRecipe extends BaseCraftingRecipe<CraftingRecipeInput> {

	public static final MapCodec<TASShapedRecipe> CODEC;
	public static final PacketCodec<RegistryByteBuf, TASShapedRecipe> PACKET_CODEC;

	public TASShapedRecipe(String group, ModCraftingRecipeCategory category, CustomShapedRecipe recipe, ItemStack result, boolean showNotification) {
		super(group, category, recipe, result, showNotification);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	public List<RecipeDisplay> getDisplays() {
		return List.of(new TASCraftingRecipeDisplay(
			this.recipe.getWidth(),
			this.recipe.getHeight(),
			this.recipe.getIngredients()
				.stream()
				.map((ingredient) ->
					ingredient.map(Ingredient::toDisplay)
						.orElse(SlotDisplay.EmptySlotDisplay.INSTANCE))
				.toList(),
			new SlotDisplay.StackSlotDisplay(this.result),
			new SlotDisplay.ItemSlotDisplay(ModItems.TURRET_ASSEMBLY_STATION)
		));
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	public RecipeType<? extends Recipe<CraftingRecipeInput>> getType() {
		return ModRecipeTypes.TAS_RECIPE_TYPE;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override
	public RecipeSerializer<? extends BaseCraftingRecipe<CraftingRecipeInput>> getSerializer() {
		return ModRecipeSerializers.TAS_SERIALIZER;
	}

	static {
		CODEC = BaseCraftingRecipe.createCodec(
			7, 7,
			ModCraftingRecipeCategory.CODEC,
			ModCraftingRecipeCategory.MISC,
			BaseCraftingRecipe::getCategory,
			TASShapedRecipe::new
		);

		PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.STRING, BaseCraftingRecipe::getGroup,
			ModCraftingRecipeCategory.PACKET_CODEC, BaseCraftingRecipe::getCategory, // *See note below
			CustomShapedRecipe.PACKET_CODEC, recipe -> recipe.recipe,
			ItemStack.PACKET_CODEC, recipe -> recipe.result,
			PacketCodecs.BOOLEAN, BaseCraftingRecipe::showNotification,
			TASShapedRecipe::new
		);
	}
}
