package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
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
import com.virus5600.defensive_measures.recipebook.ModRecipeBookCategory;

import java.util.List;
import java.util.Objects;

public class BlueprintComponent extends RecipeBookComponent<AbstractCraftingMenu> {
	private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/filter_enabled"), Identifier.withDefaultNamespace("recipe_book/filter_disabled"), Identifier.withDefaultNamespace("recipe_book/filter_enabled_highlighted"), Identifier.withDefaultNamespace("recipe_book/filter_disabled_highlighted"));
	private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
	private static final List<TabInfo> TABS;

	public BlueprintComponent(AbstractCraftingMenu screenHandler) {
		super(screenHandler, TABS);
	}

	protected boolean isCraftingSlot(Slot slot) {
		return this.menu.getResultSlot() == slot ||
			this.menu.getInputGridSlots().contains(slot);
	}

	private boolean canDisplay(RecipeDisplay display) {
		int gridWidth = this.menu.getGridWidth();
		int gridHeight = this.menu.getGridHeight();
		boolean result;

		switch (display) {
			case TASCraftingRecipeDisplay tas -> result = gridWidth >= tas.width() && gridHeight >= tas.height();
			default -> result = false;
		}

		return result;
	}

	protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipe, ContextMap context) {
		ghostSlots.setResult(this.menu.getResultSlot(), context, recipe.result());
		Objects.requireNonNull(recipe);

		switch (recipe) {
			case TASCraftingRecipeDisplay tas -> {
				List<Slot> inputSlots = this.menu.getInputGridSlots();

				List<SlotDisplay> ingredients = tas.ingredients();
				int recipeWidth = tas.width();
				int recipeHeight = tas.height();
				int gridWidth = this.menu.getGridWidth();
				int gridHeight = this.menu.getGridHeight();

				PlaceRecipeHelper.placeRecipe(
					gridWidth, gridHeight,
					recipeWidth, recipeHeight, ingredients,
					(ingredient, gridIndex, gridXPos, gridYPos) -> {
						Slot slot = inputSlots.get(gridIndex);
						ghostSlots.setInput(slot, context, ingredient);
					}
				);
			}

			default -> {
			}
		}
	}

	protected WidgetSprites getFilterButtonTextures() {
		return FILTER_BUTTON_SPRITES;
	}

	protected Component getRecipeFilterName() {
		return ONLY_CRAFTABLES_TOOLTIP;
	}

	protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents) {
		collection.selectRecipes(stackedContents, this::canDisplay);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	protected int getxOffset() {
		return 106;
	}

	static {
		TABS = List.of(
			new TabInfo(ModRecipeBookCategory.TURRET_ASSEMBLY_STATION),
			new TabInfo(ModItems.CANNON_TURRET, ModRecipeBookCategories.TAS_TURRETS),
			new TabInfo(ModItems.BALLISTA_BOLT, ModItems.MG_HEAD, ModRecipeBookCategories.TAS_PARTS),
			new TabInfo(ModItems.BOLT_HEAD, ModRecipeBookCategories.TAS_TRAPS),
			new TabInfo(ModItems.ELECTRIC_FENCE, ModRecipeBookCategories.TAS_DEFENSE),
			new TabInfo(ModItems.TURRET_REMOVER, ModRecipeBookCategories.TAS_EQUIPMENTS),
			new TabInfo(Items.COPPER_INGOT, ModRecipeBookCategories.TAS_MISC)
		);
	}
}
