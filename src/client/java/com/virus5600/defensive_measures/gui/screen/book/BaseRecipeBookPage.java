package com.virus5600.defensive_measures.gui.screen.book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BaseRecipeBookPage {
	protected static final WidgetSprites PAGE_BACKWARD_TEXTURES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/page_backward"), Identifier.withDefaultNamespace("recipe_book/page_backward_highlighted"));
	protected static final WidgetSprites PAGE_FORWARD_TEXTURES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/page_forward"), Identifier.withDefaultNamespace("recipe_book/page_forward_highlighted"));

	private final RecipeBookComponent<?> recipeBookWidget;
	private final OverlayRecipeComponent altWidget;
	private final List<BaseRecipeButton> buttons = Lists.newArrayListWithCapacity(20);

	private Minecraft client;
	private ClientRecipeBook recipeBook;
	private List<RecipeCollection> resultCollections;
	private boolean isFiltering;
	private @Nullable ImageButton forwardButton;
	private @Nullable ImageButton backButton;
	private @Nullable BaseRecipeButton hoveredButton;
	private @Nullable RecipeDisplayId lastClickedRecipe;
	private @Nullable RecipeCollection resultCollection;
	private int totalPages;
	private int currentPage;
	protected int resultCellSize = 25;
	protected int resultGridTopMargin = 31;
	protected int resultGridLeftMargin = 11;

	public BaseRecipeBookPage(RecipeBookComponent<?> recipeBookWidget, SlotSelectTime currentIndexProvider) {
		this.recipeBookWidget = recipeBookWidget;
		this.altWidget = new OverlayRecipeComponent(currentIndexProvider, false);

		for (int i = 0; i < this.getSlotCount(); ++i) {
			this.buttons.add(new BaseRecipeButton(currentIndexProvider));
		}
	}

	public void init(Minecraft client, int parentLeft, int parentTop) {
		if (client.player == null) {
			throw new NullPointerException("Client player is null. Ensure that the client is fully initialized before calling this method.");
		}

		this.client = client;
		this.recipeBook = client.player.getRecipeBook();

		// Initializes the recipe results in the GUI.
		for (int i = 0; i < this.buttons.size(); ++i) {
			this.buttons.get(i).setPosition(
				parentLeft + this.resultGridLeftMargin + this.resultCellSize * (i % this.getResultColCount()),
				parentTop + this.resultGridTopMargin + this.resultCellSize *  (i / this.getResultColCount())
			);
		}

		this.backButton = this.getPrevPageBtn(parentLeft, parentTop);
		this.forwardButton = this.getNextPageBtn(parentLeft, parentTop);

		boolean prevNull = this.backButton == null;
		boolean nextNull = this.forwardButton == null;
		if (prevNull || nextNull) {
			boolean bothNull = prevNull && nextNull;
			String exceptionMsg = "The " +
				(prevNull ? "previous button" : "") +
				(bothNull ? " and " : "") +
				(nextNull ? "next button" : "") +
				(bothNull ? " are " : " is ") +
				"null.";

			throw new NullPointerException(exceptionMsg);
		}

		this.backButton.setTooltip(Tooltip.create(this.getPrevPageTooltip()));
		this.forwardButton.setTooltip(Tooltip.create(this.getNextPageTooltip()));
	}

	public void updateCollections(List<RecipeCollection> resultCollections, boolean resetPage, boolean isFiltering) {
		this.resultCollections = resultCollections;
		this.isFiltering = isFiltering;
		this.totalPages = (int) Math.ceil((double) resultCollections.size() / (double) this.getSlotCount());

		if (this.totalPages <= this.currentPage || resetPage) {
			this.currentPage = 0;
		}

		this.updateButtonsForPage();
	}

	protected void updateButtonsForPage() {
		int startOffset = this.getSlotCount() * this.currentPage;
		ContextMap ctx = SlotDisplayContext.fromLevel(this.client.level);

		for (int i = 0; i < this.buttons.size(); ++i) {
			BaseRecipeButton btn = this.buttons.get(i);

			if (startOffset + i < this.resultCollections.size()) {
				RecipeCollection recipeCollection = this.resultCollections.get(startOffset + i);

				btn.init(recipeCollection, this.isFiltering, this, ctx);
				btn.visible = true;
			}
			else {
				btn.visible = false;
			}
		}

		this.updateArrowButtons();
	}

	protected final int getSlotCount() {
		return this.getResultColCount() * this.getResultRowCount();
	}

	protected void updateArrowButtons() {
		if (this.backButton != null) {
			this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
		}

		if (this.forwardButton != null) {
			this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
		}
	}

	public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, float deltaTicks) {
		if (this.totalPages > 1) {
			Component text = Component.translatable("gui.recipebook.page", this.currentPage + 1, this.totalPages);
			int pWidth = this.client.font.width(text);
			graphics.text(this.client.font, text, x - pWidth / 2 + 73, y + 141, -1);
		}

		this.hoveredButton = null;

		for(BaseRecipeButton recipeBookButton : this.buttons) {
			recipeBookButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

			if (recipeBookButton.visible && recipeBookButton.isHoveredOrFocused()) {
				this.hoveredButton = recipeBookButton;
			}
		}

		if (this.backButton != null) {
			this.backButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		}

		if (this.forwardButton != null) {
			this.forwardButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		}

		graphics.nextStratum();
		this.altWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
	}

	public void extractTooltip(GuiGraphicsExtractor context, int x, int y) {
		if (this.client.screen != null &&
			this.hoveredButton != null &&
			!this.altWidget.isVisible()) {
			ItemStack itemStack = this.hoveredButton.getDisplayStack();
			Identifier identifier = itemStack.get(DataComponents.TOOLTIP_STYLE);

			context.setComponentTooltipForNextFrame(
				this.client.font,
				this.hoveredButton.getTooltip(itemStack),
				x, y, identifier
			);
		}
	}

	public @Nullable RecipeDisplayId getLastClickedRecipe() {
		return this.lastClickedRecipe;
	}

	public @Nullable RecipeCollection getLastClickedRecipeCollection() {
		return this.resultCollection;
	}

	public void hideAlternates() {
		this.altWidget.setVisible(false);
	}

	public boolean mouseClicked(MouseButtonEvent click, int left, int top, int width, int height, boolean doubled) {
		Objects.requireNonNull(this.backButton);
		Objects.requireNonNull(this.forwardButton);

		this.lastClickedRecipe = null;
		this.resultCollection = null;

		if (this.altWidget.isVisible()) {
			if (this.altWidget.mouseClicked(click, doubled)) {
				this.lastClickedRecipe = this.altWidget.getLastRecipeClicked();
				this.resultCollection = this.altWidget.getRecipeCollection();
			} else {
				this.altWidget.setVisible(false);
			}

			return true;
		} else if (this.forwardButton.mouseClicked(click, doubled)) {
			++this.currentPage;
			this.updateButtonsForPage();
			return true;
		} else if (this.backButton.mouseClicked(click, doubled)) {
			--this.currentPage;
			this.updateButtonsForPage();
			return true;
		} else {
			Objects.requireNonNull(this.client.level, "Client world is null. Ensure that the client is fully initialized before calling this method.");

			ContextMap contextParameterMap = SlotDisplayContext.fromLevel(this.client.level);

			for(BaseRecipeButton animatedResultButton : this.buttons) {
				if (animatedResultButton.mouseClicked(click, doubled)) {
					if (click.button() == 0) {
						this.lastClickedRecipe = animatedResultButton.getCurrentId();
						this.resultCollection = animatedResultButton.getCollection();
					} else if (click.button() == 1 && !this.altWidget.isVisible() && !animatedResultButton.hasSingleResult()) {
						this.altWidget.init(animatedResultButton.getCollection(), contextParameterMap, this.isFiltering, animatedResultButton.getX(), animatedResultButton.getY(), left + width / 2, top + 13 + height / 2, (float)animatedResultButton.getWidth());
					}

					return true;
				}
			}

			return false;
		}
	}

	public void recipeShown(RecipeDisplayId recipeId) {
		this.recipeBookWidget.recipeShown(recipeId);
	}

	public ClientRecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	protected void forEachButton(Consumer<AbstractWidget> action) {
		this.buttons.forEach(action);
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //
	protected int getResultRowCount() {
		return 4;
	}

	protected int getResultColCount() {
		return 5;
	}

	protected Component getPrevPageTooltip() {
		return Component.translatable("gui.recipebook.previous_page");
	}

	protected Component getNextPageTooltip() {
		return Component.translatable("gui.recipebook.next_page");
	}

	protected ImageButton getPrevPageBtn(int parentLeft, int parentTop) {
		return new ImageButton(
			parentLeft + 38, parentTop + 137, 12, 17,
			PAGE_BACKWARD_TEXTURES,
			(buttonWidget) -> this.updateArrowButtons(),
			this.getPrevPageTooltip()
		);
	}

	protected ImageButton getNextPageBtn(int parentLeft, int parentTop) {
		return new ImageButton(
			parentLeft + 93, parentTop + 137, 12, 17,
			PAGE_FORWARD_TEXTURES,
			(buttonWidget) -> this.updateArrowButtons(),
			this.getNextPageTooltip()
		);
	}
}
