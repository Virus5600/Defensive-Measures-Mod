package com.virus5600.defensive_measures.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * This class is responsible for registering all custom recipe serializers used in the mod. It
 * defines and registers serializers for both shaped and shapeless recipes, registered using the
 * {@link RegistryHelper} utility class, which handles the registration process with Minecraft's
 * registry system.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModRecipeSerializers {
	// v1.1.0-beta //

	// Turret Assembly Station
	public static final RecipeSerializer<TASShapedRecipe> TAS_SERIALIZER;
	public static final RecipeSerializer<TASShapelessRecipe> TAS_SHAPELESS_SERIALIZER;

	// v1.2.0-beta //

	// Workshop
	public static final RecipeSerializer<WorkshopShapedRecipe> WORKSHOP_SERIALIZER;
	public static final RecipeSerializer<WorkshopShapelessRecipe> WORKSHOP_SHAPELESS_SERIALIZER;

	// Fabrication Matrix
	public static final RecipeSerializer<FabMatShapedRecipe> FABMAT_SERIALIZER;
	public static final RecipeSerializer<FabMatShapelessRecipe> FABMAT_SHAPELESS_SERIALIZER;

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

		// FABRICATION MATRIX
		FABMAT_SERIALIZER = RegistryHelper.registerRecipeSerializer(
			"fabmat_shaped", BaseCraftingRecipe.createSerializer(
				FabMatShapedRecipe.MAP_CODEC,
				FabMatShapedRecipe.STREAM_CODEC
			)
		);

		FABMAT_SHAPELESS_SERIALIZER =  RegistryHelper.registerRecipeSerializer(
			"fabmat_shapeless", BaseCraftingRecipe.createSerializer(
				FabMatShapelessRecipe.MAP_CODEC,
				FabMatShapelessRecipe.STREAM_CODEC
			)
		);
	}
}
