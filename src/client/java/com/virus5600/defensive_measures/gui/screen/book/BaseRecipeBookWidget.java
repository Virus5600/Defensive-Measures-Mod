package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.recipebook.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;

import com.virus5600.defensive_measures.recipebook.ModRecipeBookType;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.awt.Dimension;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

/**
 * Base class for book-related widgets, mirroring the vanilla {@link net.minecraft.client.gui.screen.recipebook.RecipeBookWidget}.
 * Implements the core interfaces for rendering, input handling, and selection.
 *
 * @param <T> the screen handler type
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.0.0
 */
public abstract class BaseRecipeBookWidget<T extends AbstractRecipeScreenHandler> implements Drawable, Element, Selectable {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/recipe_book.png");
	private static final Text SEARCH_HINT_TEXT = Text.translatable("gui.recipebook.search_hint").fillStyle(TextFieldWidget.SEARCH_STYLE);
	private static final Text TOGGLE_ALL_RECIPES_TEXT = Text.translatable("gui.recipebook.toggleRecipes.all");

	private final Dimension dimension = new Dimension(256, 256);
	private final Dimension uvSize = new Dimension(147, 166);
	private final GhostRecipe ghostRecipe;
	private final List<BaseRecipeGroupButtonWidget> tabButtons = Lists.newArrayList();
	private final List<Tab> tabs;
	private final BaseRecipeBookResults recipesArea;
	private final RecipeFinder recipeFinder = new RecipeFinder();
	protected final T screenHandler;

	private @Nullable NetworkRecipeId selectedRecipeId;
	private @Nullable BaseRecipeGroupButtonWidget currentTab;
	private @Nullable TextFieldWidget searchField;
	private @Nullable ScreenRect searchFieldRect;
	private @Nullable NetworkRecipeId selectedRecipe;
	private @Nullable RecipeResultCollection selectedRecipeResults;
	private @Nullable MinecraftClient client;
	private ClientRecipeBook recipeBook;
	private String searchText = "";
	private int cachedInvChangeCount;
	private boolean searching;
	protected CyclingButtonWidget<Boolean> toggleCraftableButton;
	protected int parentWidth;
	protected int parentHeight;
	protected int leftOffset;
	protected boolean open;
	protected boolean narrow;
	protected float displayTime;

	public BaseRecipeBookWidget(T screenHandler, List<Tab> tabs) {
		this.screenHandler = screenHandler;
		this.tabs = tabs;

		CurrentIndexProvider cip = () -> MathHelper.floor(this.displayTime / 30.0F);

		this.ghostRecipe = new GhostRecipe(cip);
		this.recipesArea = new BaseRecipeBookResults(this, cip);
	}

	public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow) {
		this.client = client;
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		this.narrow = narrow;
		this.recipeBook = client.player.getRecipeBook();
		this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
		this.open = this.isGuiOpen();

		if (this.open) {
			this.reset();
		}
	}

	protected void reset() {
		boolean isFilteringCraftable = this.isFilteringCraftable();
		this.leftOffset = this.narrow ? 0 : this.getLeftOffset();
		int x = this.getLeft();
		int y = this.getTop();

		this.recipeFinder.clear();
		this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);

		this.screenHandler.populateRecipeFinder(this.recipeFinder);

		String searchFldTxt = this.searchField != null ? this.searchField.getText() : "";
		this.searchField = this.buildNewSearchField(81, 9 + 5, null);
		this.searchField.setMaxLength(this.getMaxSearchLength());
		this.searchField.setVisible(true);
		this.searchField.setEditableColor(-1);
		this.searchField.setText(searchFldTxt);
		this.searchField.setPlaceholder(this.getSearchHintText());
		this.searchFieldRect = ScreenRect.of(NavigationAxis.HORIZONTAL, x + 8, this.searchField.getY(), this.searchField.getX() - this.getLeft(), this.searchField.getHeight());

		this.recipesArea.initialize(this.client, x, y);

		this.toggleCraftableButton = CyclingButtonWidget.onOffBuilder(
				this.getToggleCraftableButtonText(),
				this.getToggleAllRecipesText(),
				isFilteringCraftable
			)
			.tooltip((value) -> value ?
				Tooltip.of(this.getToggleCraftableButtonText()) :
				Tooltip.of(this.getToggleAllRecipesText())
			)
			.icon((button, value) ->
				this.getBookButtonTextures()
					.get(value, button.isSelected())
			)
			.labelType(CyclingButtonWidget.LabelType.HIDE)
			.build(x + 110, y + 12, 26, 16,
				ScreenTexts.EMPTY,
				(button, value) -> {
					this.toggleFilteringCraftable();
					this.sendBookDataPacket();
					this.refreshResults(false, value);
				}
			);
		this.tabButtons.clear();

		for (Tab tab : this.tabs) {
			this.tabButtons.add(new BaseRecipeGroupButtonWidget(0, 0, tab, this::onTabSelected));
		}

		// Sets the selected tab. Use the first if none.
		if (this.currentTab == null) {
			this.currentTab = this.tabButtons.getFirst();
		}
		// Otherwise, try to find the same tab as before, since the recipe book doesn't track the current tab.
		else {
			this.currentTab = this.tabButtons.stream()
				.filter((button) -> button.getCategory().equals(this.currentTab.getCategory()))
				.findFirst()
				.orElse(null);
		}
		this.currentTab.focus();

		this.populateAllRecipes();
		this.refreshTabButtons(isFilteringCraftable);
		this.refreshResults(false, isFilteringCraftable);
	}

	private int getTop() {
		return (this.parentHeight - this.getUVSize().height) / 2;
	}

	private int getLeft() {
		return (this.parentWidth - this.getUVSize().width) / 2 - this.leftOffset;
	}

	public int findLeftEdge(int width, int backgroundWidth) {
		int i;
		if (this.isOpen() && !this.narrow) {
			i = 177 + (width - backgroundWidth - 200) / 2;
		} else {
			i = (width - backgroundWidth) / 2;
		}

		return i;
	}

	public void toggleOpen() {
		this.setOpen(!this.isOpen());
	}

	public boolean isOpen() {
		return this.open;
	}

	private boolean isGuiOpen() {
		return this.recipeBook.isGuiOpen(this.screenHandler.getCategory());
	}

	protected void setOpen(boolean opened) {
		if (opened) {
			this.reset();
		}

		this.open = opened;
		this.recipeBook.setGuiOpen(this.screenHandler.getCategory(), opened);
		if (!opened) {
			this.recipesArea.hideAlternates();
		}

		this.sendBookDataPacket();
	}

	public void onMouseClick(@Nullable Slot slot) {
		if (slot != null && this.isCraftingSlot(slot)) {
			this.selectedRecipeId = null;
			this.ghostRecipe.clear();
			if (this.isOpen()) {
				this.refreshInputs();
			}
		}
	}

	private void populateAllRecipes() {
		for (Tab tab : this.tabs) {
			RecipeBookGroup category = tab.category();

			if (category instanceof ModRecipeBookType modRecipeBookType) {
				for (RecipeBookCategory recipeCategory : modRecipeBookType.getCategories()) {
					for (RecipeResultCollection recipeResultCollection : this.recipeBook.getResultsForCategory(recipeCategory)) {
						this.populateRecipes(recipeResultCollection, this.recipeFinder);
					}
				}
			} else {
				for (RecipeResultCollection recipeResultCollection : this.recipeBook.getResultsForCategory(category)) {
					this.populateRecipes(recipeResultCollection, this.recipeFinder);
				}
			}
		}
	}

	protected abstract void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder);

	private void refreshResults(boolean resetCurrentPage, boolean filteringCraftable) {
		List<RecipeResultCollection> categoryResults = Lists.newArrayList();

		// Handle ModRecipeBookType (All tab) specially to include all categories
		RecipeBookGroup category = this.currentTab.getCategory();
		if (category instanceof ModRecipeBookType modRecipeBookType) {
			for (RecipeBookCategory recipeCategory : modRecipeBookType.getCategories()) {
				categoryResults.addAll(this.recipeBook.getResultsForCategory(recipeCategory));
			}
		} else {
			categoryResults.addAll(this.recipeBook.getResultsForCategory(category));
		}

		List<RecipeResultCollection> categoryResultsDupe = Lists.newArrayList(categoryResults);
		categoryResultsDupe.removeIf((resultCollection) -> !resultCollection.hasDisplayableRecipes());

		String searchTxt = this.searchField.getText();
		if (!searchTxt.isEmpty()) {
			ClientPlayNetworkHandler clientPNH = this.client.getNetworkHandler();

			if (clientPNH != null) {
				ObjectSet<RecipeResultCollection> objectSet = new ObjectLinkedOpenHashSet<>(
					clientPNH.getSearchManager()
						.getRecipeOutputReloadFuture()
						.findAll(searchTxt.toLowerCase(Locale.ROOT))
				);

				categoryResultsDupe.removeIf(
					(resultCollection) -> !objectSet.contains(resultCollection)
				);
			}
		}

		if (filteringCraftable) {
			categoryResultsDupe.removeIf((resultCollection) -> !resultCollection.hasCraftableRecipes());
		}

		this.recipesArea.setResults(categoryResultsDupe, resetCurrentPage, filteringCraftable);
	}

	private void refreshTabButtons(boolean filteringCraftable) {
		int i = (this.parentWidth - this.getUVSize().width) / 2 - this.leftOffset - 30;
		int j = (this.parentHeight - this.getUVSize().height) / 2 + 3;
		int k = 27;
		int l = 0;

		for (BaseRecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
			RecipeBookGroup recipeBookGroup = recipeGroupButtonWidget.getCategory();

			if (recipeBookGroup instanceof ModRecipeBookType) {
				recipeGroupButtonWidget.visible = true;
				recipeGroupButtonWidget.setPosition(i, j + k * l++);
			}
			else if (recipeGroupButtonWidget.hasKnownRecipes(this.recipeBook)) {
				recipeGroupButtonWidget.setPosition(i, j + k * l++);
				recipeGroupButtonWidget.checkForNewRecipes(this.recipeBook, filteringCraftable);
			}
		}
	}

	public void update() {
		boolean bl = this.isGuiOpen();
		if (this.isOpen() != bl) {
			this.setOpen(bl);
		}

		if (this.isOpen()) {
			if (this.cachedInvChangeCount != this.client.player.getInventory().getChangeCount()) {
				this.refreshInputs();
				this.cachedInvChangeCount = this.client.player.getInventory().getChangeCount();
			}
		}
	}

	private void refreshInputs() {
		this.recipeFinder.clear();
		this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
		this.screenHandler.populateRecipeFinder(this.recipeFinder);
		this.populateAllRecipes();
		this.refreshResults(false, this.isFilteringCraftable());
	}

	private boolean isFilteringCraftable() {
		return this.recipeBook.isFilteringCraftable(this.screenHandler.getCategory());
	}

	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.isOpen()) {
			if (!this.client.isCtrlPressed()) {
				this.displayTime += deltaTicks;
			}

			int i = this.getLeft();
			int j = this.getTop();

			Dimension uvDimension = this.getUVSize();
			Dimension textureDimension = this.getTextureSize();

			context.drawTexture(
				RenderPipelines.GUI_TEXTURED, this.getUITexture(),
				i, j, 1.0F, 1.0F,
				uvDimension.width, uvDimension.height,
				textureDimension.width, textureDimension.height
			);

			this.searchField.render(context, mouseX, mouseY, deltaTicks);

			for (BaseRecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
				recipeGroupButtonWidget.render(context, mouseX, mouseY, deltaTicks);
			}

			this.toggleCraftableButton.render(context, mouseX, mouseY, deltaTicks);
			this.recipesArea.draw(context, i, j, mouseX, mouseY, deltaTicks);
		}
	}

	public void drawTooltip(DrawContext context, int x, int y, @Nullable Slot slot) {
		if (this.isOpen()) {
			this.recipesArea.drawTooltip(context, x, y);
			this.ghostRecipe.drawTooltip(context, this.client, x, y, slot);
		}
	}

	protected abstract Text getToggleCraftableButtonText();

	public void drawGhostSlots(DrawContext context, boolean resultHasPadding) {
		this.ghostRecipe.draw(context, this.client, resultHasPadding);
	}

	public boolean mouseClicked(Click click, boolean doubled) {
		if (this.isOpen() && !this.client.player.isSpectator()) {
			Dimension uvDimension = this.getUVSize();
			boolean recipesAreaClicked = this.recipesArea
				.mouseClicked(click,
					this.getLeft(), this.getTop(),
					uvDimension.width, uvDimension.height,
					doubled);

			if (recipesAreaClicked) {
				NetworkRecipeId networkRecipeId = this.recipesArea.getLastClickedRecipe();
				RecipeResultCollection recipeResultCollection = this.recipesArea.getLastClickedResults();
				if (networkRecipeId != null && recipeResultCollection != null) {
					if (!this.select(recipeResultCollection, networkRecipeId, click.hasShift())) {
						return false;
					}

					this.selectedRecipeResults = recipeResultCollection;
					this.selectedRecipe = networkRecipeId;

					if (!this.isWide()) {
						this.setOpen(false);
					}
				}

				return true;
			} else {
				if (this.searchField != null) {
					boolean isClicked = this.searchFieldRect != null && this.searchFieldRect.contains(MathHelper.floor(click.x()), MathHelper.floor(click.y()));
					if (isClicked || this.searchField.mouseClicked(click, doubled)) {
						this.searchField.setFocused(true);
						return true;
					}

					this.searchField.setFocused(false);
				}

				if (this.toggleCraftableButton.mouseClicked(click, doubled)) {
					return true;
				} else {
					for (BaseRecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
						if (recipeGroupButtonWidget.mouseClicked(click, doubled)) {
							return true;
						}
					}

					return false;
				}
			}
		} else {
			return false;
		}
	}

	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		return this.searchField != null && this.searchField.isFocused() && this.searchField.mouseDragged(click, offsetX, offsetY);
	}

	private boolean select(RecipeResultCollection results, NetworkRecipeId recipeId, boolean craftAll) {
		if (!results.isCraftable(recipeId) && recipeId.equals(this.selectedRecipeId)) {
			return false;
		} else {
			this.selectedRecipeId = recipeId;
			this.ghostRecipe.clear();
			this.client.interactionManager.clickRecipe(this.client.player.currentScreenHandler.syncId, recipeId, craftAll);
			return true;
		}
	}

	private void onTabSelected(ButtonWidget button) {
		if (this.currentTab != button && button instanceof BaseRecipeGroupButtonWidget recipeGroupButtonWidget) {
			this.setCurrentTab(recipeGroupButtonWidget);
			this.refreshResults(true, this.isFilteringCraftable());
		}

	}

	private void setCurrentTab(BaseRecipeGroupButtonWidget currentTab) {
		if (this.currentTab != null) {
			this.currentTab.unfocus();
		}

		currentTab.focus();
		this.currentTab = currentTab;
	}

	private void toggleFilteringCraftable() {
		RecipeBookType recipeBookType = this.screenHandler.getCategory();
		boolean bl = !this.recipeBook.isFilteringCraftable(recipeBookType);
		this.recipeBook.setFilteringCraftable(recipeBookType, bl);
	}

	public boolean isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int backgroundWidth, int backgroundHeight) {
		if (!this.isOpen()) {
			return true;
		} else {
			boolean isOutsideBackground = mouseX < (double) x ||
				mouseY < (double) y ||
				mouseX >= (double) (x + backgroundWidth) ||
				mouseY >= (double) (y + backgroundHeight);

			boolean isInsideBackground = (double) (x - this.getUVSize().width) < mouseX &&
				mouseX < (double) x &&
				(double) y < mouseY &&
				mouseY < (double) (y + backgroundHeight);

			return isOutsideBackground && !isInsideBackground && !this.currentTab.isSelected();
		}
	}

	public boolean keyPressed(KeyInput input) {
		this.searching = false;

		if (this.isOpen() && !this.client.player.isSpectator()) {
			if (input.isEscape() && !this.isWide()) {
				this.setOpen(false);
				return true;
			} else if (this.searchField.keyPressed(input)) {
				this.refreshSearchResults();
				return true;
			} else if (this.searchField.isFocused() && this.searchField.isVisible() && !input.isEscape()) {
				return true;
			} else if (this.client.options.chatKey.matchesKey(input) && !this.searchField.isFocused()) {
				this.searching = true;
				this.searchField.setFocused(true);
				return true;
			} else if (input.isEnterOrSpace() && this.selectedRecipeResults != null && this.selectedRecipe != null) {
				ClickableWidget.playClickSound(MinecraftClient.getInstance().getSoundManager());
				return this.select(this.selectedRecipeResults, this.selectedRecipe, input.hasShift());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean keyReleased(KeyInput input) {
		this.searching = false;

		return Element.super.keyReleased(input);
	}

	public boolean charTyped(CharInput input) {
		if (this.searching) {
			return false;
		} else if (this.isOpen() && !this.client.player.isSpectator()) {
			if (this.searchField.charTyped(input)) {
				this.refreshSearchResults();
				return true;
			} else {
				return Element.super.charTyped(input);
			}
		} else {
			return false;
		}
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}

	public void setFocused(boolean focused) {
	}

	public boolean isFocused() {
		return false;
	}

	private void refreshSearchResults() {
		String searchInput = this.searchField.getText().toLowerCase(Locale.ROOT);
		this.triggerPirateSpeakEasterEgg(searchInput);

		if (!searchInput.equals(this.searchText)) {
			this.refreshResults(false, this.isFilteringCraftable());
			this.searchText = searchInput;
		}

	}

	private void triggerPirateSpeakEasterEgg(String search) {
		if ("excitedze".equals(search)) {
			LanguageManager languageManager = this.client.getLanguageManager();
			String pirateLang = "en_pt";
			LanguageDefinition languageDefinition = languageManager.getLanguage(pirateLang);

			if (languageDefinition == null || languageManager.getLanguage().equals(pirateLang)) {
				return;
			}

			languageManager.setLanguage(pirateLang);
			this.client.options.language = pirateLang;
			this.client.reloadResources();
			this.client.options.write();
		}

	}

	private boolean isWide() {
		return this.leftOffset == this.getLeftOffset();
	}

	public void refresh() {
		this.populateAllRecipes();
		this.refreshTabButtons(this.isFilteringCraftable());

		if (this.isOpen()) {
			this.refreshResults(false, this.isFilteringCraftable());
		}

	}

	public void onRecipeDisplayed(NetworkRecipeId recipeId) {
		this.client.player.onRecipeDisplayed(recipeId);
	}

	public void onCraftFailed(RecipeDisplay display) {
		Objects.requireNonNull(this.client.world);

		this.ghostRecipe.clear();
		ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(this.client.world);
		this.showGhostRecipe(this.ghostRecipe, display, contextParameterMap);
	}

	protected abstract void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context);

	protected void sendBookDataPacket() {
		if (this.client.getNetworkHandler() != null) {
			net.minecraft.recipe.book.RecipeBookType recipeBookType = this.screenHandler.getCategory();
			boolean bl = this.recipeBook.getOptions().isGuiOpen(recipeBookType);
			boolean bl2 = this.recipeBook.getOptions().isFilteringCraftable(recipeBookType);
			this.client.getNetworkHandler().sendPacket(new RecipeCategoryOptionsC2SPacket(recipeBookType, bl, bl2));
		}
	}

	public Selectable.SelectionType getType() {
		return this.open ? SelectionType.HOVERED : SelectionType.NONE;
	}

	public void appendNarrations(NarrationMessageBuilder builder) {
		List<Selectable> list = Lists.newArrayList();
		this.recipesArea.forEachButton((button) -> {
			if (button.isInteractable()) {
				list.add(button);
			}

		});
		list.add(this.searchField);
		list.add(this.toggleCraftableButton);
		list.addAll(this.tabButtons);
		Screen.SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, (Selectable) null);
		if (selectedElementNarrationData != null) {
			selectedElementNarrationData.selectable().appendNarrations(builder.nextMessage());
		}
	}

	// ///////////// //
	// FINAL METHODS //
	// ///////////// //

	protected final int getCurrentLeftOffset() {
		return this.leftOffset;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	protected int getTabBtnWidth() {
		return 30;
	}

	protected Identifier getUITexture() {
		return TEXTURE;
	}

	protected Dimension getTextureSize() {
		return this.dimension;
	}

	/**
	 * Identifies the dimension of the actual texture to be used for the GUI.
	 * @return Dimension
	 */
	protected Dimension getUVSize() {
		return this.uvSize;
	}

	protected int getLeftOffset() {
		return 86;
	}

	protected int getMaxSearchLength() {
		return 50;
	}

	protected Text getSearchHintText() {
		return SEARCH_HINT_TEXT;
	}

	protected Text getToggleAllRecipesText() {
		return TOGGLE_ALL_RECIPES_TEXT;
	}

	protected TextFieldWidget buildNewSearchField(int width, int height, @Nullable Text searchText) {
		TextRenderer textRenderer = this.client.textRenderer;
		int x = this.getLeft() + 25;
		int y = this.getTop() + 13;

		return new TextFieldWidget(
			textRenderer,
			x, y, width, height,
			searchText == null ?
				Text.translatable("itemGroup.search") : searchText
		);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected abstract ButtonTextures getBookButtonTextures();

	protected abstract boolean isCraftingSlot(Slot slot);

	// ///////////// //
	// CUSTOM RECORD //
	// ///////////// //

	public record Tab(ItemStack primaryIcon, Optional<ItemStack> secondaryIcon,
	                  RecipeBookGroup category) {
		public Tab(ModRecipeBookType type) {
			this(new ItemStack(Items.COMPASS), Optional.empty(), type);
		}

		public Tab(Item primaryIcon, RecipeBookCategory category) {
			this(new ItemStack(primaryIcon), Optional.empty(), category);
		}

		public Tab(Item primaryIcon, Item secondaryIcon, RecipeBookCategory category) {
			this(new ItemStack(primaryIcon), Optional.of(new ItemStack(secondaryIcon)), category);
		}
	}
}
