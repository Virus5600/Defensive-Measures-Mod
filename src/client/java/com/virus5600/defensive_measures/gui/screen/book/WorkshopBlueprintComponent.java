package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;

import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.display.FlexibleShapedCraftingRecipeDisplay;
import com.virus5600.defensive_measures.screen.WorkshopScreenHandler;

public class WorkshopBlueprintComponent extends BaseBlueprintComponent {
	public WorkshopBlueprintComponent(WorkshopScreenHandler screenHandler) {
		super(screenHandler);
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
		return ModItems.WORKSHOP;
	}
}
