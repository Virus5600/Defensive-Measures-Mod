package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;

import com.virus5600.defensive_measures.gui.screen.book.overlay.TASOverlayRecipeComponent;
import com.virus5600.defensive_measures.gui.screen.ingame.TurretAssemblyStationScreen;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.display.FlexibleShapedCraftingRecipeDisplay;
import com.virus5600.defensive_measures.screen.TurretAssemblyStationScreenHandler;

/**
 * The custom blueprint component used by the {@link TurretAssemblyStationScreen}.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class TASBlueprintComponent extends BaseBlueprintComponent {
	private final BaseRecipeBookPage recipeBookPage;

	public TASBlueprintComponent(TurretAssemblyStationScreenHandler screenHandler) {
		super(screenHandler);

		SlotSelectTime slotSelectTime = () -> Mth.floor(this.displayTime / 30.0F);

		this.recipeBookPage = new BaseRecipeBookPage(
			this,
			slotSelectTime,
			new TASOverlayRecipeComponent(slotSelectTime, false)
		);
	}

	// ///////////////// //
	// OVERRIDEN METHODS //
	// ///////////////// //

	protected BaseRecipeBookPage recipeBookPage() {
		return this.recipeBookPage;
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
		return ModItems.TURRET_ASSEMBLY_STATION;
	}
}
