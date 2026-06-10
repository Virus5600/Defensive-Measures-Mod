package com.virus5600.defensive_measures.screen;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.recipe.BaseCraftingRecipe;
import com.virus5600.defensive_measures.recipe.ModRecipeTypes;
import com.virus5600.defensive_measures.recipe.TASShapedRecipe;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseCraftingScreenHandler extends AbstractCraftingMenu {
	protected final ContainerLevelAccess context;
	protected final Player player;
	protected boolean filling;

	public BaseCraftingScreenHandler(MenuType type, int syncId, Inventory playerInventory, ContainerLevelAccess context, int width, int height) {
		super(type, syncId, width, height);

		this.context = context;
		this.player = playerInventory.player;
	}

	protected static <T extends BaseCraftingRecipe<CraftingInput>> void updateResult(
            AbstractContainerMenu handler,
            RecipeType<T> recipeType,
            ServerLevel world,
            Player player,
            CraftingContainer craftingInventory,
            ResultContainer resultInventory,
            @Nullable RecipeHolder<T> recipe
	) {
		CraftingInput craftingRecipeInput = craftingInventory.asCraftInput();
		ServerPlayer serverPlayerEntity = (ServerPlayer) player;
		ItemStack itemStack = ItemStack.EMPTY;

		if (world.getServer() != null) {
			Optional<RecipeHolder<T>> optional = world.getServer()
				.getRecipeManager()
				.getRecipeFor(
					recipeType,
					craftingRecipeInput,
					world,
					recipe != null ? recipe.id() : null
				);

			if (optional.isPresent()) {
				RecipeHolder<T> recipeEntry = optional.get();
				T customRecipe = recipeEntry.value();

				if (resultInventory.setRecipeUsed(serverPlayerEntity, recipeEntry)) {
					ItemStack craftedItem = customRecipe.assemble(craftingRecipeInput, world.registryAccess());

					if (craftedItem.isItemEnabled(world.enabledFeatures())) {
						itemStack = craftedItem;
					}
				}
			}

		}

		resultInventory.setItem(0, itemStack);
		handler.setRemoteSlot(0, itemStack);
		serverPlayerEntity.connection.send(
			new ClientboundContainerSetSlotPacket(
				handler.containerId,
				handler.incrementStateId(),
				0,
				itemStack
			)
		);
	}

	public void slotsChanged(@NonNull Container inventory) {
		if (!this.filling) {
			this.context.execute((world, _) -> {
				if (world instanceof ServerLevel serverWorld) {
					updateResult(
						this,
						ModRecipeTypes.TAS_RECIPE_TYPE,
						serverWorld,
						this.player,
						this.craftSlots,
						this.resultSlots,
						null
					);
				}
			});
		}
	}

	public void beginPlacingRecipe() {
		this.filling = true;
	}

	public void finishPlacingRecipe(@NonNull ServerLevel world, @NonNull RecipeHolder<CraftingRecipe> recipe) {
		this.filling = false;

		@SuppressWarnings("unchecked")
		RecipeHolder<TASShapedRecipe> tasRecipe = (RecipeHolder<TASShapedRecipe>) (Object) recipe;

		updateResult(
			this,
			ModRecipeTypes.TAS_RECIPE_TYPE,
			world,
			this.player,
			this.craftSlots,
			this.resultSlots,
			tasRecipe
		);
	}

	public void removed(@NonNull Player player) {
		super.removed(player);
		this.context.execute((_, _) -> this.clearContainer(player, this.craftSlots));
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	@Override
	public boolean canTakeItemForPickAll(@NonNull ItemStack stack, Slot slot) {
		return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
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
		return this.getGridWidth() * this.getGridHeight();
	}

	/**
	 * The default output slot of crafting screens (based on {@link net.minecraft.world.inventory.CraftingMenu#quickMoveStack(Player, int) CraftingScreenHandler#quickMove(PlayerEntity, int)}.
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
	public boolean stillValid(@NonNull Player player) {
		return stillValid(this.context, player, ModBlocks.TURRET_ASSEMBLY_STATION);
	}

	@Override @NonNull
	public ItemStack quickMoveStack(@NonNull Player player, int slot) {
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
		if (clickedSlot != null && clickedSlot.hasItem()) {
			// Copy the item stack in the clicked slot for returning later.
			ItemStack clickedSlotItemStack = clickedSlot.getItem();
			itemStack = clickedSlotItemStack.copy();

			// If the slot was the output...
			if (slot == outputSlotIndex) {
				clickedSlotItemStack.getItem().onCraftedBy(clickedSlotItemStack, player);

				// Try moving the item into the inventory starting from the hotbar
				if (!this.moveItemStackTo(clickedSlotItemStack, invSlotStart, hbSlotEnd, true)) {
					return ItemStack.EMPTY;
				}

				clickedSlot.onQuickCraft(clickedSlotItemStack, itemStack);
			}
			// If the clicked slot is from within the inventory and hotbar...
			else if (slot >= invSlotStart && slot < hbSlotEnd) {
				// Try moving the item into the input slots...
				if (!this.moveItemStackTo(clickedSlotItemStack, inputSlotStart, inputSlotEnd, false)) {
					if (slot < hbSlotStart) {
						// ... then to the hotbar...
						if (!this.moveItemStackTo(clickedSlotItemStack, hbSlotStart, hbSlotEnd, false)) {
							return ItemStack.EMPTY;
						}
					}
					// ... then to the inventory
					else if (!this.moveItemStackTo(clickedSlotItemStack, invSlotStart, invSlotEnd, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			// If all else fail, try moving it into the hotbar.
			else if (!this.moveItemStackTo(clickedSlotItemStack, invSlotStart, hbSlotEnd, false)) {
				return ItemStack.EMPTY;
			}

			// If there's no item in the clicked slot, set the clicked slot as empty
			if (clickedSlotItemStack.isEmpty()) {
				clickedSlot.setByPlayer(ItemStack.EMPTY);
			}
			// Otherwise, mark the slot as updated.
			else {
				clickedSlot.setChanged();
			}

			// If the clicked item has the same amount as the backed-up stack from the variable
			if (clickedSlotItemStack.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			clickedSlot.onTake(player, clickedSlotItemStack);

			// If the clicked slot was the output slot, drop the item if it couldn't be moved into the inventory or hotbar.
			if (slot == outputSlotIndex) {
				player.drop(clickedSlotItemStack, false);
			}
		}

		return itemStack;
	}

	@NonNull
	public Slot getResultSlot() {
		return this.slots.get(this.getOutputSlotIndex());
	}

	@NonNull
	public List<Slot> getInputGridSlots() {
		return this.slots.subList(this.getInputSlotStart(), this.getInputSlotEnd() + 1);
	}

	@Override @NonNull
	public final RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	@Override @NonNull
	protected Player owner() {
		return this.player;
	}

	@Override @NonNull
	@SuppressWarnings({"unchecked", "rawtypes"})
	public PostPlaceAction handlePlacement(boolean craftAll, boolean creative, @NonNull RecipeHolder<?> recipe, @NonNull ServerLevel world, @NonNull Inventory inventory) {
		RecipeHolder<BaseCraftingRecipe<?>> recipeEntry = (RecipeHolder) recipe;
		BaseCraftingRecipe<?> baseRecipe = recipeEntry.value();
		int recipeWidth = baseRecipe.getWidth();
		int recipeHeight = baseRecipe.getHeight();
		int gridWidth = this.getGridWidth();
		int gridHeight = this.getGridHeight();
		int startX = (gridWidth - recipeWidth) / 2;
		int startY = (gridHeight - recipeHeight) / 2;
		List<Slot> inputSlots = this.getInputGridSlots();
		List<Slot> centeredInputSlots = new ArrayList<>(recipeWidth * recipeHeight);

		for (int row = 0; row < recipeHeight; ++row) {
			for (int col = 0; col < recipeWidth; ++col) {
				int slotIndex = (startY + row) * gridWidth + startX + col;

				if (slotIndex >= 0 && slotIndex < inputSlots.size()) {
					centeredInputSlots.add(inputSlots.get(slotIndex));
				}
			}
		}

		if (centeredInputSlots.size() != recipeWidth * recipeHeight) {
			centeredInputSlots = inputSlots;
		}

		this.beginPlacingRecipe();

		RecipeBookMenu.PostPlaceAction result;
		try {
			result = ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess() {
				@Override
				public void fillCraftSlotsStackedContents(@NonNull StackedItemContents finder) {
					BaseCraftingScreenHandler.this.fillCraftSlotsStackedContents(finder);
				}

				@Override
				public void clearCraftingContent() {
					BaseCraftingScreenHandler.this.resultSlots.clearContent();
					BaseCraftingScreenHandler.this.craftSlots.clearContent();
				}

				@Override
				public boolean recipeMatches(@NonNull RecipeHolder entry) {
					BaseCraftingRecipe<?> recipe = (BaseCraftingRecipe<?>) entry.value();

					return recipe.matches(
						BaseCraftingScreenHandler.this.craftSlots.asCraftInput(),
						BaseCraftingScreenHandler.this.owner().level()
					);
				}
			}, recipeWidth, recipeHeight, centeredInputSlots, centeredInputSlots, inventory, (RecipeHolder) recipeEntry, craftAll, creative);
		} finally {
			this.finishPlacingRecipe(world, (RecipeHolder<CraftingRecipe>) (RecipeHolder) recipeEntry);
		}

		return result;
	}
}
