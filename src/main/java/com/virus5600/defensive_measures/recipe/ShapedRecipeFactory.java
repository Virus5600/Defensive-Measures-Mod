package com.virus5600.defensive_measures.recipe;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe.BookInfo;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;

/**
 * A functional interface for creating shaped crafting recipes with custom patterns and book
 * information. This interface allows for the creation of shaped recipes that can be registered in
 * the game, providing a flexible way to define crafting mechanics.
 *
 * @param <T> the type of crafting recipe to be created, which must extend {@link BaseCraftingRecipe}
 * @param <C> the type of string representable used for the book information, which must extend {@link StringRepresentable}
 * @param <D> the type of book information used for the recipe, which must extend {@link BookInfo BookInfo}
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@FunctionalInterface
public interface ShapedRecipeFactory<
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
