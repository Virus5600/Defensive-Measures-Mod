package com.virus5600.defensive_measures.recipe.book;

import net.minecraft.recipebook.PlaceRecipeHelper;

import java.util.Iterator;

/**
 * A custom place recipe helper for handling recipe placement logic.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface ModPlaceRecipeHelper extends PlaceRecipeHelper {
	static <T> void placeRecipeCentered(final int gridWidth, final int gridHeight, final int recipeWidth, final int recipeHeight, final Iterable<T> entries, final Output<T> output) {
		Iterator<T> iterator = entries.iterator();
		int startX = (gridWidth - recipeWidth) / 2;
		int startY = (gridHeight - recipeHeight) / 2;

		for (int row = 0; row < recipeHeight; ++row) {
			for (int col = 0; col < recipeWidth; ++col) {
				int gridX = startX + col;
				int gridY = startY + row;

				// Convert to the 1D slot index
				int gridIndex = gridY * gridWidth + gridX;

				// Grab your ingredient and put it in gridIndex!
				output.addItemToSlot(iterator.next(), gridIndex, gridX, gridY);
			}
		}
	}
}
