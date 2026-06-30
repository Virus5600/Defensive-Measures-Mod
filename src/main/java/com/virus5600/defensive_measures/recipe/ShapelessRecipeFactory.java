package com.virus5600.defensive_measures.recipe;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.BookInfo;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;

import java.util.List;

@FunctionalInterface
public interface ShapelessRecipeFactory<
	T extends BaseCraftingRecipe<?>,
	C extends StringRepresentable,
	D extends BookInfo<C>
> {
	T create(
		CommonInfo commonInfo,
		D bookInfo,
		List<Ingredient> ingredients,
		ItemStackTemplate result
	);
}
