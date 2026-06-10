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

import com.virus5600.defensive_measures.gui.screen.book.BaseRecipeBookWidget;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public abstract class BaseRecipeBookScreen<T extends RecipeBookMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
	private final BaseRecipeBookWidget<?> recipeBook;
	private boolean narrow;

	public BaseRecipeBookScreen(
		T handler, BaseRecipeBookWidget<?> recipeBook, Inventory inventory, Component title,
		int imageWidth, int imageHeight
	) {
		super(handler, inventory, title, imageWidth, imageHeight);

		this.recipeBook = recipeBook;
	}

	protected void init() {
		super.init();

		this.narrow = this.width < 379;
		this.getRecipeBook().initialize(this.width, this.height, this.minecraft, this.narrow);
		this.leftPos = this.getRecipeBook().findLeftEdge(this.width, this.imageWidth);
		this.addRecipeBook();
	}

	protected void addRecipeBook() {
		ScreenPosition screenPos = this.getRecipeBookButtonPos();

		this.addRenderableWidget(new ImageButton(
			screenPos.x(), screenPos.y(), 20, 18,
			this.getButtonTexture(), this::getRecipeBookButtonAction
		));
		this.addWidget(this.getRecipeBook());
	}

	public void extractContents(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
		if (this.getRecipeBook().isOpen() && this.narrow) {
			this.extractBackground(context, mouseX, mouseY, deltaTicks);
		} else {
			super.extractContents(context, mouseX, mouseY, deltaTicks);
		}

		context.nextStratum();
		this.getRecipeBook().extractRenderState(context, mouseX, mouseY, deltaTicks);

		context.nextStratum();
		this.extractCarriedItem(context, mouseX, mouseY);
		this.extractSnapbackItem(context);
		this.extractTooltip(context, mouseX, mouseY);
		this.getRecipeBook().drawTooltip(context, mouseX, mouseY, this.hoveredSlot);
	}

	protected void extractSlots(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY) {
		super.extractSlots(context, mouseX, mouseY);
		this.getRecipeBook().drawGhostSlots(context, this.shouldAddPaddingToGhostResult());
	}

	protected boolean shouldAddPaddingToGhostResult() {
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
			return this.narrow && this.getRecipeBook().isOpen() || super.mouseClicked(click, doubled);
		}
	}

	public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
		return this.getRecipeBook().mouseDragged(click, offsetX, offsetY) || super.mouseDragged(click, offsetX, offsetY);
	}

	protected boolean isHovering(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.getRecipeBook().isOpen()) && super.isHovering(x, y, width, height, pointX, pointY);
	}

	protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top) {
		boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.imageWidth) || mouseY >= (double)(top + this.imageHeight);
		return this.getRecipeBook().isClickOutsideBounds(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight) && bl;
	}

	protected void slotClicked(@NonNull Slot slot, int slotId, int button, @NonNull ContainerInput actionType) {
		super.slotClicked(slot, slotId, button, actionType);
		this.getRecipeBook().onMouseClick(slot);
	}

	public void containerTick() {
		super.containerTick();
		this.getRecipeBook().update();
	}

	public void recipesUpdated() {
		this.getRecipeBook().refresh();
	}

	public void fillGhostRecipe(@NonNull RecipeDisplay display) {
		this.getRecipeBook().onCraftFailed(display);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	protected BaseRecipeBookWidget<?> getRecipeBook() {
		return this.recipeBook;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	protected void getRecipeBookButtonAction(Button btnWidget) {
		this.getRecipeBook().toggleOpen();
		this.leftPos = this.getRecipeBook().findLeftEdge(this.width, this.imageWidth);

		ScreenPosition screenPos = this.getRecipeBookButtonPos();
		btnWidget.setPosition(screenPos.x(), screenPos.y());

		this.onRecipeBookToggled();
	}

	protected void onRecipeBookToggled() {
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //
	protected abstract ScreenPosition getRecipeBookButtonPos();

	protected abstract WidgetSprites getButtonTexture();
}
