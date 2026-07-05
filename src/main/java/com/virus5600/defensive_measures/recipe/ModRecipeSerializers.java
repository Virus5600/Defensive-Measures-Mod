package com.virus5600.defensive_measures.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

public class ModRecipeSerializers {
	// Turret Assembly Station
	public static final RecipeSerializer<TASShapedRecipe> TAS_SERIALIZER;
	public static final RecipeSerializer<TASShapelessRecipe> TAS_SHAPELESS_SERIALIZER;

	// Workshop
	public static final RecipeSerializer<WorkshopShapedRecipe> WORKSHOP_SERIALIZER;
	public static final RecipeSerializer<WorkshopShapelessRecipe> WORKSHOP_SHAPELESS_SERIALIZER;

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING RECIPE SERIALIZERS FOR {}...", DefensiveMeasures.MOD_NAME);
	}

	static {
		// TURRET ASSEMBLY STATION
		TAS_SERIALIZER = RegistryHelper.registerRecipeSerializer(
			"tas_shaped", BaseCraftingRecipe.createSerializer(
				TASShapedRecipe.MAP_CODEC,
				TASShapedRecipe.STREAM_CODEC
			)
		);

		TAS_SHAPELESS_SERIALIZER =  RegistryHelper.registerRecipeSerializer(
			"tas_shapeless", BaseCraftingRecipe.createSerializer(
				TASShapelessRecipe.MAP_CODEC,
				TASShapelessRecipe.STREAM_CODEC
			)
		);

		// WORKSHOP
		WORKSHOP_SERIALIZER = RegistryHelper.registerRecipeSerializer(
			"workshop_shaped", BaseCraftingRecipe.createSerializer(
				WorkshopShapedRecipe.MAP_CODEC,
				WorkshopShapedRecipe.STREAM_CODEC
			)
		);

		WORKSHOP_SHAPELESS_SERIALIZER =  RegistryHelper.registerRecipeSerializer(
			"workshop_shapeless", BaseCraftingRecipe.createSerializer(
				WorkshopShapelessRecipe.MAP_CODEC,
				WorkshopShapelessRecipe.STREAM_CODEC
			)
		);
	}
}
