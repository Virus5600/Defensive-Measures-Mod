package com.virus5600.defensive_measures.recipe.book;

import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * Derives from the {@link RecipeBookCategories}
 */
public class ModRecipeBookCategories {
	// Crafting Table
	public static final RecipeBookCategory DM_TURRETS = RegistryHelper.registerRecipeBookCat("dm_turrets");

	// Turret Assembly Station
	public static final RecipeBookCategory TAS_TURRETS = RegistryHelper.registerRecipeBookCat("tas_turrets");
	public static final RecipeBookCategory TAS_PARTS = RegistryHelper.registerRecipeBookCat("tas_parts");
	public static final RecipeBookCategory TAS_TRAPS = RegistryHelper.registerRecipeBookCat("tas_traps");
	public static final RecipeBookCategory TAS_DEFENSE = RegistryHelper.registerRecipeBookCat("tas_defense");
	public static final RecipeBookCategory TAS_EQUIPMENT = RegistryHelper.registerRecipeBookCat("tas_equipment");
	public static final RecipeBookCategory TAS_MISC = RegistryHelper.registerRecipeBookCat("tas_misc");

	// Workshop
	public static final RecipeBookCategory WORKSHOP_TURRETS = RegistryHelper.registerRecipeBookCat("workshop_turrets");
	public static final RecipeBookCategory WORKSHOP_PARTS = RegistryHelper.registerRecipeBookCat("workshop_parts");
	public static final RecipeBookCategory WORKSHOP_TRAPS = RegistryHelper.registerRecipeBookCat("workshop_traps");
	public static final RecipeBookCategory WORKSHOP_DEFENSE = RegistryHelper.registerRecipeBookCat("workshop_defense");
	public static final RecipeBookCategory WORKSHOP_EQUIPMENT = RegistryHelper.registerRecipeBookCat("workshop_equipment");
	public static final RecipeBookCategory WORKSHOP_MISC = RegistryHelper.registerRecipeBookCat("workshop_misc");

	// Fabrication Matrix
	public static final RecipeBookCategory FABMAT_TURRETS = RegistryHelper.registerRecipeBookCat("fabmat_turrets");
	public static final RecipeBookCategory FABMAT_PARTS = RegistryHelper.registerRecipeBookCat("fabmat_parts");
	public static final RecipeBookCategory FABMAT_TRAPS = RegistryHelper.registerRecipeBookCat("fabmat_traps");
	public static final RecipeBookCategory FABMAT_DEFENSE = RegistryHelper.registerRecipeBookCat("fabmat_defense");
	public static final RecipeBookCategory FABMAT_EQUIPMENT = RegistryHelper.registerRecipeBookCat("fabmat_equipment");
	public static final RecipeBookCategory FABMAT_MISC = RegistryHelper.registerRecipeBookCat("fabmat_misc");

	public static void registerBookCategories() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE BOOK CATEGORIES (BLUEPRINT CATEGORIES) FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
