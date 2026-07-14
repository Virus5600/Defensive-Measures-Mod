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

import com.virus5600.defensive_measures.recipe.BaseCraftingRecipe;
import com.virus5600.defensive_measures.screen.slots.SpriteResultSlot;
import com.virus5600.defensive_measures.screen.slots.SpriteSlot;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This abstract class serves as the base for all crafting menu that will be used by this mod. This
 * class is only able to handle grid-system crafting menus and thus, other kind of menu requires a
 * different abstract class.
 *
 * @param <T> The type of recipe this crafting screen handler is for.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class BaseCraftingScreenHandler<
	T extends BaseCraftingRecipe<CraftingInput>
> extends AbstractCraftingMenu {
	protected final RecipeType<T> recipeType;
	protected final ContainerLevelAccess access;
	protected final Player player;
	protected boolean placingRecipe;
	protected Point arrowPos;

	public BaseCraftingScreenHandler(
		MenuType type, int syncId,
		Inventory playerInventory, ContainerLevelAccess access,
		int width, int height, RecipeType<T> recipeType
	) {
		super(type, syncId, width, height);

		this.access = access;
		this.player = playerInventory.player;

		this.recipeType = recipeType;
	}

	// /////// //
	// METHODS //
	// /////// //

	public void setArrowPos(final int x, final int y) {
		this.setArrowPos(new Point(x, y));
	}

	public void setArrowPos(final Point position) {
		this.arrowPos = position;
	}

	public Point getArrowPos() {
		return this.arrowPos;
	}

	/**
	 * Adds a result slot using the {@link SpriteResultSlot} slot.
	 *
	 * @param player The player who is using the crafting screen.
	 * @param x      The x position of the slot.
	 * @param y      The y position of the slot.
	 *
	 * @return {@link Slot} The result slot that was added.
	 */
	protected Slot addResultSlot(final Player player, final int x, final int y) {
		return this.addResultSlot(new SpriteResultSlot(
			player, this.craftSlots,
			this.resultSlots, 0,
			x, y
		), x, y);
	}

	/**
	 * Adds a custom {@link Slot} as the result slot. This method allows inheritors to provide
	 * their own custom slot, allowing the result slot to be modified as needed.
	 *
	 * @param slot The custom slot to be added as the result slot.
	 * @param x    The x position of the slot.
	 * @param y    The y position of the slot.
	 *
	 * @return {@link Slot} The result slot that was added.
	 */
	protected Slot addResultSlot(final Slot slot, final int x, final int y) {
		int padding = 6;

		if (this.arrowPos == null) {
			// Arrow res is 22 x 15
			int arrowX = x - (22 + 15);
			int arrowY = y - (padding / 2) + (((20 + padding) - 15) / 2);

			this.arrowPos = new Point(arrowX, arrowY);
		}

		if (slot instanceof SpriteResultSlot srs) {
			srs.setPadding(padding);
		}

		return this.addSlot(slot);
	}

	protected void addCraftingGridSlots(final int left, final int top) {
		for(int y = 0; y < this.getGridWidth(); ++y) {
			for(int x = 0; x < this.getGridHeight(); ++x) {
				this.addSlot(new SpriteSlot(
					this.craftSlots, x + y * this.getGridWidth(),
					left + x * 18,
					top + y * 18)
				);
			}
		}
	}

	protected void addInventoryHotbarSlots(final Container inventory, final int left, final int top) {
		for(int x = 0; x < 9; ++x) {
			this.addSlot(new SpriteSlot(
				inventory, x,
				left + x * 18, top
			));
		}
	}

	protected void addInventoryExtendedSlots(final Container inventory, final int left, final int top) {
		for(int y = 0; y < 3; ++y) {
			for(int x = 0; x < 9; ++x) {
				this.addSlot(new SpriteSlot(
					inventory, x + (y + 1) * 9,
					left + x * 18, top + y * 18
				));
			}
		}
	}

	protected static <T extends BaseCraftingRecipe<CraftingInput>> void slotChangedCraftingGrid(
            AbstractContainerMenu menu,
            RecipeType<T> recipeType,
            ServerLevel level,
            Player player,
            CraftingContainer container,
            ResultContainer resultSlots,
            @Nullable RecipeHolder<T> recipeHint
	) {
		CraftingInput input = container.asCraftInput();
		ServerPlayer serverPlayer = (ServerPlayer) player;
		ItemStack result = ItemStack.EMPTY;
		Optional<RecipeHolder<T>> maybeRecipe = level.getServer()
			.getRecipeManager()
			.getRecipeFor(
				recipeType,
				input,
				level,
				recipeHint != null ? recipeHint.id() : null
			);

		if (maybeRecipe.isPresent()) {
			RecipeHolder<T> recipeHolder = maybeRecipe.get();
			T craftingRecipe = recipeHolder.value();

			if (resultSlots.setRecipeUsed(serverPlayer, recipeHolder)) {
				ItemStack recipeResult = craftingRecipe.assemble(input);

				if (recipeResult.isItemEnabled(level.enabledFeatures())) {
					result = recipeResult;
				}
			}
		}

		resultSlots.setItem(0, result);
		menu.setRemoteSlot(0, result);
		serverPlayer.connection.send(
			new ClientboundContainerSetSlotPacket(
				menu.containerId,
				menu.incrementStateId(),
				0,
				result
			)
		);
	}

	public void slotsChanged(@NonNull Container inventory) {
		if (!this.placingRecipe) {
			this.access.execute((level, _) -> {
				if (level instanceof ServerLevel serverWorld) {
					slotChangedCraftingGrid(
						this,
						this.recipeType,
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
		this.placingRecipe = true;
	}

	public void finishPlacingRecipe(@NonNull ServerLevel level, @NonNull RecipeHolder<CraftingRecipe> recipe) {
		this.placingRecipe = false;

		@SuppressWarnings("unchecked")
		RecipeHolder<T> tasRecipe = (RecipeHolder<T>) (Object) recipe;

		slotChangedCraftingGrid(
			this,
			this.recipeType,
			level,
			this.player,
			this.craftSlots,
			this.resultSlots,
			tasRecipe
		);
	}

	public void removed(@NonNull Player player) {
		super.removed(player);
		this.access.execute((_, _) -> this.clearContainer(player, this.craftSlots));
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	@Override
	public boolean canTakeItemForPickAll(@NonNull ItemStack carried, Slot target) {
		return target.container != this.resultSlots && super.canTakeItemForPickAll(carried, target);
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

	@Override @NonNull
	public ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
		int outputSlotIndex = this.getOutputSlotIndex();
		int inputSlotStart = this.getInputSlotStart();
		int inputSlotEnd = this.getInputSlotEnd() + 1;
		int invSlotStart = this.getInventoryStart();
		int invSlotEnd = this.getInventoryEnd() + 1;
		// hb == hotbar
		int hbSlotStart = this.getHotbarStart();
		int hbSlotEnd = this.getHotbarEnd() + 1;

		ItemStack clicked = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);

		// If the clicked slot has an item...
		if (slot != null && slot.hasItem()) {
			// Copy the item stack in the clicked slot for returning later.
			ItemStack stack = slot.getItem();
			clicked = stack.copy();

			// If the slot was the output...
			if (slotIndex == outputSlotIndex) {
				stack.getItem().onCraftedBy(stack, player);

				// Try moving the item into the inventory starting from the hotbar
				if (!this.moveItemStackTo(stack, invSlotStart, hbSlotEnd, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(stack, clicked);
			}
			// If the clicked slot is from within the inventory and hotbar...
			else if (slotIndex >= invSlotStart && slotIndex < hbSlotEnd) {
				// Try moving the item into the input slots...
				if (!this.moveItemStackTo(stack, inputSlotStart, inputSlotEnd, false)) {
					if (slotIndex < hbSlotStart) {
						// ... then to the hotbar...
						if (!this.moveItemStackTo(stack, hbSlotStart, hbSlotEnd, false)) {
							return ItemStack.EMPTY;
						}
					}
					// ... then to the inventory
					else if (!this.moveItemStackTo(stack, invSlotStart, invSlotEnd, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			// If all else fail, try moving it into the hotbar.
			else if (!this.moveItemStackTo(stack, invSlotStart, hbSlotEnd, false)) {
				return ItemStack.EMPTY;
			}

			// If there's no item in the clicked slot, set the clicked slot as empty
			if (stack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			}
			// Otherwise, mark the slot as updated.
			else {
				slot.setChanged();
			}

			// If the clicked item has the same amount as the backed-up stack from the variable
			if (stack.getCount() == clicked.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, stack);

			// If the clicked slot was the output slot, drop the item if it couldn't be moved into the inventory or hotbar.
			if (slotIndex == outputSlotIndex) {
				player.drop(stack, false);
			}
		}

		return clicked;
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
	public PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, @NonNull RecipeHolder<?> recipe, @NonNull ServerLevel level, @NonNull Inventory inventory) {
		RecipeHolder<BaseCraftingRecipe<?>> typedRecipe = (RecipeHolder) recipe;
		BaseCraftingRecipe<?> baseRecipe = typedRecipe.value();

		int recipeWidth;
		int recipeHeight;

		int gridWidth = this.getGridWidth();
		int gridHeight = this.getGridHeight();
		List<Slot> inputSlots = this.getInputGridSlots();
		List<Slot> centeredInputSlots;

		if (baseRecipe.isShaped()) {
			recipeWidth = baseRecipe.getWidth();
			recipeHeight = baseRecipe.getHeight();

			int startX = (gridWidth - recipeWidth) / 2;
			int startY = ((gridHeight - recipeHeight) / 2);

			centeredInputSlots = new ArrayList<>(recipeWidth * recipeHeight);
			boolean outOfBounds = false;

			for (int row = 0; row < recipeHeight; ++row) {
				for (int col = 0; col < recipeWidth; ++col) {
					int slotIndex = (startY + row) * gridWidth + startX + col;

					if (slotIndex >= 0 && slotIndex < inputSlots.size()) {
						Slot slot = inputSlots.get(slotIndex);
						centeredInputSlots.add(slot);
					}
					else {
						outOfBounds = true;
					}
				}
			}

			if (centeredInputSlots.size() != recipeWidth * recipeHeight || outOfBounds) {
				centeredInputSlots = inputSlots;
			}
		}
		else {
			recipeWidth = gridWidth;
			recipeHeight = gridHeight;

			centeredInputSlots = inputSlots;
		}

		this.beginPlacingRecipe();
		RecipeBookMenu.PostPlaceAction result;

		try {
			result = ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<T>() {
				@Override
				public void fillCraftSlotsStackedContents(@NonNull StackedItemContents stackedContents) {
					BaseCraftingScreenHandler.this.fillCraftSlotsStackedContents(stackedContents);
				}

				@Override
				public void clearCraftingContent() {
					BaseCraftingScreenHandler.this.resultSlots.clearContent();
					BaseCraftingScreenHandler.this.craftSlots.clearContent();
				}

				@Override
				public boolean recipeMatches(@NonNull RecipeHolder entry) {
					BaseCraftingRecipe<CraftingInput> recipe = (BaseCraftingRecipe<CraftingInput>) entry.value();

					return recipe.matches(
						BaseCraftingScreenHandler.this.craftSlots.asCraftInput(),
						BaseCraftingScreenHandler.this.owner().level()
					);
				}
			}, recipeWidth, recipeHeight, centeredInputSlots, centeredInputSlots, inventory, (RecipeHolder) typedRecipe, useMaxItems, allowDroppingItemsToClear);
		} finally {
			this.finishPlacingRecipe(level, (RecipeHolder<CraftingRecipe>) (RecipeHolder) typedRecipe);
		}

		return result;
	}
}
