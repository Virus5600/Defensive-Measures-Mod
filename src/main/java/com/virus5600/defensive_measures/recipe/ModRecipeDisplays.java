package com.virus5600.defensive_measures.recipe;

import net.minecraft.world.item.crafting.display.RecipeDisplay;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.recipe.display.TASCraftingRecipeDisplay;

public final class ModRecipeDisplays {
	public static final RecipeDisplay.Type<TASCraftingRecipeDisplay> TAS_CRAFTING_DISPLAY;

	public static void registerModRecipeDisplays() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE DISPLAYS FOR {}...", DefensiveMeasures.MOD_NAME);
	}

	static {
		TAS_CRAFTING_DISPLAY = RegistryHelper.registerRecipeDisplay(
			"tas_crafting",
			TASCraftingRecipeDisplay.SERIALIZER
		);
	}
}
