package com.virus5600.defensive_measures.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.block.misc.tier3.FabricationMatrixBlock;
import com.virus5600.defensive_measures.inventory.ModContainerLevelAccess;
import com.virus5600.defensive_measures.recipe.FabMatShapedRecipe;
import com.virus5600.defensive_measures.recipe.ModRecipeTypes;
import com.virus5600.defensive_measures.screen.slots.FabMatResultSlot;

import org.jspecify.annotations.NonNull;

/**
 * A screen handler which provides the menu for the
 * {@link FabricationMatrixBlock Fabrication Matrix}'s crafting UI.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class FabricationMatrixScreenHandler extends BaseCraftingScreenHandler<FabMatShapedRecipe> {
	private static final int WIDTH = 9;
	private static final int HEIGHT = 9;

	public FabricationMatrixScreenHandler(int syncId, Inventory playerInventory) {
		this(syncId, playerInventory, ModContainerLevelAccess.NULL);
	}

	public FabricationMatrixScreenHandler(int syncId, Inventory playerInventory, ModContainerLevelAccess access) {
		super(ModScreenHandlers.FABRICATION_MATRIX, syncId, playerInventory, access, WIDTH, HEIGHT, ModRecipeTypes.FABRICATION_MATRIX_RECIPE_TYPE);

		this.setArrowPos(169, 57);

		int x = 195, y = 55;
		Level level = this.player.level();

		FabMatResultSlot resultSlot = new FabMatResultSlot(
			this.player, this.craftSlots,
			this.resultSlots, 0,
			x, y,
			level, access.getPos()
		);

		this.addResultSlot(resultSlot, x , y);

		this.addCraftingGridSlots(5, 17);
		this.addStandardInventorySlots(playerInventory, 170, 103);
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //
	@Override
	public boolean stillValid(@NonNull Player player) {
		return stillValid(this.access, player, ModBlocks.FABRICATION_MATRIX);
	}
}
