package com.virus5600.defensive_measures.gui.screen.book.overlay;

import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.*;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.recipe.book.ModPlaceRecipeHelper;
import com.virus5600.defensive_measures.recipe.display.FlexibleShapedCraftingRecipeDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModOverlayCraftingRecipeButton extends BaseOverlayRecipeButton {
	private static final Identifier ENABLED_SPRITE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "blueprint/overlay/crafting_overlay");
	private static final Identifier HIGHLIGHTED_ENABLED_SPRITE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "blueprint/overlay/crafting_overlay_highlighted");
	private static final Identifier DISABLED_SPRITE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "blueprint/overlay/crafting_overlay_disabled");
	private static final Identifier HIGHLIGHTED_DISABLED_SPRITE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "blueprint/overlay/crafting_overlay_disabled_highlighted");
	private static final int GRID_WIDTH = 3;
	private static final int GRID_HEIGHT = 3;
	public static final int SPRITE_BORDER_SIZE = 2;
	public static final int SPRITE_SLOT_SIZE = 6;

	public ModOverlayCraftingRecipeButton(
		final int x, final int y,
		final RecipeDisplayId id, final RecipeDisplay recipe,
		final BaseOverlayRecipeComponent component,
		final ContextMap context, final boolean isCraftable,
		final int gridWidth, final int gridHeight
	) {
		super(
			x, y,
			id, recipe,
			component,
			context, isCraftable,
			gridWidth, gridHeight
		);
	}

	public ModOverlayCraftingRecipeButton(
		final int x, final int y,
		final RecipeDisplayId id, final RecipeDisplay recipe,
		final BaseOverlayRecipeComponent component,
		final ContextMap context, final boolean isCraftable
	) {
		this(
			x, y,
			id, recipe,
			component,
			context, isCraftable,
			GRID_WIDTH, GRID_HEIGHT
		);
	}

	// /////////////////////////// //
	// OVERRIDDEN ABSTRACT METHODS //
	// /////////////////////////// //

	protected List<Pos> calculateIngredientsPositions(
		final RecipeDisplay recipe, final ContextMap context,
		final int gridWidth, final int gridHeight
	) {
		List<BaseOverlayRecipeButton.Pos> result = new ArrayList<>();

		switch (recipe) {
			case ShapedCraftingRecipeDisplay shaped ->
				PlaceRecipeHelper.placeRecipe(
					gridWidth, gridHeight,
					shaped.width(), shaped.height(),
					shaped.ingredients(),
					(ingredient, _, gridXPos, gridYPos) -> {
						List<ItemStack> items = ingredient.resolveForStacks(context);

						if (!items.isEmpty()) {
							result.add(
								this.createGridPos(
									gridXPos,
									gridYPos,
									items
								)
							);
						}
					});

			case ShapelessCraftingRecipeDisplay shapeless -> {
				List<SlotDisplay> ingredients = shapeless.ingredients();

				for (int i = 0; i < ingredients.size(); i++) {
					List<ItemStack> items = (ingredients.get(i)).resolveForStacks(context);

					if (!items.isEmpty()) {
						result.add(
							this.createGridPos(
								i % gridWidth,
								i / gridHeight,
								items
							)
						);
					}
				}
			}

			default -> {
				if (recipe instanceof FlexibleShapedCraftingRecipeDisplay flexible) {
					ModPlaceRecipeHelper.placeRecipeCentered(
						gridWidth, gridHeight,
						flexible.width(), flexible.height(), flexible.ingredients(),
						(ingredient, _, gridXPos, gridYPos) -> {
							List<ItemStack> items = ingredient.resolveForStacks(context);

							if (!items.isEmpty()) {
								result.add(
									this.createGridPos(
										gridXPos,
										gridYPos,
										items
									)
								);
							}
						}
					);
				}
			}
		}

		return result;
	}

	protected Identifier getSprite(final boolean isCraftable) {
		return isCraftable ?
			(this.isHoveredOrFocused() ? this.getHighlightedEnabledSprite() : this.getEnabledSprite())
			: (this.isHoveredOrFocused() ? this.getHighlightedDisabledSprite() : this.getDisabledSprite());
	}

	protected int getBorderSize() {
		return SPRITE_BORDER_SIZE;
	}

	protected int getSlotSize() {
		return SPRITE_SLOT_SIZE;
	}

	// ////////////////// //
	// OVERRIDDEN METHODS //
	// ////////////////// //
	protected int getGridWidth() {
		return GRID_WIDTH;
	}

	protected int getGridHeight() {
		return GRID_HEIGHT;
	}

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
