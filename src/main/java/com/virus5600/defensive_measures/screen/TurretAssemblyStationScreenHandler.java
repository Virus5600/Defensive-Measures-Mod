package com.virus5600.defensive_measures.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class TurretAssemblyStationScreenHandler extends BaseCraftingScreenHandler {
	private static final int WIDTH = 7;
	private static final int HEIGHT = 7;

	public TurretAssemblyStationScreenHandler(int syncId, Inventory playerInventory) {
		this(syncId, playerInventory, ContainerLevelAccess.NULL);
	}

	public TurretAssemblyStationScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
		super(ModScreenHandlers.TURRET_ASSEMBLY_STATION, syncId, playerInventory, context, WIDTH, HEIGHT);

		this.addResultSlot(this.player, 206, 71);
		this.addCraftingGridSlots(30, 17);
		this.addStandardInventorySlots(playerInventory, 66, 159);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //
}
