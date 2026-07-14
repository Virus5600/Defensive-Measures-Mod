package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;

import com.virus5600.defensive_measures.gui.screen.ingame.FabricationMatrixScreen;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.display.FlexibleShapedCraftingRecipeDisplay;
import com.virus5600.defensive_measures.screen.FabricationMatrixScreenHandler;

/**
 * The custom blueprint component used by the {@link FabricationMatrixScreen}.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class FabMatBlueprintComponent extends BaseBlueprintComponent {
	public FabMatBlueprintComponent(FabricationMatrixScreenHandler screenHandler) {
		super(screenHandler);
	}

	// ////////////////// //
	// OVERRIDDEN METHODS //
	// ////////////////// //
	protected int getXOffset() {
		return 146;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	// BaseBlueprintComponent
	protected void fillGhostShapedRecipe(
		FlexibleShapedCraftingRecipeDisplay display,
		GhostSlots ghostSlots, ContextMap ctx
	) {
		BaseBlueprintComponent.defaultFillGhostShaped(
			display, ghostSlots,
			ctx, this.menu
		);
	}

	@Override
	public Item getItemForSlotDisplay() {
		return ModItems.FABRICATION_MATRIX;
	}
}
