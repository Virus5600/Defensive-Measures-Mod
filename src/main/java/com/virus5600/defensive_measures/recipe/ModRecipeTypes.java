package com.virus5600.defensive_measures.recipe;

import net.minecraft.world.item.crafting.RecipeType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * This class is responsible for registering custom recipe types for the mod. These recipe types
 * are registered using the {@link RegistryHelper} utility class.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModRecipeTypes {
	// v1.1.0-beta
	public static final RecipeType<TASShapedRecipe> TAS_RECIPE_TYPE = RegistryHelper.registerRecipeType("tas_recipe_type");
	// v1.2.0-beta
	public static final RecipeType<WorkshopShapedRecipe> WORKSHOP_RECIPE_TYPE = RegistryHelper.registerRecipeType("workshop_recipe_type");
	public static final RecipeType<FabMatShapedRecipe> FABRICATION_MATRIX_RECIPE_TYPE = RegistryHelper.registerRecipeType("fabrication_matrix_recipe_type");

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE TYPES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
