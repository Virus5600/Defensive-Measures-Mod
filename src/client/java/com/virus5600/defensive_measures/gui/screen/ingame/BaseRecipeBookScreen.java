package com.virus5600.defensive_measures.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import com.virus5600.defensive_measures.gui.screen.book.BaseRecipeBookWidget;

@Environment(EnvType.CLIENT)
public abstract class BaseRecipeBookScreen<T extends AbstractRecipeScreenHandler> extends HandledScreen<T> implements RecipeBookProvider {
	private final BaseRecipeBookWidget<?> recipeBook;
	private boolean narrow;

	public BaseRecipeBookScreen(T handler, BaseRecipeBookWidget<?> recipeBook, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);

		this.recipeBook = recipeBook;
	}

	protected void init() {
		super.init();

		this.narrow = this.width < 379;
		this.getRecipeBook().initialize(this.width, this.height, this.client, this.narrow);
		this.x = this.getRecipeBook().findLeftEdge(this.width, this.backgroundWidth);
		this.addRecipeBook();
	}

	protected void addRecipeBook() {
		ScreenPos screenPos = this.getRecipeBookButtonPos();

		this.addDrawableChild(new TexturedButtonWidget(
			screenPos.x(), screenPos.y(), 20, 18,
			this.getButtonTexture(), this::getRecipeBookButtonAction
		));
		this.addSelectableChild(this.getRecipeBook());
	}

	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.getRecipeBook().isOpen() && this.narrow) {
			this.renderBackground(context, mouseX, mouseY, deltaTicks);
		} else {
			super.renderMain(context, mouseX, mouseY, deltaTicks);
		}

		context.createNewRootLayer();
		this.getRecipeBook().render(context, mouseX, mouseY, deltaTicks);

		context.createNewRootLayer();
		this.renderCursorStack(context, mouseX, mouseY);
		this.renderLetGoTouchStack(context);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
		this.getRecipeBook().drawTooltip(context, mouseX, mouseY, this.focusedSlot);
	}

	protected void drawSlots(DrawContext context, int mouseX, int mouseY) {
		super.drawSlots(context, mouseX, mouseY);
		this.getRecipeBook().drawGhostSlots(context, this.shouldAddPaddingToGhostResult());
	}

	protected boolean shouldAddPaddingToGhostResult() {
		return true;
	}

	public boolean charTyped(CharInput input) {
		return this.getRecipeBook().charTyped(input) || super.charTyped(input);
	}

	public boolean keyPressed(KeyInput input) {
		return this.getRecipeBook().keyPressed(input) || super.keyPressed(input);
	}

	public boolean mouseClicked(Click click, boolean doubled) {
		if (this.getRecipeBook().mouseClicked(click, doubled)) {
			this.setFocused(this.recipeBook);
			return true;
		} else {
			return this.narrow && this.getRecipeBook().isOpen() || super.mouseClicked(click, doubled);
		}
	}

	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		return this.getRecipeBook().mouseDragged(click, offsetX, offsetY) || super.mouseDragged(click, offsetX, offsetY);
	}

	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.getRecipeBook().isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
	}

	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top) {
		boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
		return this.getRecipeBook().isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight) && bl;
	}

	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
		this.getRecipeBook().onMouseClick(slot);
	}

	public void handledScreenTick() {
		super.handledScreenTick();
		this.getRecipeBook().update();
	}

	public void refreshRecipeBook() {
		this.getRecipeBook().refresh();
	}

	public void onCraftFailed(RecipeDisplay display) {
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

	protected void getRecipeBookButtonAction(ButtonWidget btnWidget) {
		this.getRecipeBook().toggleOpen();
		this.x = this.getRecipeBook().findLeftEdge(this.width, this.backgroundWidth);

		ScreenPos screenPos = this.getRecipeBookButtonPos();
		btnWidget.setPosition(screenPos.x(), screenPos.y());

		this.onRecipeBookToggled();
	}

	protected void onRecipeBookToggled() {
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //
	protected abstract ScreenPos getRecipeBookButtonPos();

	protected abstract ButtonTextures getButtonTexture();
}
