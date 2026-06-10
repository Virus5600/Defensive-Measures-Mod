package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;
import com.virus5600.defensive_measures.recipe.display.TASCraftingRecipeDisplay;
import com.virus5600.defensive_measures.recipebook.ModRecipeBookType;

import java.util.List;

public class BlueprintWidget extends BaseRecipeBookWidget<AbstractCraftingMenu> {
	private static final WidgetSprites FILTER_BUTTON_TEXTURES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/filter_enabled"), Identifier.withDefaultNamespace("recipe_book/filter_disabled"), Identifier.withDefaultNamespace("recipe_book/filter_enabled_highlighted"), Identifier.withDefaultNamespace("recipe_book/filter_disabled_highlighted"));
	private static final Component TOGGLE_CRAFTABLE_TEXT = Component.translatable("gui.recipebook.toggleRecipes.craftable");
	private static final List<BaseRecipeBookWidget.Tab> TABS;

	public BlueprintWidget(AbstractCraftingMenu screenHandler) {
		super(screenHandler, TABS);
	}

	protected boolean isCraftingSlot(Slot slot) {
		return this.screenHandler.getResultSlot() == slot ||
			this.screenHandler.getInputGridSlots().contains(slot);
	}

	private boolean canDisplay(RecipeDisplay display) {
		int parentWidth = this.screenHandler.getGridWidth();
		int parentHeight = this.screenHandler.getGridHeight();
		boolean result;

		switch (display) {
			case TASCraftingRecipeDisplay tasCraftingRecipeDisplay -> result = parentWidth >= tasCraftingRecipeDisplay.width() && parentHeight >= tasCraftingRecipeDisplay.height();
			default -> result = false;
		}

		return result;
	}

	protected void showGhostRecipe(GhostSlots ghostRecipe, RecipeDisplay display, ContextMap context) {
		switch (display) {
			case TASCraftingRecipeDisplay tasCraftingRecipeDisplay -> {
				List<Slot> inputSlots = this.screenHandler.getInputGridSlots();
				List<SlotDisplay> ingredients = tasCraftingRecipeDisplay.ingredients();
				int recipeWidth = tasCraftingRecipeDisplay.width();
				int recipeHeight = tasCraftingRecipeDisplay.height();
				int gridWidth = this.screenHandler.getGridWidth();
				int gridHeight = this.screenHandler.getGridHeight();

				// Center the recipe in the grid
				int startX = (gridWidth - recipeWidth) / 2;
				int startY = (gridHeight - recipeHeight) / 2;

				// Place each ingredient at its correct grid position, centered
				for (int i = 0; i < ingredients.size(); i++) {
					int recipeX = i % recipeWidth;
					int recipeY = i / recipeWidth;

					int actualX = startX + recipeX;
					int actualY = startY + recipeY;
					int slotIndex = actualY * gridWidth + actualX;

					if (slotIndex < inputSlots.size()) {
						ghostRecipe.setInput(inputSlots.get(slotIndex), context, ingredients.get(i));
					}
				}

				ghostRecipe.setResult(this.screenHandler.getResultSlot(), context, tasCraftingRecipeDisplay.result());
			}

			default -> {
			}
		}
	}

	protected WidgetSprites getBookButtonTextures() {
		return FILTER_BUTTON_TEXTURES;
	}

	protected Component getToggleCraftableButtonText() {
		return TOGGLE_CRAFTABLE_TEXT;
	}

	protected void populateRecipes(RecipeCollection recipeResultCollection, StackedItemContents recipeFinder) {
		recipeResultCollection.selectRecipes(recipeFinder, this::canDisplay);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	protected int getLeftOffset() {
		return 106;
	}

	static {
		TABS = List.of(
			new BaseRecipeBookWidget.Tab(ModRecipeBookType.TURRET_ASSEMBLY_STATION),
			new BaseRecipeBookWidget.Tab(ModItems.CANNON_TURRET, ModRecipeBookCategories.TAS_TURRETS),
			new BaseRecipeBookWidget.Tab(ModItems.BALLISTA_BOLT, ModItems.MG_HEAD, ModRecipeBookCategories.TAS_PARTS),
			new BaseRecipeBookWidget.Tab(ModItems.ELECTRIC_FENCE, ModRecipeBookCategories.TAS_TRAPS),
			new BaseRecipeBookWidget.Tab(Items.OAK_FENCE, ModRecipeBookCategories.TAS_DEFENSE),
			new BaseRecipeBookWidget.Tab(ModItems.TURRET_REMOVER, ModRecipeBookCategories.TAS_EQUIPMENTS),
			new BaseRecipeBookWidget.Tab(Items.COPPER_INGOT, ModRecipeBookCategories.TAS_MISC)
		);
	}
}
