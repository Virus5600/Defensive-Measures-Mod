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
import net.minecraft.client.gui.navigation.ScreenPosition;
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

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.gui.screen.TextSpriteButton;
import com.virus5600.defensive_measures.recipebook.ModRecipeBookCategory;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.awt.Dimension;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Base class for book-related widgets, mirroring the vanilla {@link net.minecraft.client.gui.screens.recipebook.RecipeBookComponent RecipeBookComponent}.
 * Implements the core interfaces for rendering, input handling, and selection.
 *
 * @param <T> the screen handler type
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.1.0-beta
 */
public abstract class RecipeBookComponent<T extends RecipeBookMenu> implements GuiEventListener, Renderable, NarratableEntry {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "textures/gui/container/recipe_book.png");
	private static final Component SEARCH_HINT_TEXT = Component.translatable("gui.recipebook.search_hint").withStyle(EditBox.SEARCH_HINT_STYLE);
	private static final Component TOGGLE_ALL_RECIPES_TEXT = Component.translatable("gui.recipebook.toggleRecipes.all");

	private final Dimension textureSize = new Dimension(256, 256);
	private final Dimension uvSize = new Dimension(147, 195);
	private final GhostSlots ghostRecipe;
	private final List<BaseRecipeBookTabButton> tabButtons = Lists.newArrayList();
	private final List<TabInfo> tabInfos;
	private final BaseRecipeBookPage recipeBookPage;
	private final StackedItemContents stackedContents = new StackedItemContents();
	protected final T menu;

	private @Nullable TextSpriteButton closeBtn;
	private @Nullable RecipeDisplayId lastPlacedRecipe;
	private @Nullable BaseRecipeBookTabButton selectedTab;
	private @Nullable EditBox searchBox;
	private @Nullable ScreenRectangle magnifierIconPlacement;
	private @Nullable RecipeDisplayId lastRecipe;
	private @Nullable RecipeCollection lastRecipeCollection;
	private Minecraft minecraft;
	private ClientRecipeBook book;
	private String lastSearch = "";
	private int timesInventoryChanged;
	private boolean ignoreTextInput;
	protected CycleButton<Boolean> filterButton;
	protected int parentWidth;
	protected int parentHeight;
	protected int xOffset;
	protected boolean visible;
	protected boolean widthTooNarrow;
	protected float displayTime;

	public RecipeBookComponent(T screenHandler, List<TabInfo> tabInfos) {
		this.menu = screenHandler;
		this.tabInfos = tabInfos;

		SlotSelectTime slotSelectTime = () -> Mth.floor(this.displayTime / 30.0F);

		this.ghostRecipe = new GhostSlots(slotSelectTime);
		this.recipeBookPage = new BaseRecipeBookPage(this, slotSelectTime);
	}

	public void init(int parentWidth, int parentHeight, Minecraft minecraft, boolean widthTooNarrow) {
		Objects.requireNonNull(minecraft.player, "Player cannot be null");

		this.minecraft = minecraft;
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		this.widthTooNarrow = widthTooNarrow;
		this.book = minecraft.player.getRecipeBook();
		this.timesInventoryChanged = minecraft.player.getInventory().getTimesChanged();
		this.visible = this.isVisibleAccordingToBookData();

		if (this.visible) {
			this.initVisuals();
		}
	}

	protected void initVisuals() {
		if (this.minecraft.player == null) {
			return;
		}

		boolean isFiltering = this.isFiltering();
		this.xOffset = this.widthTooNarrow ? 0 : this.getXOffset();
		int xOrigin = this.getXOrigin();
		int yOrigin = this.getYOrigin();

		this.stackedContents.clear();
		this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);

		this.menu.fillCraftSlotsStackedContents(this.stackedContents);

		String oldEdit = this.searchBox != null ? this.searchBox.getValue() : "";
		this.searchBox = this.buildNewSearchField(81, 9 + 5, null);

		this.searchBox.setMaxLength(this.getMaxSearchLength());
		this.searchBox.setVisible(true);
		this.searchBox.setTextColor(-1);
		this.searchBox.setValue(oldEdit);
		this.searchBox.setHint(this.getSearchHintText());
		this.magnifierIconPlacement = ScreenRectangle.of(
			ScreenAxis.HORIZONTAL,
			xOrigin + 8, this.searchBox.getY(),
			this.searchBox.getX() - this.getXOrigin(),
			this.searchBox.getHeight()
		);

		this.recipeBookPage.init(this.minecraft, xOrigin, yOrigin);

		this.filterButton = CycleButton.booleanBuilder(
				this.getRecipeFilterName(),
				this.getToggleAllRecipesText(),
				isFiltering
			)
			.withTooltip((filtering) -> filtering ?
				Tooltip.create(this.getRecipeFilterName()) :
				Tooltip.create(this.getToggleAllRecipesText())
			)
			.withSprite((cycleBtn, filtering) ->
				this.getFilterButtonTextures()
					.get(filtering, cycleBtn.isHoveredOrFocused())
			)
			.displayState(CycleButton.DisplayState.HIDE)
			.create(xOrigin + 110, yOrigin + 12, 26, 16,
				CommonComponents.EMPTY,
				(_, value) -> {
					this.toggleFiltering();
					this.sendUpdateSettings();
					this.updateCollections(false, value);
				}
			);
		this.tabButtons.clear();

		for (TabInfo tabInfo : this.tabInfos) {
			BaseRecipeBookTabButton btn = new BaseRecipeBookTabButton(0, 0, tabInfo, this::onTabButtonPress);

			if (tabInfo.category() instanceof ExtendedRecipeBookCategory
				&& !(tabInfo.category() instanceof RecipeBookCategory)
			) {
				btn.setTooltip(Tooltip.create(Component.translatable("gui.recipebook.all")));
			} else {
				btn.setTooltip(Tooltip.create(Component.translatable("gui.recipebook." + tabInfo.langGuiKey())));
			}

			this.tabButtons.add(btn);
		}

		// Try to find the same tab as before, since the recipe book doesn't track the current tab.
		if (this.selectedTab != null) {
			this.selectedTab = this.tabButtons.stream()
				.filter((button) -> button.getCategory().equals(this.selectedTab.getCategory()))
				.findFirst()
				.orElse(null);
		}

		// Sets the selected tab. Use the first if none.
		if (this.selectedTab == null) {
			this.selectedTab = this.tabButtons.getFirst();
		}

		this.selectedTab.select();

		this.selectMatchingRecipes();
		this.updateTabs(isFiltering);
		this.updateCollections(false, isFiltering);

		this.closeBtn = null;
		if (this.widthTooNarrow) {
			this.initCloseButton();
		}
	}

	protected final void initCloseButton() {
		int width = 147, height = 20;
		ScreenPosition screenPos = new ScreenPosition(
			this.getXOrigin() + this.getUVSize().width - width,
			this.getYOrigin() + this.getUVSize().height - 1
		);

		TextSpriteButton btn = new TextSpriteButton(
			screenPos.x(), screenPos.y(), width, height,
			Component.translatable("gui.recipebook.close"),
			this.minecraft.font, getCloseButtonTexture(),
			this::closeBook
		);

		btn.setTooltip(Tooltip.create(Component.translatable("gui.recipebook.close")));

		this.closeBtn = btn;
	}

	private void closeBook(Button btn) {
		this.setVisible(false);
	}

	private int getYOrigin() {
		return (this.parentHeight - this.uvSize.height) / 2;
	}

	private int getXOrigin() {
		return (this.parentWidth - this.uvSize.width) / 2 - this.xOffset;
	}

	public int updateScreenPosition(int width, int imageWidth) {
		int leftPos;
		if (this.isVisible() && !this.widthTooNarrow) {
			leftPos = 170 + (width - imageWidth - this.uvSize.width) / 2;
		} else {
			leftPos = (width - imageWidth) / 2;
		}

		return leftPos;
	}

	public void toggleVisibility() {
		this.setVisible(!this.isVisible());
	}

	public boolean isVisible() {
		return this.visible;
	}

	private boolean isVisibleAccordingToBookData() {
		return this.book.isOpen(this.menu.getRecipeBookType());
	}

	protected void setVisible(boolean visible) {
		if (visible) {
			this.initVisuals();
		}

		this.visible = visible;
		this.book.setOpen(this.menu.getRecipeBookType(), visible);
		if (!visible) {
			this.recipeBookPage.hideAlternates();
		}

		this.sendUpdateSettings();
	}

	public void slotClicked(@Nullable Slot slot) {
		if (slot != null && this.isCraftingSlot(slot)) {
			this.lastPlacedRecipe = null;
			this.ghostRecipe.clear();
			if (this.isVisible()) {
				this.updateStackedContents();
			}
		}
	}

	public int selectMatchingRecipes() {
		int matchedRecipes = 0;

		for (TabInfo tab : this.tabInfos) {
			ExtendedRecipeBookCategory category = tab.category();

			if (category instanceof ModRecipeBookCategory modRecipeBookType) {
				for (RecipeBookCategory recipeCategory : modRecipeBookType.includedCategories()) {
					for (RecipeCollection recipeResultCollection : this.book.getCollection(recipeCategory)) {
						this.selectMatchingRecipes(recipeResultCollection, this.stackedContents);
						matchedRecipes++;
					}
				}
			} else {
				for (RecipeCollection recipeResultCollection : this.book.getCollection(category)) {
					this.selectMatchingRecipes(recipeResultCollection, this.stackedContents);
					matchedRecipes++;
				}
			}
		}

		return matchedRecipes;
	}

	private void updateCollections(boolean resetPage, boolean isFiltering) {
		if (this.selectedTab == null) {
			return;
		}

		ExtendedRecipeBookCategory category = this.selectedTab.getCategory();
		List<RecipeCollection> tabCollection = this.book.getCollection(category);
		List<RecipeCollection> collection = Lists.newArrayList(tabCollection);
		collection.removeIf((resultCollection) -> !resultCollection.hasAnySelected());

		// Handles mod related categories...
		if (category instanceof ModRecipeBookCategory modRecipeBookType) {
			for (RecipeBookCategory recipeCategory : modRecipeBookType.includedCategories()) {
				collection.addAll(this.book.getCollection(recipeCategory));
			}
		}

		String searchTxt = this.searchBox == null ? "" : this.searchBox.getValue();
		if (!searchTxt.isEmpty()) {
			ClientPacketListener connection = this.minecraft.getConnection();

			if (connection != null) {
				ObjectSet<RecipeCollection> set = new ObjectLinkedOpenHashSet<>(
					connection.searchTrees()
						.recipes()
						.search(searchTxt.toLowerCase(Locale.ROOT))
				);

				collection.removeIf(
					(resultCollection) -> !set.contains(resultCollection)
				);
			}
		}

		if (isFiltering) {
			collection.removeIf((resultCollection) -> !resultCollection.hasCraftable());
		}

		this.recipeBookPage.updateCollections(collection, resetPage, isFiltering);
	}

	private void updateTabs(boolean filteringCraftable) {
		int xPosTab = (this.parentWidth - this.uvSize.width) / 2 - this.xOffset - 30;
		int yPosTab = (this.parentHeight - this.uvSize.height) / 2 + 3;
		int yOffset = 27;
		int index = 0;

		for (BaseRecipeBookTabButton tabBtn : this.tabButtons) {
			ExtendedRecipeBookCategory category = tabBtn.getCategory();

			if (category instanceof ModRecipeBookCategory) {
				tabBtn.visible = true;
				tabBtn.setPosition(xPosTab, yPosTab + yOffset * index++);
			}
			else if (tabBtn.updateVisibility(this.book)) {
				tabBtn.setPosition(xPosTab, yPosTab + yOffset * index++);
				tabBtn.startAnimation(this.book, filteringCraftable);
			}
		}
	}

	public int getTimesInventoryChanged() {
		return this.timesInventoryChanged;
	}

	public void tick() {
		boolean shouldBeVisible = this.isVisibleAccordingToBookData();
		if (this.isVisible() != shouldBeVisible) {
			this.setVisible(shouldBeVisible);
		}

		if (this.isVisible() && this.minecraft.player != null) {
			int timesChanged = this.minecraft.player.getInventory().getTimesChanged();

			if (this.timesInventoryChanged != timesChanged) {
				this.updateStackedContents();
				this.timesInventoryChanged = timesChanged;
			}
		}
	}

	private void updateStackedContents() {
		if (this.minecraft.player != null) {
			this.stackedContents.clear();
			this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
			this.menu.fillCraftSlotsStackedContents(this.stackedContents);
			this.selectMatchingRecipes();
			this.updateCollections(false, this.isFiltering());
		}
	}

	private boolean isFiltering() {
		return this.book.isFiltering(this.menu.getRecipeBookType());
	}

	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.isVisible()) {
			if (!this.minecraft.hasControlDown()) {
				this.displayTime += deltaTicks;
			}

			int xOrigin = this.getXOrigin();
			int yOrigin = this.getYOrigin();

			Dimension uvDimension = this.uvSize;
			Dimension textureDimension = this.getTextureSize();

			graphics.blit(
				RenderPipelines.GUI_TEXTURED, this.getUITexture(),
				xOrigin, yOrigin, 1.0F, 1.0F,
				uvDimension.width, uvDimension.height,
				textureDimension.width, textureDimension.height
			);

			if (this.searchBox != null) {
				this.searchBox.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			}

			for (BaseRecipeBookTabButton tabButton : this.tabButtons) {
				tabButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			}

			if (this.closeBtn != null) {
				this.closeBtn.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			}

			this.filterButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			this.recipeBookPage.extractRenderState(graphics, xOrigin, yOrigin, mouseX, mouseY, deltaTicks);
		}
	}

	public void extractTooltip(GuiGraphicsExtractor graphics, int x, int y, @Nullable Slot hoveredSlot) {
		if (this.isVisible()) {
			this.recipeBookPage.extractTooltip(graphics, x, y);
			this.ghostRecipe.extractTooltip(graphics, this.minecraft, x, y, hoveredSlot);
		}
	}

	protected abstract Component getRecipeFilterName();

	public void extractGhostRecipe(GuiGraphicsExtractor graphics, boolean isResultSlotBig) {
		this.ghostRecipe.extractRenderState(graphics, this.minecraft, isResultSlotBig);
	}

	public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
		if (this.isVisible() && this.minecraft.player != null && !this.minecraft.player.isSpectator()) {
			Dimension uvDimension = this.uvSize;
			boolean recipesAreaClicked = this.recipeBookPage
				.mouseClicked(event,
					this.getXOrigin(), this.getYOrigin(),
					uvDimension.width, uvDimension.height,
					doubleClick);

			if (recipesAreaClicked) {
				RecipeDisplayId recipe = this.recipeBookPage.getLastClickedRecipe();
				RecipeCollection recipeCollection = this.recipeBookPage.getLastClickedRecipeCollection();
				if (recipe != null && recipeCollection != null) {
					if (!this.tryPlaceRecipe(recipeCollection, recipe, event.hasShiftDown())) {
						return false;
					}

					this.lastRecipeCollection = recipeCollection;
					this.lastRecipe = recipe;

					if (!this.isOffsetNextToMainGUI()) {
						this.setVisible(false);
					}
				}

				return true;
			} else {
				if (this.closeBtn != null && this.closeBtn.mouseClicked(event, doubleClick)) {
					return true;
				}

				if (this.searchBox != null) {
					boolean clickedMagnifierIcon = this.magnifierIconPlacement != null && this.magnifierIconPlacement.containsPoint(Mth.floor(event.x()), Mth.floor(event.y()));
					if (clickedMagnifierIcon || this.searchBox.mouseClicked(event, doubleClick)) {
						this.searchBox.setFocused(true);
						return true;
					}

					this.searchBox.setFocused(false);
				}

				if (this.filterButton.mouseClicked(event, doubleClick)) {
					return true;
				} else {
					for (BaseRecipeBookTabButton tabButton : this.tabButtons) {
						if (tabButton.mouseClicked(event, doubleClick)) {
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

	public boolean mouseDragged(MouseButtonEvent event, double offsetX, double offsetY) {
		return this.searchBox != null && this.searchBox.isFocused() && this.searchBox.mouseDragged(event, offsetX, offsetY);
	}

	private boolean tryPlaceRecipe(RecipeCollection recipeCollection, RecipeDisplayId recipe, boolean useMaxItems) {
		if ((!recipeCollection.isCraftable(recipe) && recipe.equals(this.lastPlacedRecipe))
			|| this.minecraft.gameMode == null || this.minecraft.player == null
		) {
			return false;
		} else {
			this.lastPlacedRecipe = recipe;
			this.ghostRecipe.clear();
			this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, recipe, useMaxItems);
			return true;
		}
	}

	private void onTabButtonPress(Button button) {
		if (this.selectedTab != button && button instanceof BaseRecipeBookTabButton recipeBookTabButton) {
			this.replaceSelected(recipeBookTabButton);
			this.updateCollections(true, this.isFiltering());
		}

	}

	private void replaceSelected(BaseRecipeBookTabButton tabButton) {
		if (this.selectedTab != null) {
			this.selectedTab.unselect();
		}

		tabButton.select();
		this.selectedTab = tabButton;
	}

	private void toggleFiltering() {
		RecipeBookType type = this.menu.getRecipeBookType();
		boolean newSetting = !this.book.isFiltering(type);
		this.book.setFiltering(type, newSetting);
	}

	public boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int imageWidth, int imageHeight) {
		if (!this.isVisible()) {
			return true;
		} else {
			boolean clickedOutside = mouseX < (double) x ||
				mouseY < (double) y ||
				mouseX >= (double) (x + imageWidth) ||
				mouseY >= (double) (y + imageHeight);

			boolean clickedOnRecipeBook = (double) (x - this.uvSize.width) < mouseX &&
				mouseX < (double) x &&
				(double) y < mouseY &&
				mouseY < (double) (y + imageHeight);

			return clickedOutside && !clickedOnRecipeBook && this.selectedTab != null && !this.selectedTab.isHoveredOrFocused();
		}
	}

	public boolean keyPressed(KeyEvent event) {
		this.ignoreTextInput = false;

		if (this.isVisible() && this.minecraft.player != null && !this.minecraft.player.isSpectator()) {
			if (event.isEscape() && !this.isOffsetNextToMainGUI()) {
				this.setVisible(false);
				return true;
			} else if (this.searchBox != null && this.searchBox.keyPressed(event)) {
				this.checkSearchStringUpdate();
				return true;
			} else if (this.searchBox != null && this.searchBox.isFocused() && this.searchBox.isVisible() && !event.isEscape()) {
				return true;
			} else if (this.minecraft.options.keyChat.matches(event) && !this.searchBox.isFocused()) {
				this.ignoreTextInput = true;
				this.searchBox.setFocused(true);
				return true;
			} else if (event.isSelection() && this.lastRecipeCollection != null && this.lastRecipe != null) {
				AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
				return this.tryPlaceRecipe(this.lastRecipeCollection, this.lastRecipe, event.hasShiftDown());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean keyReleased(KeyEvent event) {
		this.ignoreTextInput = false;

		return GuiEventListener.super.keyReleased(event);
	}

	public boolean charTyped(CharacterEvent event) {
		if (this.ignoreTextInput) {
			return false;
		} else if (this.isVisible() && this.minecraft.player != null && !this.minecraft.player.isSpectator()) {
			if (this.searchBox != null && this.searchBox.charTyped(event)) {
				this.checkSearchStringUpdate();
				return true;
			} else {
				return GuiEventListener.super.charTyped(event);
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

	private void checkSearchStringUpdate() {
		String searchInput = this.searchBox == null ? "" : this.searchBox.getValue().toLowerCase(Locale.ROOT);
		this.pirateSpeechForThePeople(searchInput);

		if (!searchInput.equals(this.lastSearch)) {
			this.updateCollections(false, this.isFiltering());
			this.lastSearch = searchInput;
		}

	}

	private void pirateSpeechForThePeople(String search) {
		if ("excitedze".equals(search)) {
			LanguageManager languageManager = this.minecraft.getLanguageManager();
			String pirateLang = "en_pt";
			LanguageInfo languageDefinition = languageManager.getLanguage(pirateLang);

			if (languageDefinition == null || languageManager.getSelected().equals(pirateLang)) {
				return;
			}

			languageManager.setSelected(pirateLang);
			this.minecraft.options.languageCode = pirateLang;
			this.minecraft.reloadResourcePacks();
			this.minecraft.options.save();
		}

	}

	private boolean isOffsetNextToMainGUI() {
		return this.xOffset == this.getXOffset();
	}

	public void recipesUpdated() {
		this.selectMatchingRecipes();
		this.updateTabs(this.isFiltering());

		if (this.isVisible()) {
			this.updateCollections(false, this.isFiltering());
		}

	}

	public void recipeShown(RecipeDisplayId recipe) {
		if (this.minecraft.player != null) {
			this.minecraft.player.removeRecipeHighlight(recipe);
		}
	}

	public void fillGhostRecipe(RecipeDisplay recipe) {
		Objects.requireNonNull(this.minecraft.level);

		this.ghostRecipe.clear();
		ContextMap context = SlotDisplayContext.fromLevel(this.minecraft.level);
		this.fillGhostRecipe(this.ghostRecipe, recipe, context);
	}

	/**
	 * Determines if a recipe display can be shown in the recipe book.
	 *
	 * @param display The recipe display to check.
	 *
	 * @return {@code true} if the display can be shown, {@code false} otherwise.
	 *
	 * @implNote Update this method if a {@link #fillGhostRecipe(GhostSlots, RecipeDisplay, ContextMap)}
	 * handles a new {@link RecipeDisplay}.
	 */
	protected abstract boolean canDisplay(RecipeDisplay display);

	/**
	 * Fills the crafting grid input slots with ghost items to show the recipe.
	 *
	 * @param ghostSlots The {@link GhostSlots} instance to fill with ghost items.
	 * @param display    The {@link RecipeDisplay} to display in the crafting grid.
	 * @param context    The {@link ContextMap} to use for the ghost slots.
	 *
	 * @implNote Update this method when the {@link #canDisplay(RecipeDisplay)} to also display the
	 * accepted display from said method.
	 */
	protected abstract void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context);

	protected void sendUpdateSettings() {
		if (this.minecraft.getConnection() != null) {
			RecipeBookType type = this.menu.getRecipeBookType();
			boolean open = this.book.getBookSettings().isOpen(type);
			boolean filtering = this.book.getBookSettings().isFiltering(type);
			this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(type, open, filtering));
		}
	}

	public NarratableEntry.NarrationPriority narrationPriority() {
		return this.visible ? NarrationPriority.HOVERED : NarrationPriority.NONE;
	}

	public void updateNarration(NarrationElementOutput output) {
		List<NarratableEntry> narratableEntries = Lists.newArrayList();
		this.recipeBookPage.forEachButton((button) -> {
			if (button.isActive()) {
				narratableEntries.add(button);
			}

		});
		narratableEntries.add(this.searchBox);
		narratableEntries.add(this.filterButton);
		narratableEntries.addAll(this.tabButtons);
		Screen.NarratableSearchResult narratable = Screen.findNarratableWidget(narratableEntries, null);
		if (narratable != null) {
			narratable.entry().updateNarration(output.nest());
		}
	}

	protected static WidgetSprites getCloseButtonTexture() {
		return new WidgetSprites(
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "widget/text_sprite_button"),
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "widget/text_sprite_button_highlighted")
		);
	}

	// ///////////// //
	// FINAL METHODS //
	// ///////////// //

	protected final int getCurrentLeftOffset() {
		return this.xOffset;
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
		return this.textureSize;
	}

	/**
	 * Identifies the dimension of the actual texture to be used for the GUI.
	 * @return Dimension
	 */
	protected Dimension getUVSize() {
		return this.uvSize;
	}


	protected int getXOffset() {
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
		Font textRenderer = this.minecraft.font;
		int x = this.getXOrigin() + 25;
		int y = this.getYOrigin() + 13;

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

	protected abstract void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents);

	protected abstract WidgetSprites getFilterButtonTextures();

	protected abstract boolean isCraftingSlot(Slot slot);

	// ///////////// //
	// CUSTOM RECORD //
	// ///////////// //

	public record TabInfo(
		String langGuiKey,
		ItemStack primaryIcon,
		Optional<ItemStack> secondaryIcon,
		ExtendedRecipeBookCategory category
	) {
		public TabInfo(ModRecipeBookCategory type) {
			this("all", new ItemStack(Items.COMPASS), Optional.empty(), type);
		}

		public TabInfo(String langGuiKey, Item primaryIcon, RecipeBookCategory category) {
			this(langGuiKey, new ItemStack(primaryIcon), Optional.empty(), category);
		}

		public TabInfo(String langGuiKey, Item primaryIcon, Item secondaryIcon, RecipeBookCategory category) {
			this(langGuiKey, new ItemStack(primaryIcon), Optional.of(new ItemStack(secondaryIcon)), category);
		}
	}
}
