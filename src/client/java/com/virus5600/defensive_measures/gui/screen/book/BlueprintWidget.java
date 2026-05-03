package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;
import com.virus5600.defensive_measures.recipe.display.TASCraftingRecipeDisplay;
import com.virus5600.defensive_measures.recipebook.ModRecipeBookType;

import java.util.List;

public class BlueprintWidget extends BaseRecipeBookWidget<AbstractCraftingScreenHandler> {
	private static final ButtonTextures FILTER_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/filter_enabled"), Identifier.ofVanilla("recipe_book/filter_disabled"), Identifier.ofVanilla("recipe_book/filter_enabled_highlighted"), Identifier.ofVanilla("recipe_book/filter_disabled_highlighted"));
	private static final Text TOGGLE_CRAFTABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.craftable");
	private static final List<BaseRecipeBookWidget.Tab> TABS;

	public BlueprintWidget(AbstractCraftingScreenHandler screenHandler) {
		super(screenHandler, TABS);
	}

	protected boolean isCraftingSlot(Slot slot) {
		return this.screenHandler.getOutputSlot() == slot ||
			this.screenHandler.getInputSlots().contains(slot);
	}

	private boolean canDisplay(RecipeDisplay display) {
		int parentWidth = this.screenHandler.getWidth();
		int parentHeight = this.screenHandler.getHeight();
		boolean result;

		switch (display) {
			case TASCraftingRecipeDisplay tasCraftingRecipeDisplay -> result = parentWidth >= tasCraftingRecipeDisplay.width() && parentHeight >= tasCraftingRecipeDisplay.height();
			default -> result = false;
		}

		return result;
	}

	protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context) {
		switch (display) {
			case TASCraftingRecipeDisplay tasCraftingRecipeDisplay -> {
				List<Slot> inputSlots = this.screenHandler.getInputSlots();
				List<SlotDisplay> ingredients = tasCraftingRecipeDisplay.ingredients();
				int recipeWidth = tasCraftingRecipeDisplay.width();
				int recipeHeight = tasCraftingRecipeDisplay.height();
				int gridWidth = this.screenHandler.getWidth();
				int gridHeight = this.screenHandler.getHeight();

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
						ghostRecipe.addInputs(inputSlots.get(slotIndex), context, ingredients.get(i));
					}
				}

				ghostRecipe.addResults(this.screenHandler.getOutputSlot(), context, tasCraftingRecipeDisplay.result());
			}

			default -> {
			}
		}
	}

	protected ButtonTextures getBookButtonTextures() {
		return FILTER_BUTTON_TEXTURES;
	}

	protected Text getToggleCraftableButtonText() {
		return TOGGLE_CRAFTABLE_TEXT;
	}

	protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder) {
		recipeResultCollection.populateRecipes(recipeFinder, this::canDisplay);
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
			new BaseRecipeBookWidget.Tab(ModItems.BOLT_HEAD, ModRecipeBookCategories.TAS_TRAPS),
			new BaseRecipeBookWidget.Tab(Items.OAK_FENCE, ModRecipeBookCategories.TAS_DEFENSE),
			new BaseRecipeBookWidget.Tab(ModItems.TURRET_REMOVER, ModRecipeBookCategories.TAS_EQUIPMENTS),
			new BaseRecipeBookWidget.Tab(Items.COPPER_INGOT, ModRecipeBookCategories.TAS_MISC)
		);
	}
}
