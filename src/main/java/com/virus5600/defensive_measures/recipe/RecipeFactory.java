package com.virus5600.defensive_measures.recipe;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface RecipeFactory<T extends BaseCraftingRecipe<?>, C> {
	T create(String group, C category, CustomShapedRecipe recipe, ItemStack result, boolean showNotification);
}
