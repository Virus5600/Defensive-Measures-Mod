package com.virus5600.defensive_measures.recipe;

import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBookCategory;

import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;

public interface BaseCraftingRecipeInterface<T extends RecipeInput> extends Recipe<T> {
	ModCraftingRecipeCategory getCategory();

	default DefaultedList<ItemStack> getRecipeRemainders(T input) {
		return collectRecipeRemainders(input);
	}

	static DefaultedList<ItemStack> collectRecipeRemainders(RecipeInput input) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

		for(int i = 0; i < defaultedList.size(); ++i) {
			Item item = input.getStackInSlot(i).getItem();
			defaultedList.set(i, item.getRecipeRemainder());
		}

		return defaultedList;
	}

	default RecipeBookCategory getRecipeBookCategory() {
		RecipeBookCategory category;
		switch (this.getCategory()) {
			case TURRETS -> category = ModRecipeBookCategories.TAS_TURRETS;
			case PARTS -> category = ModRecipeBookCategories.TAS_PARTS;
			case TRAPS -> category = ModRecipeBookCategories.TAS_TRAPS;
			case EQUIPMENTS -> category = ModRecipeBookCategories.TAS_EQUIPMENTS;
			case MISC -> category = ModRecipeBookCategories.TAS_MISC;
			default -> throw new MatchException(null, null);
		}

		return category;
	}
}
