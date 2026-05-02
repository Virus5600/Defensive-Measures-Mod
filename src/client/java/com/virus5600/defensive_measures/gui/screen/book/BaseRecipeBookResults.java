package com.virus5600.defensive_measures.gui.screen.book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.text.Text;

import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class BaseRecipeBookResults {
	protected static final ButtonTextures PAGE_BACKWARD_TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/page_backward"), Identifier.ofVanilla("recipe_book/page_backward_highlighted"));
	protected static final ButtonTextures PAGE_FORWARD_TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/page_forward"), Identifier.ofVanilla("recipe_book/page_forward_highlighted"));

	private final BaseRecipeBookWidget<?> recipeBookWidget;
	private final RecipeAlternativesWidget altWidget;
	private final List<BaseAnimatedResultButton> resultsButtons = Lists.newArrayListWithCapacity(20);

	private MinecraftClient client;
	private ClientRecipeBook recipeBook;
	private List<RecipeResultCollection> resultCollections;
	private boolean filteringCraftable;
	private @Nullable TexturedButtonWidget nextPageBtn;
	private @Nullable TexturedButtonWidget prevPageBtn;
	private @Nullable BaseAnimatedResultButton hoveredResultButton;
	private @Nullable NetworkRecipeId lastClickedRecipe;
	private @Nullable RecipeResultCollection resultCollection;
	private int pageCount;
	private int currentPage;
	protected int resultCellSize = 25;
	protected int resultGridTopMargin = 31;
	protected int resultGridLeftMargin = 11;

	public BaseRecipeBookResults(BaseRecipeBookWidget<?> recipeBookWidget, CurrentIndexProvider currentIndexProvider) {
		this.recipeBookWidget = recipeBookWidget;
		this.altWidget = new RecipeAlternativesWidget(currentIndexProvider, false);

		for (int i = 0; i < this.getSlotCount(); ++i) {
			this.resultsButtons.add(new BaseAnimatedResultButton(currentIndexProvider));
		}
	}

	public void initialize(MinecraftClient client, int parentLeft, int parentTop) {
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

		this.prevPageBtn.setTooltip(Tooltip.of(this.getPrevPageTooltip()));
		this.nextPageBtn.setTooltip(Tooltip.of(this.getNextPageTooltip()));
	}

	public void setResults(List<RecipeResultCollection> resultCollections, boolean resetCurrentPage, boolean filteringCraftable) {
		this.resultCollections = resultCollections;
		this.filteringCraftable = filteringCraftable;
		this.pageCount = (int) Math.ceil((double) resultCollections.size() / (double) this.getSlotCount());

		if (this.pageCount <= this.currentPage || resetCurrentPage) {
			this.currentPage = 0;
		}

		this.refreshResultButtons();
	}

	protected void refreshResultButtons() {
		if (this.client.world == null) {
			throw new NullPointerException("Client world is null. Ensure that the client is fully initialized before calling this method.");
		}

		int i = this.getSlotCount() * this.currentPage;
		ContextParameterMap cpm = SlotDisplayContexts.createParameters(this.client.world);

		for (int j = 0; j < this.resultsButtons.size(); j++) {
			BaseAnimatedResultButton btn = this.resultsButtons.get(j);

			if (i + j < this.resultCollections.size()) {
				RecipeResultCollection rrc = this.resultCollections.get(i + j);

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

	public void draw(DrawContext context, int x, int y, int mouseX, int mouseY, float deltaTicks) {
		if (this.pageCount > 1) {
			Text text = Text.translatable("gui.recipebook.page", this.currentPage + 1, this.pageCount);
			int i = this.client.textRenderer.getWidth(text);
			context.drawTextWithShadow(this.client.textRenderer, text, x - i / 2 + 73, y + 141, -1);
		}

		this.hoveredResultButton = null;

		for(BaseAnimatedResultButton animatedResultButton : this.resultsButtons) {
			animatedResultButton.render(context, mouseX, mouseY, deltaTicks);

			if (animatedResultButton.visible && animatedResultButton.isSelected()) {
				this.hoveredResultButton = animatedResultButton;
			}
		}

		if (this.prevPageBtn != null) {
			this.prevPageBtn.render(context, mouseX, mouseY, deltaTicks);
		}

		if (this.nextPageBtn != null) {
			this.nextPageBtn.render(context, mouseX, mouseY, deltaTicks);
		}

		context.createNewRootLayer();
		this.altWidget.render(context, mouseX, mouseY, deltaTicks);
	}

	public void drawTooltip(DrawContext context, int x, int y) {
		if (this.client.currentScreen != null &&
			this.hoveredResultButton != null &&
			!this.altWidget.isVisible()) {
			ItemStack itemStack = this.hoveredResultButton.getDisplayStack();
			Identifier identifier = itemStack.get(DataComponentTypes.TOOLTIP_STYLE);

			context.drawTooltip(
				this.client.textRenderer,
				this.hoveredResultButton.getTooltip(itemStack),
				x, y, identifier
			);
		}
	}

	public @Nullable NetworkRecipeId getLastClickedRecipe() {
		return this.lastClickedRecipe;
	}

	public @Nullable RecipeResultCollection getLastClickedResults() {
		return this.resultCollection;
	}

	public void hideAlternates() {
		this.altWidget.setVisible(false);
	}

	public boolean mouseClicked(Click click, int left, int top, int width, int height, boolean doubled) {
		Objects.requireNonNull(this.prevPageBtn);
		Objects.requireNonNull(this.nextPageBtn);

		this.lastClickedRecipe = null;
		this.resultCollection = null;

		if (this.altWidget.isVisible()) {
			if (this.altWidget.mouseClicked(click, doubled)) {
				this.lastClickedRecipe = this.altWidget.getLastClickedRecipe();
				this.resultCollection = this.altWidget.getResults();
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
			Objects.requireNonNull(this.client.world, "Client world is null. Ensure that the client is fully initialized before calling this method.");

			ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(this.client.world);

			for(BaseAnimatedResultButton animatedResultButton : this.resultsButtons) {
				if (animatedResultButton.mouseClicked(click, doubled)) {
					if (click.button() == 0) {
						this.lastClickedRecipe = animatedResultButton.getCurrentId();
						this.resultCollection = animatedResultButton.getResultCollection();
					} else if (click.button() == 1 && !this.altWidget.isVisible() && !animatedResultButton.hasSingleResult()) {
						this.altWidget.showAlternativesForResult(animatedResultButton.getResultCollection(), contextParameterMap, this.filteringCraftable, animatedResultButton.getX(), animatedResultButton.getY(), left + width / 2, top + 13 + height / 2, (float)animatedResultButton.getWidth());
					}

					return true;
				}
			}

			return false;
		}
	}

	public void onRecipeDisplayed(NetworkRecipeId recipeId) {
		this.recipeBookWidget.onRecipeDisplayed(recipeId);
	}

	public ClientRecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	protected void forEachButton(Consumer<ClickableWidget> action) {
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

	protected Text getPrevPageTooltip() {
		return Text.translatable("gui.recipebook.previous_page");
	}

	protected Text getNextPageTooltip() {
		return Text.translatable("gui.recipebook.next_page");
	}

	protected TexturedButtonWidget getPrevPageBtn(int parentLeft, int parentTop) {
		return new TexturedButtonWidget(
			parentLeft + 38, parentTop + 137, 12, 17,
			PAGE_BACKWARD_TEXTURES,
			(buttonWidget) -> this.hideShowPageButtons(),
			this.getPrevPageTooltip()
		);
	}

	protected TexturedButtonWidget getNextPageBtn(int parentLeft, int parentTop) {
		return new TexturedButtonWidget(
			parentLeft + 93, parentTop + 137, 12, 17,
			PAGE_FORWARD_TEXTURES,
			(buttonWidget) -> this.hideShowPageButtons(),
			this.getNextPageTooltip()
		);
	}
}
