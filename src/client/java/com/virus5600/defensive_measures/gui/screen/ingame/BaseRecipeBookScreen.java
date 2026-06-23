package com.virus5600.defensive_measures.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import com.virus5600.defensive_measures.gui.screen.book.RecipeBookComponent;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public abstract class BaseRecipeBookScreen<T extends RecipeBookMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
	private final RecipeBookComponent<?> recipeBook;
	private boolean narrow;

	public BaseRecipeBookScreen(
		T handler, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title,
		int imageWidth, int imageHeight
	) {
		super(handler, inventory, title, imageWidth, imageHeight);

		this.recipeBook = recipeBook;
	}

	protected void init() {
		super.init();

		this.narrow = this.width < 379;
		this.getRecipeBook().init(this.width, this.height, this.minecraft, this.narrow);
		this.leftPos = this.getRecipeBook().updateScreenPosition(this.width, this.imageWidth);
		this.initButton();
	}

	protected void initButton() {
		ScreenPosition screenPos = this.getRecipeBookButtonPos();

		this.addRenderableWidget(new ImageButton(
			screenPos.x(), screenPos.y(), 20, 18,
			this.getButtonTexture(), this::getRecipeBookButtonAction
		));
		this.addWidget(this.getRecipeBook());
	}

	public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.getRecipeBook().isVisible() && this.narrow) {
			this.extractBackground(graphics, mouseX, mouseY, deltaTicks);
		} else {
			super.extractContents(graphics, mouseX, mouseY, deltaTicks);
		}

		graphics.nextStratum();
		this.getRecipeBook().extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		graphics.nextStratum();
		this.extractCarriedItem(graphics, mouseX, mouseY);
		this.extractTooltip(graphics, mouseX, mouseY);
		this.getRecipeBook().extractTooltip(graphics, mouseX, mouseY, this.hoveredSlot);
	}

	protected void extractSlots(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY) {
		super.extractSlots(context, mouseX, mouseY);
		this.getRecipeBook().extractGhostRecipe(context, this.isBiggerResultSlot());
	}

	protected boolean isBiggerResultSlot() {
		return true;
	}

	public boolean charTyped(@NonNull CharacterEvent input) {
		return this.getRecipeBook().charTyped(input) || super.charTyped(input);
	}

	public boolean keyPressed(@NonNull KeyEvent input) {
		return this.getRecipeBook().keyPressed(input) || super.keyPressed(input);
	}

	public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
		if (this.getRecipeBook().mouseClicked(click, doubled)) {
			this.setFocused(this.recipeBook);
			return true;
		} else {
			return this.narrow && this.getRecipeBook().isVisible() || super.mouseClicked(click, doubled);
		}
	}

	public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
		return this.getRecipeBook().mouseDragged(click, offsetX, offsetY) || super.mouseDragged(click, offsetX, offsetY);
	}

	protected boolean isHovering(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.getRecipeBook().isVisible()) && super.isHovering(x, y, width, height, pointX, pointY);
	}

	protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top) {
		boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.imageWidth) || mouseY >= (double)(top + this.imageHeight);
		return this.getRecipeBook().hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight) && bl;
	}

	protected void slotClicked(@NonNull Slot slot, int slotId, int button, @NonNull ContainerInput actionType) {
		super.slotClicked(slot, slotId, button, actionType);
		this.getRecipeBook().slotClicked(slot);
	}

	public void containerTick() {
		super.containerTick();
		this.getRecipeBook().tick();
	}

	public void recipesUpdated() {
		this.getRecipeBook().recipesUpdated();
	}

	public void fillGhostRecipe(@NonNull RecipeDisplay display) {
		this.getRecipeBook().fillGhostRecipe(display);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	protected RecipeBookComponent<?> getRecipeBook() {
		return this.recipeBook;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	protected void getRecipeBookButtonAction(Button btnWidget) {
		this.getRecipeBook().toggleVisibility();
		this.leftPos = this.getRecipeBook().updateScreenPosition(this.width, this.imageWidth);

		ScreenPosition updatedButtonPos = this.getRecipeBookButtonPos();
		btnWidget.setPosition(updatedButtonPos.x(), updatedButtonPos.y());

		this.onRecipeBookButtonClick();
	}

	protected void onRecipeBookButtonClick() {
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //
	protected abstract ScreenPosition getRecipeBookButtonPos();

	protected abstract WidgetSprites getButtonTexture();
}
