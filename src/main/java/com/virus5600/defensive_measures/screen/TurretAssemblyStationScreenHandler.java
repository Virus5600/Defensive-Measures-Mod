package com.virus5600.defensive_measures.screen;

import com.virus5600.defensive_measures.block.ModBlocks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;

import com.virus5600.defensive_measures.recipe.ModRecipeTypes;
import com.virus5600.defensive_measures.recipe.TASShapedRecipe;
import org.jspecify.annotations.NonNull;

public class TurretAssemblyStationScreenHandler extends BaseCraftingScreenHandler<TASShapedRecipe> {
	private static final int WIDTH = 7;
	private static final int HEIGHT = 7;

	public TurretAssemblyStationScreenHandler(int syncId, Inventory playerInventory) {
		this(syncId, playerInventory, ContainerLevelAccess.NULL);
	}

	public TurretAssemblyStationScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess access) {
		super(ModScreenHandlers.TURRET_ASSEMBLY_STATION, syncId, playerInventory, access, WIDTH, HEIGHT, ModRecipeTypes.TAS_RECIPE_TYPE);

		this.addResultSlot(this.player, 206, 71);
		this.addCraftingGridSlots(30, 17);
		this.addStandardInventorySlots(playerInventory, 66, 159);
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //
	@Override
	public boolean stillValid(@NonNull Player player) {
		return stillValid(this.access, player, ModBlocks.TURRET_ASSEMBLY_STATION);
	}
}
