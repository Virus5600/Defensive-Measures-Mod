package com.virus5600.defensive_measures.recipe;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe.BookInfo;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;

@FunctionalInterface
public interface RecipeFactory<
	T extends BaseCraftingRecipe<?>,
	C extends StringRepresentable,
	D extends BookInfo<C>
> {
	T create(
		CommonInfo commonInfo,
	    D bookInfo,
		CustomShapedRecipePattern pattern,
		ItemStackTemplate result
	);
}
