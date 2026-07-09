package com.virus5600.defensive_measures.recipebook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;

import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;

import java.util.List;

/**
 * An enumeration of custom recipe book categories for the mod.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Environment(EnvType.CLIENT)
public enum ModRecipeBookCategory implements ExtendedRecipeBookCategory {
	TURRET_ASSEMBLY_STATION(
		ModRecipeBookCategories.TAS_TURRETS,
		ModRecipeBookCategories.TAS_PARTS,
		ModRecipeBookCategories.TAS_TRAPS,
		ModRecipeBookCategories.TAS_DEFENSE,
		ModRecipeBookCategories.TAS_EQUIPMENT,
		ModRecipeBookCategories.TAS_MISC
	),
	WORKSHOP(
		ModRecipeBookCategories.WORKSHOP_TURRETS,
		ModRecipeBookCategories.WORKSHOP_PARTS,
		ModRecipeBookCategories.WORKSHOP_TRAPS,
		ModRecipeBookCategories.WORKSHOP_DEFENSE,
		ModRecipeBookCategories.WORKSHOP_EQUIPMENT,
		ModRecipeBookCategories.WORKSHOP_MISC
	),
	FABRICATION_MATRIX(
		ModRecipeBookCategories.FABMAT_TURRETS,
		ModRecipeBookCategories.FABMAT_PARTS,
		ModRecipeBookCategories.FABMAT_TRAPS,
		ModRecipeBookCategories.FABMAT_DEFENSE,
		ModRecipeBookCategories.FABMAT_EQUIPMENT,
		ModRecipeBookCategories.FABMAT_MISC
	)
	;

	private final List<RecipeBookCategory> includedCategories;

	ModRecipeBookCategory(final RecipeBookCategory... includedCategories) {
		this.includedCategories =  List.of(includedCategories);
	}

	public List<RecipeBookCategory> includedCategories() {
		return this.includedCategories;
	}
}
