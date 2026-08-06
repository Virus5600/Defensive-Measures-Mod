package com.virus5600.defensive_measures.gui.screen.book.overlay;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

import java.util.List;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModOverlaySmeltingRecipeButton extends BaseOverlayRecipeButton {
	private static final Identifier ENABLED_SPRITE = Identifier.withDefaultNamespace("recipe_book/furnace_overlay");
	private static final Identifier HIGHLIGHTED_ENABLED_SPRITE = Identifier.withDefaultNamespace("recipe_book/furnace_overlay_highlighted");
	private static final Identifier DISABLED_SPRITE = Identifier.withDefaultNamespace("recipe_book/furnace_overlay_disabled");
	private static final Identifier HIGHLIGHTED_DISABLED_SPRITE = Identifier.withDefaultNamespace("recipe_book/furnace_overlay_disabled_highlighted");

	public ModOverlaySmeltingRecipeButton(
		final int x, final int y,
		final RecipeDisplayId id, final RecipeDisplay recipe,
		final BaseOverlayRecipeComponent component,
		final ContextMap context, final boolean isCraftable
	) {
		super(
			x, y,
			id, recipe,
			component,
			context, isCraftable,
			1, 1
		);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected List<Pos> calculateIngredientsPositions(
		final RecipeDisplay recipe, final ContextMap context,
		final int gridWidth, final int gridHeight
	) {
		if (recipe instanceof FurnaceRecipeDisplay furnaceRecipe) {
			List<ItemStack> items = furnaceRecipe.ingredient().resolveForStacks(context);
			if (!items.isEmpty()) {
				return List.of(createGridPos(1, 1, items));
			}
		}

		return List.of();
	}

	protected Identifier getSprite(final boolean isCraftable) {
		return isCraftable ?
			(this.isHoveredOrFocused() ? this.getHighlightedEnabledSprite() : this.getEnabledSprite())
			: (this.isHoveredOrFocused() ? this.getHighlightedDisabledSprite() : this.getDisabledSprite());
	}

	protected int getBorderSize() {
		return 2;
	}

	protected int getSlotSize() {
		return 6;
	}

	// //////////////////// //
	// OVERLOADABLE METHODS //
	// //////////////////// //
	protected Identifier getEnabledSprite() {
		return ENABLED_SPRITE;
	}

	protected Identifier getHighlightedEnabledSprite() {
		return HIGHLIGHTED_ENABLED_SPRITE;
	}

	protected Identifier getDisabledSprite() {
		return DISABLED_SPRITE;
	}

	protected Identifier getHighlightedDisabledSprite() {
		return HIGHLIGHTED_DISABLED_SPRITE;
	}
}
