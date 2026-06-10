package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;

import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;
import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;

import org.jspecify.annotations.NonNull;

public interface BaseCraftingRecipeInterface<T extends RecipeInput> extends Recipe<T> {
	ModCraftingRecipeCategory category();

	@NonNull
	default RecipeBookCategory recipeBookCategory() {
		RecipeBookCategory category;
		switch (this.category()) {
			case TURRETS -> category = ModRecipeBookCategories.TAS_TURRETS;
			case PARTS -> category = ModRecipeBookCategories.TAS_PARTS;
			case TRAPS -> category = ModRecipeBookCategories.TAS_TRAPS;
			case DEFENSE -> category = ModRecipeBookCategories.TAS_DEFENSE;
			case EQUIPMENTS -> category = ModRecipeBookCategories.TAS_EQUIPMENTS;
			case MISC -> category = ModRecipeBookCategories.TAS_MISC;
			default -> throw new MatchException(null, null);
		}

		return category;
	}

	record ModCraftingBookInfo(ModCraftingRecipeCategory category, String group) implements Recipe.BookInfo<ModCraftingRecipeCategory> {
		public static final MapCodec<ModCraftingBookInfo> MAP_CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, ModCraftingBookInfo> STREAM_CODEC;

		static {
			MAP_CODEC = BookInfo.mapCodec(
				ModCraftingRecipeCategory.CODEC,
				ModCraftingRecipeCategory.MISC,
				ModCraftingBookInfo::new
			);

			STREAM_CODEC = BookInfo.streamCodec(
				ModCraftingRecipeCategory.STREAM_CODEC,
				ModCraftingBookInfo::new
			);
		}
	}
}
