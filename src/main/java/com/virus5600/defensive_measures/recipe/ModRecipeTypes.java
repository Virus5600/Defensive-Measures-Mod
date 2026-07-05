package com.virus5600.defensive_measures.recipe;

import net.minecraft.world.item.crafting.RecipeType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

public class ModRecipeTypes {
	public static final RecipeType<TASShapedRecipe> TAS_RECIPE_TYPE = RegistryHelper.registerRecipeType("tas_recipe_type");
	public static final RecipeType<WorkshopShapedRecipe> WORKSHOP_RECIPE_TYPE = RegistryHelper.registerRecipeType("workshop_recipe_type");

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE TYPES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
