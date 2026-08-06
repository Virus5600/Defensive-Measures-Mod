package com.virus5600.defensive_measures.gui.screen.book.overlay;

import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

import com.virus5600.defensive_measures.gui.screen.ingame.TurretAssemblyStationScreen;

/**
 * The custom blueprint component overlay for {@link TurretAssemblyStationScreen}, allowing
 * grouped recipes to show an overlay, showing all the recipes for the group.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class TASOverlayRecipeComponent extends BaseOverlayRecipeComponent {
	public TASOverlayRecipeComponent(final SlotSelectTime slotSelectTime, final boolean isFurnaceMenu) {
		super(slotSelectTime, isFurnaceMenu);
	}

	// ////////////////// //
	// OVERRIDDEN METHODS //
	// ////////////////// //

	protected int getMaxRow() {
		return 2;
	}

	protected int getMaxRowLarge() {
		return 3;
	}

	protected int getButtonSize() {
		int borderSize = ModOverlayCraftingRecipeButton.SPRITE_BORDER_SIZE;
		int slotSize = ModOverlayCraftingRecipeButton.SPRITE_SLOT_SIZE;

		// (6px slot size * 7 row/col) + (2px border size * 2 opposing sides) + (7 row/col - 1 grid line)
		return (slotSize * 7) + (borderSize * 2) + (7 - 1);
	}

	protected TASOverlayCraftingRecipeButton getCraftingOverlayRecipeButton(
		final int x, final int y,
		final RecipeDisplayId id, final RecipeDisplay recipe,
		final ContextMap context, final boolean isCraftable
	) {
		return new TASOverlayCraftingRecipeButton(
			x, y,
			id, recipe,
			this, context, isCraftable
		);
	}
}
