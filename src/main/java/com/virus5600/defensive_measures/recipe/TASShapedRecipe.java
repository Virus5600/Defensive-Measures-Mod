package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;
import com.virus5600.defensive_measures.recipe.display.TASCraftingRecipeDisplay;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class TASShapedRecipe extends BaseCraftingRecipe<CraftingInput> {

	public static final MapCodec<TASShapedRecipe> CODEC;
	public static final StreamCodec<RegistryFriendlyByteBuf, TASShapedRecipe> PACKET_CODEC;

	public TASShapedRecipe(String group, ModCraftingRecipeCategory category, CustomShapedRecipe recipe, ItemStack result, boolean showNotification) {
		super(group, category, recipe, result, showNotification);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	public @NonNull List<RecipeDisplay> display() {
		return List.of(new TASCraftingRecipeDisplay(
			this.recipe.getWidth(),
			this.recipe.getHeight(),
			this.recipe.getIngredients()
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

	@Override @NonNull
	public RecipeType<? extends Recipe<CraftingInput>> getType() {
		return ModRecipeTypes.TAS_RECIPE_TYPE;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override @NonNull
	public RecipeSerializer<? extends BaseCraftingRecipe<CraftingInput>> getSerializer() {
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

		PACKET_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, BaseCraftingRecipe::group,
			ModCraftingRecipeCategory.PACKET_CODEC, BaseCraftingRecipe::getCategory,
			CustomShapedRecipe.PACKET_CODEC, recipe -> recipe.recipe,
			ItemStack.STREAM_CODEC, recipe -> recipe.result,
			ByteBufCodecs.BOOL, BaseCraftingRecipe::showNotification,
			TASShapedRecipe::new
		);
	}
}
