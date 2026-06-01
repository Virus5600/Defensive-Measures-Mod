package com.virus5600.defensive_measures.recipe;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.recipe.display.TASCraftingRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModRecipeDisplays {
	public static final RecipeDisplay.Serializer<TASCraftingRecipeDisplay> TAS_CRAFTING_DISPLAY;

	public static void registerModRecipeDisplays() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE DISPLAYS FOR {}...", DefensiveMeasures.MOD_NAME);
	}

	static {
		TAS_CRAFTING_DISPLAY = Registry.register(
			Registries.RECIPE_DISPLAY,
			Identifier.of(DefensiveMeasures.MOD_ID, "tas_crafting"),
			TASCraftingRecipeDisplay.SERIALIZER
		);
	}
}


