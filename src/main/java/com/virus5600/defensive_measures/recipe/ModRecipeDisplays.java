package com.virus5600.defensive_measures.recipe;

import net.minecraft.world.item.crafting.display.RecipeDisplay;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.recipe.display.FlexibleShapedCraftingRecipeDisplay;

public final class ModRecipeDisplays {
	public static final RecipeDisplay.Type<FlexibleShapedCraftingRecipeDisplay> FLEXIBLE_SHAPED_DISPLAY;

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE DISPLAYS FOR {}...", DefensiveMeasures.MOD_NAME);
	}

	static {
		FLEXIBLE_SHAPED_DISPLAY = RegistryHelper.registerRecipeDisplay(
			"flexible_shaped",
			FlexibleShapedCraftingRecipeDisplay.TYPE
		);
	}
}
