package com.virus5600.defensive_measures.gui.screen.book.overlay;

import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

/**
 * The custom overlay recipe button for {@link WorkshopOverlayRecipeComponent}, allowing
 * the grouped recipes shown from the overlay to be interacted with and provide the
 * recipe's pattern.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class WorkshopOverlayCraftingRecipeButton extends ModOverlayCraftingRecipeButton {
	public static final int GRID_WIDTH = 9;
	public static final int GRID_HEIGHT = 9;

	public WorkshopOverlayCraftingRecipeButton(
		final int x, final int y,
		final RecipeDisplayId id, final RecipeDisplay recipe,
		final BaseOverlayRecipeComponent component,
		final ContextMap context, final boolean isCraftable
	) {
		super(
			x, y,
			id, recipe,
			component, context, isCraftable,
			GRID_WIDTH, GRID_HEIGHT
		);
	}

	// ////////////////// //
	// OVERRIDDEN METHODS //
	// ////////////////// //

	@Override
	protected int getGridWidth() {
		return GRID_WIDTH;
	}

	@Override
	protected int getGridHeight() {
		return GRID_HEIGHT;
	}
}
