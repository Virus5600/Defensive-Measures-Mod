package com.virus5600.defensive_measures.recipe;

import net.minecraft.recipe.book.RecipeBookType;

/**
 * This enum replaces the vanilla {@link RecipeBookType} for all custom crafting stations added by
 * the mod. It allows for better organization and potential future expansion of custom recipe book
 * types without modifying vanilla code.
 */
public enum HelperUIType {
	TURRET_ASSEMBLY_STATION;

	private HelperUIType() {}
}
