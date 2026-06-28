package com.virus5600.defensive_measures.recipe.book;

import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * Derives from the {@link RecipeBookCategories}
 */
public class ModRecipeBookCategories {
	public static final RecipeBookCategory DM_TURRETS = RegistryHelper.registerRecipeBookCat("dm_turrets");
	public static final RecipeBookCategory TAS_TURRETS = RegistryHelper.registerRecipeBookCat("tas_turrets");
	public static final RecipeBookCategory TAS_PARTS = RegistryHelper.registerRecipeBookCat("tas_parts");
	public static final RecipeBookCategory TAS_TRAPS = RegistryHelper.registerRecipeBookCat("tas_traps");
	public static final RecipeBookCategory TAS_DEFENSE = RegistryHelper.registerRecipeBookCat("tas_defense");
	public static final RecipeBookCategory TAS_EQUIPMENT = RegistryHelper.registerRecipeBookCat("tas_equipments");
	public static final RecipeBookCategory TAS_MISC = RegistryHelper.registerRecipeBookCat("tas_misc");

	public static void registerBookCategories() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE BOOK CATEGORIES (BLUEPRINT CATEGORIES) FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
