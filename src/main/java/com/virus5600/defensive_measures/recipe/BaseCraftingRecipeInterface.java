package com.virus5600.defensive_measures.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;

import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;
import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;
import org.jspecify.annotations.NonNull;

public interface BaseCraftingRecipeInterface<T extends RecipeInput> extends Recipe<T> {
	ModCraftingRecipeCategory getCategory();

	default NonNullList<ItemStack> getRecipeRemainders(T input) {
		return collectRecipeRemainders(input);
	}

	static NonNullList<ItemStack> collectRecipeRemainders(RecipeInput input) {
		NonNullList<ItemStack> defaultedList = NonNullList.withSize(input.size(), ItemStack.EMPTY);

		for(int i = 0; i < defaultedList.size(); ++i) {
			Item item = input.getItem(i).getItem();
			defaultedList.set(i, item.getCraftingRemainder());
		}

		return defaultedList;
	}

	@NonNull
	default RecipeBookCategory recipeBookCategory() {
		RecipeBookCategory category;
		switch (this.getCategory()) {
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
}
