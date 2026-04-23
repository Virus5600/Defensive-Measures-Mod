package com.virus5600.defensive_measures.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import org.jspecify.annotations.Nullable;

import com.virus5600.defensive_measures.block.ModBlocks;

import java.util.List;
import java.util.Optional;

public abstract class BaseCraftingScreenHandler extends AbstractCraftingScreenHandler {
	protected final ScreenHandlerContext context;
	protected final PlayerEntity player;
	protected boolean filling;

	public BaseCraftingScreenHandler(ScreenHandlerType type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, int width, int height) {
		super(type, syncId, width, height);

		this.context = context;
		this.player = playerInventory.player;
	}

	protected static void updateResult(ScreenHandler handler, ServerWorld world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, @Nullable RecipeEntry<CraftingRecipe> recipe) {
		CraftingRecipeInput craftingRecipeInput = craftingInventory.createRecipeInput();
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
		ItemStack itemStack = ItemStack.EMPTY;

		if (world.getServer() != null) {
			Optional<RecipeEntry<CraftingRecipe>> optional = world.getServer()
				.getRecipeManager()
				.getFirstMatch(
					RecipeType.CRAFTING,
					craftingRecipeInput,
					world,
					recipe != null ? recipe.id() : null
				);

			if (optional.isPresent()) {
				RecipeEntry<CraftingRecipe> recipeEntry = optional.get();
				CraftingRecipe craftingRecipe = recipeEntry.value();

				if (resultInventory.shouldCraftRecipe(serverPlayerEntity, recipeEntry)) {
					ItemStack itemStack2 = craftingRecipe.craft(craftingRecipeInput, world.getRegistryManager());

					if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
						itemStack = itemStack2;
					}
				}
			}

		}

		resultInventory.setStack(0, itemStack);
		handler.setReceivedStack(0, itemStack);
		serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack));
	}

	public void onContentChanged(Inventory inventory) {
		if (!this.filling) {
			this.context.run((world, pos) -> {
				if (world instanceof ServerWorld serverWorld) {
					updateResult(this, serverWorld, this.player, this.craftingInventory, this.craftingResultInventory, null);
				}
			});
		}
	}

	public void onInputSlotFillStart() {
		this.filling = true;
	}

	public void onInputSlotFillFinish(ServerWorld world, RecipeEntry<CraftingRecipe> recipe) {
		this.filling = false;
		updateResult(this, world, this.player, this.craftingInventory, this.craftingResultInventory, recipe);
	}

	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.craftingInventory));
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.craftingResultInventory && super.canInsertIntoSlot(stack, slot);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	/**
	 * The amount of slots the crafting grid has.
	 *
	 * @return {@code int} amount of input slots, which is the width multiplied by the height of the crafting grid by default.
	 */
	protected int getInputSlotCount() {
		return this.getWidth() * this.getHeight();
	}

	/**
	 * The default output slot of crafting screens (based on {@link net.minecraft.screen.CraftingScreenHandler#quickMove(PlayerEntity, int) CraftingScreenHandler#quickMove(PlayerEntity, int)}.
	 *
	 * @return {@code int} index of the output slot, which is 0 by default.
	 */
	protected int getOutputSlotIndex() {
		return 0;
	}

	/**
	 * Gets the index of the first input slot. By default, this is the index of the output slot + 1,
	 * since the output slot is usually added before the input slots.
	 *
	 * @return {@code int} index of the first input slot.
	 */
	protected int getInputSlotStart() {
		return this.getOutputSlotIndex() + 1;
	}

	/**
	 * Gets the index of the last input slot. By default, this is the index of the first input slot
	 * + the amount of input slots, since the input slots are usually added in a contiguous manner.
	 *
	 * @return {@code int} index of the last input slot.
	 */
	protected int getInputSlotEnd() {
		return this.getInputSlotStart() + this.getInputSlotCount() - 1;
	}

	/**
	 * Gets the index of the first inventory slot. By default, this is the index of the last input
	 * slot + 1, since the inventory slots are usually added after the input slots.
	 *
	 * @return {@code int} index of the first inventory slot.
	 */
	protected int getInventoryStart() {
		return this.getInputSlotEnd() + 1;
	}

	/**
	 * Gets the index of the last inventory slot. By default, this is the index of the first
	 * inventory slot + 26, since the vanilla player inventory has 27 slots (not including the
	 * hotbar) and due to the count being inclusive, minus 1 is needed.
	 *
	 * @return {@code int} index of the last inventory slot.
	 */
	protected int getInventoryEnd() {
		return this.getInventoryStart() + 26;
	}

	/**
	 * Gets the index of the first hotbar slot. By default, this is the index of the last inventory
	 * slot, since the hotbar slots are usually added after the inventory slots.
	 *
	 * @return {@code int} index of the first hotbar slot.
	 */
	protected int getHotbarStart() {
		return this.getInventoryEnd() + 1;
	}

	/**
	 * Gets the index of the last hotbar slot. By default, this is the index of the first hotbar
	 * slot + 8, since the vanilla player hotbar has 9 slots and due to the count being inclusive,
	 * minus 1 is needed.
	 *
	 * @return {@code int} index of the last hotbar slot.
	 */
	protected int getHotbarEnd() {
		return this.getHotbarStart() + 8;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, ModBlocks.TURRET_ASSEMBLY_STATION);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		int outputSlotIndex = this.getOutputSlotIndex();
		int inputSlotStart = this.getInputSlotStart();
		int inputSlotEnd = this.getInputSlotEnd() + 1;
		int invSlotStart = this.getInventoryStart();
		int invSlotEnd = this.getInventoryEnd() + 1;
		int hbSlotStart = this.getHotbarStart();
		int hbSlotEnd = this.getHotbarEnd() + 1;

		ItemStack itemStack = ItemStack.EMPTY;
		Slot clickedSlot = this.slots.get(slot);

		// If the clicked slot has an item...
		if (clickedSlot != null && clickedSlot.hasStack()) {
			// Copy the item stack in the clicked slot for returning later.
			ItemStack clickedSlotItemStack = clickedSlot.getStack();
			itemStack = clickedSlotItemStack.copy();

			// If the slot was the output...
			if (slot == outputSlotIndex) {
				clickedSlotItemStack.getItem().onCraftByPlayer(clickedSlotItemStack, player);

				// Try moving the item into the inventory starting from the hotbar
				if (!this.insertItem(clickedSlotItemStack, invSlotStart, hbSlotEnd, true)) {
					return ItemStack.EMPTY;
				}

				clickedSlot.onQuickTransfer(clickedSlotItemStack, itemStack);
			}
			// If the clicked slot is from within the inventory and hotbar...
			else if (slot >= invSlotStart && slot < hbSlotEnd) {
				// Try moving the item into the input slots...
				if (!this.insertItem(clickedSlotItemStack, inputSlotStart, inputSlotEnd, false)) {
					if (slot < hbSlotStart) {
						// ... then to the hotbar...
						if (!this.insertItem(clickedSlotItemStack, hbSlotStart, hbSlotEnd, false)) {
							return ItemStack.EMPTY;
						}
					}
					// ... then to the inventory
					else if (!this.insertItem(clickedSlotItemStack, invSlotStart, invSlotEnd, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			// If all else fail, try moving it into the hotbar.
			else if (!this.insertItem(clickedSlotItemStack, invSlotStart, hbSlotEnd, false)) {
				return ItemStack.EMPTY;
			}

			// If there's no item in the clicked slot, set the clicked slot as empty
			if (clickedSlotItemStack.isEmpty()) {
				clickedSlot.setStack(ItemStack.EMPTY);
			}
			// Otherwise, mark the slot as updated.
			else {
				clickedSlot.markDirty();
			}

			// If the clicked item has the same amount as the backed-up stack from the variable
			if (clickedSlotItemStack.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			clickedSlot.onTakeItem(player, clickedSlotItemStack);

			// If the clicked slot was the output slot, drop the item if it couldn't be moved into the inventory or hotbar.
			if (slot == outputSlotIndex) {
				player.dropItem(clickedSlotItemStack, false);
			}
		}

		return itemStack;
	}

	public Slot getOutputSlot() {
		return this.slots.get(this.getOutputSlotIndex());
	}

	public List<Slot> getInputSlots() {
		return this.slots.subList(1, this.getInputSlotEnd());
	}

	@Override
	public RecipeBookType getCategory() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	protected PlayerEntity getPlayer() {
		return this.player;
	}
}
