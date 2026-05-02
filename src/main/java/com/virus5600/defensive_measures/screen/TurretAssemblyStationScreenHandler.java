package com.virus5600.defensive_measures.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.ScreenHandlerContext;

public class TurretAssemblyStationScreenHandler extends BaseCraftingScreenHandler {
	private static final int WIDTH = 7;
	private static final int HEIGHT = 7;

	public TurretAssemblyStationScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public TurretAssemblyStationScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ModScreenHandlers.TURRET_ASSEMBLY_STATION, syncId, playerInventory, context, WIDTH, HEIGHT);

		this.addResultSlot(this.player, 206, 71);
		this.addInputSlots(30, 17);
		this.addPlayerSlots(playerInventory, 66, 159);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	// TODO: Implement populateRecipeFinder;
}
