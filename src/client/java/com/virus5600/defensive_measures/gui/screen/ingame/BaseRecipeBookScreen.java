package com.virus5600.defensive_measures.gui.screen.ingame;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.gui.screen.book.RecipeBookComponent;
import com.virus5600.defensive_measures.screen.BaseCraftingScreenHandler;
import com.virus5600.defensive_measures.screen.slots.SpriteResultSlot;
import com.virus5600.defensive_measures.screen.slots.SpriteSlot;

import com.google.common.collect.Maps;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.Map;

/**
 * The {@code BaseRecipeBookScreen} class is an abstract base class for creating custom recipe book
 * screens in this Minecraft mod. It extends the {@link AbstractContainerScreen} class and
 * implements the {@link RecipeUpdateListener} interface, allowing it to handle recipe updates
 * and manage the display of recipes within the screen.
 *
 * @param <T> The type of {@link RecipeBookMenu} that this screen is associated with. This generic
 *           type allows for flexibility in defining different types of recipe book screens that
 *           can work with various menu implementations.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class BaseRecipeBookScreen<T extends RecipeBookMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
	public static Identifier ARROW = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "container/crafting_arrow");

	private final RecipeBookComponent<?> recipeBook;
	protected final Map<AbstractWidget, UpdateScreenPosition> widgets;
	private final Dimension size;

	private ImageButton recipeBookBtn;
	private boolean narrow;
	protected boolean drawArrow;

	public BaseRecipeBookScreen(
		T handler, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title,
		int uiWidth, int uiHeight
	) {
		super(handler, inventory, title, uiWidth, uiHeight);

		this.recipeBook = recipeBook;
		this.widgets = Maps.newHashMap();
		this.size = new Dimension(uiWidth, uiHeight);
		this.drawArrow = true;
	}

	// ////////////////////// //
	// INITIALIZATION METHODS //
	// ////////////////////// //

	protected void init() {
		super.init();

		this.narrow = this.width < this.getUISize().getWidth() + 200;
		this.recipeBook.init(this.width, this.height, this.minecraft, this.narrow);
		this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
		this.initButton();
		this.initCloseButton();

		if (!this.hasRecipes()) {
			if (this.minecraft.player != null) {
				this.minecraft
					.player
					.sendOverlayMessage(Component.translatable("gui.recipebook.no_recipes"));
			}

			if (this.recipeBook.isVisible()) {
				this.recipeBook.toggleVisibility();
			}
		}
	}

	protected void initButton() {
		ScreenPosition screenPos = this.getRecipeBookButtonPos(
			this.leftPos, this.topPos,
			this.imageWidth, this.imageHeight,
			this.width, this.height
		);

		this.recipeBookBtn = new ImageButton(
			screenPos.x(), screenPos.y(), 20, 18,
			this.getButtonTexture(),
			this::recipeBookButtonAction
		);

		this.updateRecipeBookTooltip();

		this.addUpdatebleWidget(this.recipeBookBtn, this::getRecipeBookButtonPos);
		this.addWidget(this.getRecipeBook());
	}

	protected final void initCloseButton() {
		ScreenPosition screenPos = getCloseButtonPos(
			this.leftPos, this.topPos,
			this.imageWidth, this.imageHeight,
			this.width, this.height
		);

		ImageButton btn = new ImageButton(
			screenPos.x(), screenPos.y(), 15, 15,
			getCloseButtonTexture(),
			_ -> this.minecraft.gui.setScreen(null)
		);

		btn.setTooltip(Tooltip.create(Component.translatable("gui.recipebook.close")));

		this.addUpdatebleWidget(btn, BaseRecipeBookScreen::getCloseButtonPos);
	}

	// /////// //
	// METHODS //
	// /////// //

	public boolean hasRecipes() {
		int recipes = this.recipeBook.selectMatchingRecipes();

		return recipes > 0;
	}

	private void updateRecipeBookTooltip() {
		if (this.hasRecipes()) {
			this.recipeBookBtn.setTooltip(Tooltip.create(Component.translatable("gui.recipebook.blueprint")));
		}
		else {
			this.recipeBookBtn.setTooltip(Tooltip.create(Component.translatable("gui.recipebook.no_recipes")));
		}
	}

	public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.getRecipeBook().isVisible() && this.narrow) {
			this.extractBackground(graphics, mouseX, mouseY, deltaTicks);
		} else {
			this.extractContents(graphics, mouseX, mouseY, deltaTicks);
		}

		if (this.drawArrow) {
			this.drawArrow(graphics);
		}

		graphics.nextStratum();
		this.getRecipeBook().extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		graphics.nextStratum();
		this.extractCarriedItem(graphics, mouseX, mouseY);
		this.extractTooltip(graphics, mouseX, mouseY);
		this.getRecipeBook().extractTooltip(graphics, mouseX, mouseY, this.hoveredSlot);
	}

	protected void drawArrow(@NonNull GuiGraphicsExtractor graphics) {
		if (this.menu instanceof BaseCraftingScreenHandler<?> handler) {
			ScreenPosition pos = new ScreenPosition(
				(int) handler.getArrowPos().getX() + this.leftPos - 2,
				(int) handler.getArrowPos().getY() + this.topPos - 2
			);

			graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, this.getArrowSprite(),
				pos.x(), pos.y(),
				22, 15
			);
		}
	}

	protected void extractSlots(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		for(Slot slot : this.menu.slots) {
			if (slot.isActive()) {
				this.extractSlot(graphics, slot, mouseX, mouseY);
			}
		}

		this.getRecipeBook().extractGhostRecipe(graphics, this.isBiggerResultSlot());
	}

	protected void extractSlot(
		final GuiGraphicsExtractor graphics, final Slot slot,
		final int mouseX, final int mouseY
	) {
		if (slot instanceof SpriteSlot spriteSlot) {
			int xOrigin = spriteSlot.x - (spriteSlot.getPadding() / 2) - 1;
			int yOrigin = spriteSlot.y - (spriteSlot.getPadding() / 2) - 1;
			Dimension size = new Dimension(
				18 + spriteSlot.getPadding(),
				18 + spriteSlot.getPadding()
			);

			graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, spriteSlot.getSprite(),
				xOrigin, yOrigin,
				size.width, size.height
			);
		} else if (slot instanceof SpriteResultSlot spriteResultSlot) {
			int xOrigin = spriteResultSlot.x - (spriteResultSlot.getPadding() / 2) - 2;
			int yOrigin = spriteResultSlot.y - (spriteResultSlot.getPadding() / 2) - 2;
			Dimension size = new Dimension(
				20 + spriteResultSlot.getPadding(),
				20 + spriteResultSlot.getPadding()
			);

			graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, spriteResultSlot.getSprite(),
				xOrigin, yOrigin,
				size.width, size.height
			);
		}

		super.extractSlot(graphics, slot, mouseX, mouseY);
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

		if ((this.recipeBook.isVisible() || this.hasRecipes()) && this.minecraft.player != null) {
			int timesChanged = this.minecraft.player.getInventory().getTimesChanged();

			if (this.recipeBook.getTimesInventoryChanged() != timesChanged) {
				this.updateRecipeBookTooltip();
			}
		}

		this.recipeBook.tick();
	}

	public void recipesUpdated() {
		this.getRecipeBook().recipesUpdated();
	}

	public void fillGhostRecipe(@NonNull RecipeDisplay display) {
		this.getRecipeBook().fillGhostRecipe(display);
	}

	public final void updateWidgetPositions() {
		this.widgets.forEach((widget, lambda) -> {
			ScreenPosition newPos = lambda.updateScreenPosition(
				this.leftPos, this.topPos,
				this.imageWidth, this.imageHeight,
				this.width, this.height
			);

			widget.setPosition(newPos.x(), newPos.y());
		});
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	protected final Dimension getUISize() {
		return this.size;
	}

	/**
	 * Adds a widget that gets its position updated when {@link #updateWidgetPositions()} gets
	 * called.
	 *
	 * @param widget An instance of {@link AbstractWidget} that will be added to the screen and have its position updated.
	 * @param lambda An instance of {@link UpdateScreenPosition} functional interface that gets called to calculate the new position of the {@code widget}.
	 *
	 * @return The same widget that was passed in as a parameter, allowing for method chaining or further manipulation if needed.
	 *
	 * @see UpdateScreenPosition
	 *
	 * @implNote This method is final and cannot be re-implemented. To extend this method, you will
	 * have to create a new one and then call this method from within.
	 *
	 * @apiNote This method adds the widget to the {@link #widgets} field and in a private
	 * {@code renderer} field under the {@link net.minecraft.client.gui.screens.Screen Screen} class.
	 */
	protected final AbstractWidget addUpdatebleWidget(final AbstractWidget widget, final UpdateScreenPosition lambda) {
		this.widgets.put(widget, lambda);

		return super.addRenderableWidget(widget);
	}

	protected RecipeBookComponent<?> getRecipeBook() {
		return this.recipeBook;
	}

	public boolean isNarrow() {
		return this.narrow;
	}

	protected static ScreenPosition getCloseButtonPos(int leftPos, int topPos, int imageWidth, int imageHeight, int screenWidth, int screenHeight) {
		return new ScreenPosition(
			leftPos + imageWidth - 22,
			topPos + 7
		);
	}

	protected static WidgetSprites getCloseButtonTexture() {
		return new WidgetSprites(
			Identifier.withDefaultNamespace("widget/cross_button"),
			Identifier.withDefaultNamespace("widget/cross_button_highlighted")
		);
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	protected void recipeBookButtonAction(Button btnWidget) {
		if (!this.hasRecipes()) {
			if (this.minecraft.player != null) {
				this.minecraft
					.player
					.sendOverlayMessage(Component.translatable("gui.recipebook.no_recipes"));
			}
			return;
		}

		this.recipeBook.toggleVisibility();
		this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);

		this.updateWidgetPositions();
	}

	protected Identifier getArrowSprite() {
		return ARROW;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	/**
	 * Calculates the position of the recipe book button based on the given parameters and returns a {@link ScreenPosition} object.
	 *
	 * @param leftPos      The left-most position of the parent screen.
	 * @param topPos       The top-most position of the parent screen.
	 * @param imageWidth   The width of the parent screen's size.
	 * @param imageHeight  The height of the parent screen's size.
	 * @param screenWidth  The width of the current screen.
	 * @param screenHeight The height of the current screen.
	 *
	 * @return The calculated {@link ScreenPosition} object representing the position of the recipe book button.
	 */
	protected abstract ScreenPosition getRecipeBookButtonPos(int leftPos, int topPos, int imageWidth, int imageHeight, int screenWidth, int screenHeight);

	protected abstract WidgetSprites getButtonTexture();

	// //////////////////// //
	// FUNCTIONAL INTERFACE //
	// //////////////////// //

	/**
	 * A functional interface that guarantees an updated {@link ScreenPosition} for a widget or any
	 * element that requires a position update based on the current screen dimensions and the parent
	 * screen's size. And since this functional interface utilizes the screens top-left position and
	 * its size, it can be used to calculate the new position of any widget or element that needs
	 * to be repositioned when the screen size changes within or outside a screen/UI.
	 */
	@FunctionalInterface
	protected interface UpdateScreenPosition {
		/**
		 * Calculates the new screen position based on the given parameters and returns a {@link ScreenPosition} object.
		 *
		 * @param leftPos     The left-most position of the parent screen.
		 * @param topPos      The top-most position of the parent screen.
		 * @param imageWidth  The width of the parent screen's size.
		 * @param imageHeight The height of the parent screen's size.
		 * @param screenWidth  The width of the current screen.
		 * @param screenHeight The height of the current screen.
		 *
		 * @return The calculated {@link ScreenPosition} object representing the new position.
		 */
		ScreenPosition updateScreenPosition(int leftPos, int topPos, int imageWidth, int imageHeight, int screenWidth, int screenHeight);
	}
}
