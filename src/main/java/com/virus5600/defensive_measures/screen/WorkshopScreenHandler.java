package com.virus5600.defensive_measures.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.recipe.ModRecipeTypes;
import com.virus5600.defensive_measures.recipe.WorkshopShapedRecipe;

import org.jspecify.annotations.NonNull;

public class WorkshopScreenHandler extends BaseCraftingScreenHandler<WorkshopShapedRecipe> {
	private static final int WIDTH = 9;
	private static final int HEIGHT = 9;

	public WorkshopScreenHandler(int syncId, Inventory playerInventory) {
		this(syncId, playerInventory, ContainerLevelAccess.NULL);
	}

	public WorkshopScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess access) {
		super(ModScreenHandlers.WORKSHOP, syncId, playerInventory, access, WIDTH, HEIGHT, ModRecipeTypes.WORKSHOP_RECIPE_TYPE);

		this.setArrowPos(169, 57);
		this.addResultSlot(this.player, 195, 55);
		this.addCraftingGridSlots(5, 17);
		this.addStandardInventorySlots(playerInventory, 170, 103);
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //
	@Override
	public boolean stillValid(@NonNull Player player) {
		return stillValid(this.access, player, ModBlocks.WORKSHOP);
	}
}
