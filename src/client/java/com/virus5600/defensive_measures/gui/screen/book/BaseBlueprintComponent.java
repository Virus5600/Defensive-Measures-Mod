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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.book.ModPlaceRecipeHelper;
import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;
import com.virus5600.defensive_measures.recipe.display.FlexibleShapedCraftingRecipeDisplay;
import com.virus5600.defensive_measures.recipebook.ModRecipeBookCategory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.Objects;

/**
 * Base class for blueprint components in the recipe book.
 * <br><br>
 * This component will serve as the "recipe book" for the following benches of this mod:
 * <ul>
 *     <li>{@link TASBlueprintComponent Turret Assembly Station (TAS)}</li>
 *     <li>{@link WorkshopBlueprintComponent Workshop}</li>
 * </ul>
 * This allows the modded benches to show the crafting recipes of the modded items, assisting
 * players in creating what they need.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class BaseBlueprintComponent extends RecipeBookComponent<AbstractCraftingMenu> {
	private static final WidgetSprites FILTER_BUTTON_SPRITES;
	private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
	private static final List<TabInfo> TABS;

	public BaseBlueprintComponent(AbstractCraftingMenu screenHandler) {
		super(screenHandler, TABS);
	}

	protected boolean isCraftingSlot(Slot slot) {
		return this.menu.getResultSlot() == slot ||
			this.menu.getInputGridSlots().contains(slot);
	}

	protected boolean canDisplay(RecipeDisplay display) {
		int gridWidth = this.menu.getGridWidth();
		int gridHeight = this.menu.getGridHeight();
		boolean result;

		// NOTE: Include display condition handling here when created...
		switch (display) {
			case FlexibleShapedCraftingRecipeDisplay shaped -> result =
				gridWidth >= shaped.width()
				&& gridHeight >= shaped.height()
				&& this.getItemForSlotDisplay()
					.getDefaultInstance()
					.is(((SlotDisplay.ItemSlotDisplay) shaped.craftingStation())
						.item())
			;

			case ShapelessCraftingRecipeDisplay shapeless -> result =
				gridWidth * gridHeight >= shapeless.ingredients().size()
				&& this.getItemForSlotDisplay()
					.getDefaultInstance()
					.is(((SlotDisplay.ItemSlotDisplay) shapeless.craftingStation())
						.item())
			;

			default -> result = false;
		}

		return result;
	}

	protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipe, ContextMap ctx) {
		ghostSlots.setResult(this.menu.getResultSlot(), ctx, recipe.result());
		Objects.requireNonNull(recipe);

		switch (recipe) {
			case FlexibleShapedCraftingRecipeDisplay flexible -> this.fillGhostShapedRecipe(flexible, ghostSlots, ctx);
			case ShapelessCraftingRecipeDisplay shapeless -> {
				List<Slot> inputSlots = this.menu.getInputGridSlots();
				int slotCount = Math.min(shapeless.ingredients().size(), inputSlots.size());

				for (int i = 0; i < slotCount; i++) {
					ghostSlots.setInput(inputSlots.get(i), ctx, shapeless.ingredients().get(i));
				}
			}

			default -> DefensiveMeasures.LOGGER.warn("Recipe '{}' not supported for {}", recipe, this.getItemForSlotDisplay().toString());
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

	// ////////////// //
	// STATIC METHODS //
	// ////////////// //
	protected static <T extends AbstractCraftingMenu> void defaultFillGhostShaped(
		FlexibleShapedCraftingRecipeDisplay display,
		GhostSlots ghostSlots, ContextMap ctx, T menu
	) {
		List<Slot> inputSlots = menu.getInputGridSlots();

		List<SlotDisplay> ingredients = display.ingredients();
		int recipeWidth = display.width();
		int recipeHeight = display.height();
		int gridWidth = menu.getGridWidth();
		int gridHeight = menu.getGridHeight();

		ModPlaceRecipeHelper.placeRecipeCentered(
			gridWidth, gridHeight,
			recipeWidth, recipeHeight, ingredients,
			(ingredient, gridIndex, _, _) -> {
				Slot slot = inputSlots.get(gridIndex);
				ghostSlots.setInput(slot, ctx, ingredient);
			}
		);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	protected int getXOffset() {
		return 107;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected abstract void fillGhostShapedRecipe(
		FlexibleShapedCraftingRecipeDisplay display,
		GhostSlots ghostSlots, ContextMap ctx
	);

	public abstract Item getItemForSlotDisplay();

	// ///////////////////// //
	// STATIC INITIALIZATION //
	// ///////////////////// //

	static {
		FILTER_BUTTON_SPRITES = new WidgetSprites(
			Identifier.withDefaultNamespace("recipe_book/filter_enabled"),
			Identifier.withDefaultNamespace("recipe_book/filter_disabled"),
			Identifier.withDefaultNamespace("recipe_book/filter_enabled_highlighted"),
			Identifier.withDefaultNamespace("recipe_book/filter_disabled_highlighted")
		);

		TABS = List.of(
			new TabInfo(ModRecipeBookCategory.TURRET_ASSEMBLY_STATION),
			new TabInfo("tas_turrets", ModItems.CANNON_TURRET, ModRecipeBookCategories.TAS_TURRETS),
			new TabInfo("tas_parts", ModItems.MG_HEAD, ModItems.BALLISTA_BOW, ModRecipeBookCategories.TAS_PARTS),
			new TabInfo("tas_traps", ModItems.BOLT_HEAD, ModRecipeBookCategories.TAS_TRAPS),
			new TabInfo("tas_defense", ModItems.ELECTRIC_FENCE, ModRecipeBookCategories.TAS_DEFENSE),
			new TabInfo("tas_equipment", ModItems.TURRET_REMOVER, ModRecipeBookCategories.TAS_EQUIPMENT),
			new TabInfo("tas_misc", Items.COPPER_INGOT, ModRecipeBookCategories.TAS_MISC)
		);
	}
}
