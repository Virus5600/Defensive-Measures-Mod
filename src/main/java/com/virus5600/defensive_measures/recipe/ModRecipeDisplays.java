package com.virus5600.defensive_measures.recipe;

import net.minecraft.world.item.crafting.display.RecipeDisplay;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.recipe.display.FlexibleShapedCraftingRecipeDisplay;

/**
 * This class is responsible for registering and managing custom recipe displays for the mod. It
 * provides a centralized location for defining and initializing recipe display types, ensuring
 * that they are properly registered with the game's registry system.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
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
