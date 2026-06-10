package com.virus5600.defensive_measures.gui.screen.book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
public class BaseRecipeBookResults {
	protected static final WidgetSprites PAGE_BACKWARD_TEXTURES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/page_backward"), Identifier.withDefaultNamespace("recipe_book/page_backward_highlighted"));
	protected static final WidgetSprites PAGE_FORWARD_TEXTURES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/page_forward"), Identifier.withDefaultNamespace("recipe_book/page_forward_highlighted"));

	private final BaseRecipeBookWidget<?> recipeBookWidget;
	private final OverlayRecipeComponent altWidget;
	private final List<BaseAnimatedResultButton> resultsButtons = Lists.newArrayListWithCapacity(20);

	private Minecraft client;
	private ClientRecipeBook recipeBook;
	private List<RecipeCollection> resultCollections;
	private boolean filteringCraftable;
	private @Nullable ImageButton nextPageBtn;
	private @Nullable ImageButton prevPageBtn;
	private @Nullable BaseAnimatedResultButton hoveredResultButton;
	private @Nullable RecipeDisplayId lastClickedRecipe;
	private @Nullable RecipeCollection resultCollection;
	private int pageCount;
	private int currentPage;
	protected int resultCellSize = 25;
	protected int resultGridTopMargin = 31;
	protected int resultGridLeftMargin = 11;

	public BaseRecipeBookResults(BaseRecipeBookWidget<?> recipeBookWidget, SlotSelectTime currentIndexProvider) {
		this.recipeBookWidget = recipeBookWidget;
		this.altWidget = new OverlayRecipeComponent(currentIndexProvider, false);

		for (int i = 0; i < this.getSlotCount(); ++i) {
			this.resultsButtons.add(new BaseAnimatedResultButton(currentIndexProvider));
		}
	}

	public void initialize(Minecraft client, int parentLeft, int parentTop) {
		if (client.player == null) {
			throw new NullPointerException("Client player is null. Ensure that the client is fully initialized before calling this method.");
		}

		this.client = client;
		this.recipeBook = client.player.getRecipeBook();

		// Initializes the recipe results in the GUI.
		for (int i = 0; i < this.resultsButtons.size(); ++i) {
			this.resultsButtons.get(i).setPosition(
				parentLeft + this.resultGridLeftMargin + this.resultCellSize * (i % this.getResultColCount()),
				parentTop + this.resultGridTopMargin + this.resultCellSize *  (i / this.getResultColCount())
			);
		}

		this.prevPageBtn = this.getPrevPageBtn(parentLeft, parentTop);
		this.nextPageBtn = this.getNextPageBtn(parentLeft, parentTop);

		boolean prevNull = this.prevPageBtn == null;
		boolean nextNull = this.nextPageBtn == null;
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

		this.prevPageBtn.setTooltip(Tooltip.create(this.getPrevPageTooltip()));
		this.nextPageBtn.setTooltip(Tooltip.create(this.getNextPageTooltip()));
	}

	public void setResults(List<RecipeCollection> resultCollections, boolean resetCurrentPage, boolean filteringCraftable) {
		this.resultCollections = resultCollections;
		this.filteringCraftable = filteringCraftable;
		this.pageCount = (int) Math.ceil((double) resultCollections.size() / (double) this.getSlotCount());

		if (this.pageCount <= this.currentPage || resetCurrentPage) {
			this.currentPage = 0;
		}

		this.refreshResultButtons();
	}

	protected void refreshResultButtons() {
		if (this.client.level == null) {
			throw new NullPointerException("Client world is null. Ensure that the client is fully initialized before calling this method.");
		}

		int i = this.getSlotCount() * this.currentPage;
		ContextMap cpm = SlotDisplayContext.fromLevel(this.client.level);

		for (int j = 0; j < this.resultsButtons.size(); j++) {
			BaseAnimatedResultButton btn = this.resultsButtons.get(j);

			if (i + j < this.resultCollections.size()) {
				RecipeCollection rrc = this.resultCollections.get(i + j);

				btn.showResultCollection(rrc, this.filteringCraftable, this, cpm);
				btn.visible = true;
			}
			else {
				btn.visible = false;
			}
		}

		this.hideShowPageButtons();
	}

	protected final int getSlotCount() {
		return this.getResultColCount() * this.getResultRowCount();
	}

	protected void hideShowPageButtons() {
		if (this.prevPageBtn != null) {
			this.prevPageBtn.visible = this.pageCount > 1 && this.currentPage > 0;
		}

		if (this.nextPageBtn != null) {
			this.nextPageBtn.visible = this.pageCount > 1 && this.currentPage < this.pageCount - 1;
		}
	}

	public void draw(GuiGraphics context, int x, int y, int mouseX, int mouseY, float deltaTicks) {
		if (this.pageCount > 1) {
			Component text = Component.translatable("gui.recipebook.page", this.currentPage + 1, this.pageCount);
			int i = this.client.font.width(text);
			context.drawString(this.client.font, text, x - i / 2 + 73, y + 141, -1);
		}

		this.hoveredResultButton = null;

		for(BaseAnimatedResultButton animatedResultButton : this.resultsButtons) {
			animatedResultButton.render(context, mouseX, mouseY, deltaTicks);

			if (animatedResultButton.visible && animatedResultButton.isHoveredOrFocused()) {
				this.hoveredResultButton = animatedResultButton;
			}
		}

		if (this.prevPageBtn != null) {
			this.prevPageBtn.render(context, mouseX, mouseY, deltaTicks);
		}

		if (this.nextPageBtn != null) {
			this.nextPageBtn.render(context, mouseX, mouseY, deltaTicks);
		}

		context.nextStratum();
		this.altWidget.render(context, mouseX, mouseY, deltaTicks);
	}

	public void drawTooltip(GuiGraphics context, int x, int y) {
		if (this.client.screen != null &&
			this.hoveredResultButton != null &&
			!this.altWidget.isVisible()) {
			ItemStack itemStack = this.hoveredResultButton.getDisplayStack();
			Identifier identifier = itemStack.get(DataComponents.TOOLTIP_STYLE);

			context.setComponentTooltipForNextFrame(
				this.client.font,
				this.hoveredResultButton.getTooltip(itemStack),
				x, y, identifier
			);
		}
	}

	public @Nullable RecipeDisplayId getLastClickedRecipe() {
		return this.lastClickedRecipe;
	}

	public @Nullable RecipeCollection getLastClickedResults() {
		return this.resultCollection;
	}

	public void hideAlternates() {
		this.altWidget.setVisible(false);
	}

	public boolean mouseClicked(MouseButtonEvent click, int left, int top, int width, int height, boolean doubled) {
		Objects.requireNonNull(this.prevPageBtn);
		Objects.requireNonNull(this.nextPageBtn);

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
		} else if (this.nextPageBtn.mouseClicked(click, doubled)) {
			++this.currentPage;
			this.refreshResultButtons();
			return true;
		} else if (this.prevPageBtn.mouseClicked(click, doubled)) {
			--this.currentPage;
			this.refreshResultButtons();
			return true;
		} else {
			Objects.requireNonNull(this.client.level, "Client world is null. Ensure that the client is fully initialized before calling this method.");

			ContextMap contextParameterMap = SlotDisplayContext.fromLevel(this.client.level);

			for(BaseAnimatedResultButton animatedResultButton : this.resultsButtons) {
				if (animatedResultButton.mouseClicked(click, doubled)) {
					if (click.button() == 0) {
						this.lastClickedRecipe = animatedResultButton.getCurrentId();
						this.resultCollection = animatedResultButton.getResultCollection();
					} else if (click.button() == 1 && !this.altWidget.isVisible() && !animatedResultButton.hasSingleResult()) {
						this.altWidget.init(animatedResultButton.getResultCollection(), contextParameterMap, this.filteringCraftable, animatedResultButton.getX(), animatedResultButton.getY(), left + width / 2, top + 13 + height / 2, (float)animatedResultButton.getWidth());
					}

					return true;
				}
			}

			return false;
		}
	}

	public void onRecipeDisplayed(RecipeDisplayId recipeId) {
		this.recipeBookWidget.onRecipeDisplayed(recipeId);
	}

	public ClientRecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	protected void forEachButton(Consumer<AbstractWidget> action) {
		this.resultsButtons.forEach(action);
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
			(buttonWidget) -> this.hideShowPageButtons(),
			this.getPrevPageTooltip()
		);
	}

	protected ImageButton getNextPageBtn(int parentLeft, int parentTop) {
		return new ImageButton(
			parentLeft + 93, parentTop + 137, 12, 17,
			PAGE_FORWARD_TEXTURES,
			(buttonWidget) -> this.hideShowPageButtons(),
			this.getNextPageTooltip()
		);
	}
}
