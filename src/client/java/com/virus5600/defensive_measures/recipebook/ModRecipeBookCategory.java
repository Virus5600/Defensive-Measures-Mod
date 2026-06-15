package com.virus5600.defensive_measures.recipebook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;

import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;

import java.util.List;

@Environment(EnvType.CLIENT)
public enum ModRecipeBookCategory implements ExtendedRecipeBookCategory {
	TURRET_ASSEMBLY_STATION(
		ModRecipeBookCategories.TAS_TURRETS,
		ModRecipeBookCategories.TAS_PARTS,
		ModRecipeBookCategories.TAS_TRAPS,
		ModRecipeBookCategories.TAS_DEFENSE,
		ModRecipeBookCategories.TAS_EQUIPMENTS,
		ModRecipeBookCategories.TAS_MISC
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
