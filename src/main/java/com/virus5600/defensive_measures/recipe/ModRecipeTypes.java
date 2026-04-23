package com.virus5600.defensive_measures.recipe;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import net.minecraft.recipe.RecipeType;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class ModRecipeTypes {
	public static final RecipeType<TASShapedRecipe> TAS_RECIPE_TYPE = RegistryHelper.registerRecipeType("tas_recipe_type");

	public static void registerModRecipesTypes() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE TYPES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
