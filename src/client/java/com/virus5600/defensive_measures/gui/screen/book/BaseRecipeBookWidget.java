package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

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
 * Base class for book-related widgets, mirroring the vanilla {@link net.minecraft.client.gui.screens.recipebook.RecipeBookComponent}.
 * Implements the core interfaces for rendering, input handling, and selection.
 *
 * @param <T> the screen handler type
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.0.0-beta
 */
public abstract class BaseRecipeBookWidget<T extends RecipeBookMenu> implements Renderable, GuiEventListener, NarratableEntry {
	private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/recipe_book.png");
	private static final Component SEARCH_HINT_TEXT = Component.translatable("gui.recipebook.search_hint").withStyle(EditBox.SEARCH_HINT_STYLE);
	private static final Component TOGGLE_ALL_RECIPES_TEXT = Component.translatable("gui.recipebook.toggleRecipes.all");

	private final Dimension dimension = new Dimension(256, 256);
	private final Dimension uvSize = new Dimension(147, 166);
	private final GhostSlots ghostRecipe;
	private final List<BaseRecipeGroupButtonWidget> tabButtons = Lists.newArrayList();
	private final List<Tab> tabs;
	private final BaseRecipeBookResults recipesArea;
	private final StackedItemContents recipeFinder = new StackedItemContents();
	protected final T screenHandler;

	private @Nullable RecipeDisplayId selectedRecipeId;
	private @Nullable BaseRecipeGroupButtonWidget currentTab;
	private @Nullable EditBox searchField;
	private @Nullable ScreenRectangle searchFieldRect;
	private @Nullable RecipeDisplayId selectedRecipe;
	private @Nullable RecipeCollection selectedRecipeResults;
	private Minecraft client;
	private ClientRecipeBook recipeBook;
	private String searchText = "";
	private int cachedInvChangeCount;
	private boolean searching;
	protected CycleButton<Boolean> toggleCraftableButton;
	protected int parentWidth;
	protected int parentHeight;
	protected int leftOffset;
	protected boolean open;
	protected boolean narrow;
	protected float displayTime;

	public BaseRecipeBookWidget(T screenHandler, List<Tab> tabs) {
		this.screenHandler = screenHandler;
		this.tabs = tabs;

		SlotSelectTime cip = () -> Mth.floor(this.displayTime / 30.0F);

		this.ghostRecipe = new GhostSlots(cip);
		this.recipesArea = new BaseRecipeBookResults(this, cip);
	}

	public void initialize(int parentWidth, int parentHeight, Minecraft client, boolean narrow) {
		this.client = client;
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		this.narrow = narrow;
		this.recipeBook = client.player.getRecipeBook();
		this.cachedInvChangeCount = client.player.getInventory().getTimesChanged();
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
		this.client.player.getInventory().fillStackedContents(this.recipeFinder);

		this.screenHandler.fillCraftSlotsStackedContents(this.recipeFinder);

		String searchFldTxt = this.searchField != null ? this.searchField.getValue() : "";
		this.searchField = this.buildNewSearchField(81, 9 + 5, null);
		this.searchField.setMaxLength(this.getMaxSearchLength());
		this.searchField.setVisible(true);
		this.searchField.setTextColor(-1);
		this.searchField.setValue(searchFldTxt);
		this.searchField.setHint(this.getSearchHintText());
		this.searchFieldRect = ScreenRectangle.of(ScreenAxis.HORIZONTAL, x + 8, this.searchField.getY(), this.searchField.getX() - this.getLeft(), this.searchField.getHeight());

		this.recipesArea.initialize(this.client, x, y);

		this.toggleCraftableButton = CycleButton.booleanBuilder(
				this.getToggleCraftableButtonText(),
				this.getToggleAllRecipesText(),
				isFilteringCraftable
			)
			.withTooltip((value) -> value ?
				Tooltip.create(this.getToggleCraftableButtonText()) :
				Tooltip.create(this.getToggleAllRecipesText())
			)
			.withSprite((button, value) ->
				this.getBookButtonTextures()
					.get(value, button.isHoveredOrFocused())
			)
			.displayState(CycleButton.DisplayState.HIDE)
			.create(x + 110, y + 12, 26, 16,
				CommonComponents.EMPTY,
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

		if (this.currentTab != null) {
			this.currentTab.focus();
		}

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
		return this.recipeBook.isOpen(this.screenHandler.getRecipeBookType());
	}

	protected void setOpen(boolean opened) {
		if (opened) {
			this.reset();
		}

		this.open = opened;
		this.recipeBook.setOpen(this.screenHandler.getRecipeBookType(), opened);
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
			ExtendedRecipeBookCategory category = tab.category();

			if (category instanceof ModRecipeBookType modRecipeBookType) {
				for (RecipeBookCategory recipeCategory : modRecipeBookType.getCategories()) {
					for (RecipeCollection recipeResultCollection : this.recipeBook.getCollection(recipeCategory)) {
						this.populateRecipes(recipeResultCollection, this.recipeFinder);
					}
				}
			} else {
				for (RecipeCollection recipeResultCollection : this.recipeBook.getCollection(category)) {
					this.populateRecipes(recipeResultCollection, this.recipeFinder);
				}
			}
		}
	}

	protected abstract void populateRecipes(RecipeCollection recipeResultCollection, StackedItemContents recipeFinder);

	private void refreshResults(boolean resetCurrentPage, boolean filteringCraftable) {
		List<RecipeCollection> categoryResults = Lists.newArrayList();

		// Handle ModRecipeBookType (All tab) specially to include all categories
		ExtendedRecipeBookCategory category = this.currentTab.getCategory();
		if (category instanceof ModRecipeBookType modRecipeBookType) {
			for (RecipeBookCategory recipeCategory : modRecipeBookType.getCategories()) {
				categoryResults.addAll(this.recipeBook.getCollection(recipeCategory));
			}
		} else {
			categoryResults.addAll(this.recipeBook.getCollection(category));
		}

		List<RecipeCollection> categoryResultsDupe = Lists.newArrayList(categoryResults);
		categoryResultsDupe.removeIf((resultCollection) -> !resultCollection.hasAnySelected());

		String searchTxt = this.searchField.getValue();
		if (!searchTxt.isEmpty()) {
			ClientPacketListener clientPNH = this.client.getConnection();

			if (clientPNH != null) {
				ObjectSet<RecipeCollection> objectSet = new ObjectLinkedOpenHashSet<>(
					clientPNH.searchTrees()
						.recipes()
						.search(searchTxt.toLowerCase(Locale.ROOT))
				);

				categoryResultsDupe.removeIf(
					(resultCollection) -> !objectSet.contains(resultCollection)
				);
			}
		}

		if (filteringCraftable) {
			categoryResultsDupe.removeIf((resultCollection) -> !resultCollection.hasCraftable());
		}

		this.recipesArea.setResults(categoryResultsDupe, resetCurrentPage, filteringCraftable);
	}

	private void refreshTabButtons(boolean filteringCraftable) {
		int i = (this.parentWidth - this.getUVSize().width) / 2 - this.leftOffset - 30;
		int j = (this.parentHeight - this.getUVSize().height) / 2 + 3;
		int k = 27;
		int l = 0;

		for (BaseRecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
			ExtendedRecipeBookCategory recipeBookGroup = recipeGroupButtonWidget.getCategory();

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
			if (this.cachedInvChangeCount != this.client.player.getInventory().getTimesChanged()) {
				this.refreshInputs();
				this.cachedInvChangeCount = this.client.player.getInventory().getTimesChanged();
			}
		}
	}

	private void refreshInputs() {
		this.recipeFinder.clear();
		this.client.player.getInventory().fillStackedContents(this.recipeFinder);
		this.screenHandler.fillCraftSlotsStackedContents(this.recipeFinder);
		this.populateAllRecipes();
		this.refreshResults(false, this.isFilteringCraftable());
	}

	private boolean isFilteringCraftable() {
		return this.recipeBook.isFiltering(this.screenHandler.getRecipeBookType());
	}

	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
		if (this.isOpen()) {
			if (!this.client.hasControlDown()) {
				this.displayTime += deltaTicks;
			}

			int i = this.getLeft();
			int j = this.getTop();

			Dimension uvDimension = this.getUVSize();
			Dimension textureDimension = this.getTextureSize();

			context.blit(
				RenderPipelines.GUI_TEXTURED, this.getUITexture(),
				i, j, 1.0F, 1.0F,
				uvDimension.width, uvDimension.height,
				textureDimension.width, textureDimension.height
			);

			this.searchField.extractWidgetRenderState(context, mouseX, mouseY, deltaTicks);

			for (BaseRecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
				recipeGroupButtonWidget.extractContents(context, mouseX, mouseY, deltaTicks);
			}

			this.toggleCraftableButton.extractRenderState(context, mouseX, mouseY, deltaTicks);
			this.recipesArea.draw(context, i, j, mouseX, mouseY, deltaTicks);
		}
	}

	public void drawTooltip(GuiGraphicsExtractor context, int x, int y, @Nullable Slot slot) {
		if (this.isOpen()) {
			this.recipesArea.drawTooltip(context, x, y);
			this.ghostRecipe.extractTooltip(context, this.client, x, y, slot);
		}
	}

	protected abstract Component getToggleCraftableButtonText();

	public void drawGhostSlots(GuiGraphicsExtractor context, boolean resultHasPadding) {
		this.ghostRecipe.extractRenderState(context, this.client, resultHasPadding);
	}

	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		if (this.isOpen() && !this.client.player.isSpectator()) {
			Dimension uvDimension = this.getUVSize();
			boolean recipesAreaClicked = this.recipesArea
				.mouseClicked(click,
					this.getLeft(), this.getTop(),
					uvDimension.width, uvDimension.height,
					doubled);

			if (recipesAreaClicked) {
				RecipeDisplayId networkRecipeId = this.recipesArea.getLastClickedRecipe();
				RecipeCollection recipeResultCollection = this.recipesArea.getLastClickedResults();
				if (networkRecipeId != null && recipeResultCollection != null) {
					if (!this.select(recipeResultCollection, networkRecipeId, click.hasShiftDown())) {
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
					boolean isClicked = this.searchFieldRect != null && this.searchFieldRect.containsPoint(Mth.floor(click.x()), Mth.floor(click.y()));
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

	public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
		return this.searchField != null && this.searchField.isFocused() && this.searchField.mouseDragged(click, offsetX, offsetY);
	}

	private boolean select(RecipeCollection results, RecipeDisplayId recipeId, boolean craftAll) {
		if (!results.isCraftable(recipeId) && recipeId.equals(this.selectedRecipeId)) {
			return false;
		} else {
			this.selectedRecipeId = recipeId;
			this.ghostRecipe.clear();
			this.client.gameMode.handlePlaceRecipe(this.client.player.containerMenu.containerId, recipeId, craftAll);
			return true;
		}
	}

	private void onTabSelected(Button button) {
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
		RecipeBookType recipeBookType = this.screenHandler.getRecipeBookType();
		boolean bl = !this.recipeBook.isFiltering(recipeBookType);
		this.recipeBook.setFiltering(recipeBookType, bl);
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

			return isOutsideBackground && !isInsideBackground && !this.currentTab.isHoveredOrFocused();
		}
	}

	public boolean keyPressed(KeyEvent input) {
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
			} else if (this.client.options.keyChat.matches(input) && !this.searchField.isFocused()) {
				this.searching = true;
				this.searchField.setFocused(true);
				return true;
			} else if (input.isSelection() && this.selectedRecipeResults != null && this.selectedRecipe != null) {
				AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
				return this.select(this.selectedRecipeResults, this.selectedRecipe, input.hasShiftDown());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean keyReleased(KeyEvent input) {
		this.searching = false;

		return GuiEventListener.super.keyReleased(input);
	}

	public boolean charTyped(CharacterEvent input) {
		if (this.searching) {
			return false;
		} else if (this.isOpen() && !this.client.player.isSpectator()) {
			if (this.searchField.charTyped(input)) {
				this.refreshSearchResults();
				return true;
			} else {
				return GuiEventListener.super.charTyped(input);
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
		String searchInput = this.searchField.getValue().toLowerCase(Locale.ROOT);
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
			LanguageInfo languageDefinition = languageManager.getLanguage(pirateLang);

			if (languageDefinition == null || languageManager.getSelected().equals(pirateLang)) {
				return;
			}

			languageManager.setSelected(pirateLang);
			this.client.options.languageCode = pirateLang;
			this.client.reloadResourcePacks();
			this.client.options.save();
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

	public void onRecipeDisplayed(RecipeDisplayId recipeId) {
		this.client.player.removeRecipeHighlight(recipeId);
	}

	public void onCraftFailed(RecipeDisplay display) {
		Objects.requireNonNull(this.client.level);

		this.ghostRecipe.clear();
		ContextMap contextParameterMap = SlotDisplayContext.fromLevel(this.client.level);
		this.showGhostRecipe(this.ghostRecipe, display, contextParameterMap);
	}

	protected abstract void showGhostRecipe(GhostSlots ghostRecipe, RecipeDisplay display, ContextMap context);

	protected void sendBookDataPacket() {
		if (this.client.getConnection() != null) {
			net.minecraft.world.inventory.RecipeBookType recipeBookType = this.screenHandler.getRecipeBookType();
			boolean bl = this.recipeBook.getBookSettings().isOpen(recipeBookType);
			boolean bl2 = this.recipeBook.getBookSettings().isFiltering(recipeBookType);
			this.client.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(recipeBookType, bl, bl2));
		}
	}

	public NarratableEntry.NarrationPriority narrationPriority() {
		return this.open ? NarrationPriority.HOVERED : NarrationPriority.NONE;
	}

	public void updateNarration(NarrationElementOutput builder) {
		List<NarratableEntry> list = Lists.newArrayList();
		this.recipesArea.forEachButton((button) -> {
			if (button.isActive()) {
				list.add(button);
			}

		});
		list.add(this.searchField);
		list.add(this.toggleCraftableButton);
		list.addAll(this.tabButtons);
		Screen.NarratableSearchResult selectedElementNarrationData = Screen.findNarratableWidget(list, (NarratableEntry) null);
		if (selectedElementNarrationData != null) {
			selectedElementNarrationData.entry().updateNarration(builder.nest());
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

	protected Component getSearchHintText() {
		return SEARCH_HINT_TEXT;
	}

	protected Component getToggleAllRecipesText() {
		return TOGGLE_ALL_RECIPES_TEXT;
	}

	protected EditBox buildNewSearchField(int width, int height, @Nullable Component searchText) {
		Font textRenderer = this.client.font;
		int x = this.getLeft() + 25;
		int y = this.getTop() + 13;

		return new EditBox(
			textRenderer,
			x, y, width, height,
			searchText == null ?
				Component.translatable("itemGroup.search") : searchText
		);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected abstract WidgetSprites getBookButtonTextures();

	protected abstract boolean isCraftingSlot(Slot slot);

	// ///////////// //
	// CUSTOM RECORD //
	// ///////////// //

	public record Tab(ItemStack primaryIcon, Optional<ItemStack> secondaryIcon,
                      ExtendedRecipeBookCategory category) {
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
